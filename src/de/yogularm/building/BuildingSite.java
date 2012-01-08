package de.yogularm.building;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;

import de.yogularm.components.Body;
import de.yogularm.components.Component;
import de.yogularm.components.ComponentCollection;
import de.yogularm.geometry.Point;

/**
 * Every grid position can have the following flags:
 * 
 *  - blocked (by solid body)
 *  - taken (by any component)
 *  - keep free
 *  - safe (player can stay here)
 * 
 * @author Jan
 *
 */
public class BuildingSite {
	@SuppressWarnings("serial")
  private class FlagMap extends HashMap<Point, byte[][]> { }
  private class StackEntry {
  	public FlagMap flagMap = new FlagMap();
  	public List<Component> components = new ArrayList<Component>();
  }
	
	private ComponentCollection components;
	private Deque<StackEntry> stack = new ArrayDeque<StackEntry>();

	private static final int SECTOR_WIDTH = 20;
	private static final int SECTOR_HEIGHT = 15;

	private static final byte FLAG_BLOCKED = 0x01;
	private static final byte FLAG_KEEP_FREE = 0x02;
	private static final byte FLAG_SAFE = 0x04;
	private static final byte FLAG_TAKEN = 0x08; // by non-solid component
	
	public BuildingSite(ComponentCollection components) {
		this.components = components;
		StackEntry first = new StackEntry();
		first.components = null; // Use this.components instead
		stack.push(first);
	}

	public ComponentCollection getComponents() {
		return components;
	}
	
	/**
	 * Pushes an entry to the stack so that changes will be made inside a sandbox
	 */
	public void push() {
		stack.push(new StackEntry());
	}
	
	/**
	 * Applies all changes made in the deepest sandbox
	 * 
	 * @throws java.lang.IllegalStateException pop is called more often than push
	 */
	public void popAndApply() {
		if (stack.size() <= 1)
			throw new IllegalStateException("Tried to call pop more often than push");
		
		StackEntry entry = stack.pop();
		for (Point sector : entry.flagMap.keySet()) {
			stack.peek().flagMap.put(sector, entry.flagMap.get(sector));
		}
		List<Component> components = stack.peek().components;
		for (Component component : entry.components) {
			if (components != null)
				components.add(component);
			else
				this.components.add(component);
		}
	}
	
	/**
	 * Discards the changes made in the deepest sandbox
	 * 
	 * @throws java.lang.IllegalStateException pop is called more often than push
	 */
	public void popAndDiscard() {
		if (stack.size() <= 1)
			throw new IllegalStateException("Tried to call pop more often than push");
		
		stack.pop();
	}
	
	public boolean canPop() {
		return stack.size() > 1;
	}
	
	public boolean place(Component component, Point position) {
		byte flags = getFlags(position);
		boolean isSolid = component instanceof Body && ((Body)component).isSolid();
		
		if ((flags & FLAG_TAKEN) != 0 || (isSolid && (flags & FLAG_KEEP_FREE) != 0))
			return false;
		
		flags |= FLAG_TAKEN;
		if (isSolid) {
			makeSafe(position.add(0, 1));
			flags |= FLAG_BLOCKED;
		}
		
		component.setPosition(position.toVector());
		StackEntry entry = stack.peek();
		if (entry.components != null)
			stack.peek().components.add(component);
		else
			components.add(component);
		setFlags(position, flags);
		return true;
	}
	
	public boolean isFree(Point position) {
		return (getFlags(position) & FLAG_BLOCKED) == 0;
	}
	
	public boolean isSafe(Point position) {
		byte flags = getFlags(position);
		return (flags & FLAG_SAFE) != 0 && (flags & FLAG_BLOCKED) == 0;
	}
	
	public boolean keepFree(Point position) {
		byte flags = getFlags(position);
		if ((flags & FLAG_BLOCKED) != 0)
			return false;
		
		setFlags(position, (byte)(flags | FLAG_KEEP_FREE));
		return true;
	}
	
	public boolean canPlaceSolid(Point position) {
		byte flags = getFlags(position);
		return (flags & FLAG_TAKEN) == 0 && (flags & FLAG_KEEP_FREE) == 0;
	}
	
	public boolean canPlace(Point position) {
		byte flags = getFlags(position);
		return (flags & FLAG_TAKEN) == 0;
	}
	
	private boolean makeSafe(Point position) {
		byte flags = getFlags(position);
		if ((flags & FLAG_BLOCKED) == 0) {
			setFlags(position, (byte)(flags | FLAG_SAFE));
			return true;
		} else
			return false;
	}
	
	/**
	 * 
	 * @param position
	 * @param forWriteAccess
	 * @return may be null if forWriteAccess is false
	 */
	private byte[][] getFlagArrayOfPosition(Point position, boolean forWriteAccess) {
		// -1 / x = 0, but we want it to be in sector -1
		Point point = new Point(
			getSector(position.getX(), SECTOR_WIDTH),
			getSector(position.getY(), SECTOR_HEIGHT));
		
		boolean isHead = true;
		for (StackEntry entry : stack) {
			if (entry.flagMap.containsKey(point)) {
				byte[][] arr = entry.flagMap.get(point);
				// If this array doesn't exist in the last (current) stack entry, copy it
				if (!isHead && forWriteAccess) {
					arr = deepCopyArray(arr);
					stack.peek().flagMap.put(point, arr);
				}
				return arr;
			}
			isHead = false;
		}

		// Start of stack reached, and no array found, so create one
		if (forWriteAccess) {
	  	byte[][] arr = new byte[SECTOR_WIDTH][SECTOR_HEIGHT];
			stack.peek().flagMap.put(point, arr);
			return arr;
		} else
			return null;
	}
	
	private byte[][] deepCopyArray(byte[][] arr) {
		byte[][] newArr = Arrays.copyOf(arr, arr.length);
		for (int x = 0; x < arr.length; x++) {
			newArr[x] = Arrays.copyOf(newArr[x], newArr[x].length);
		}
		return newArr;
	}
	
	private int getSector(int position, int cellSize) {
		if (position < 0)
			// position -1 is the last entry in sector -1; -20 is the first one in sector -1
			return (position - 1) / cellSize - 1;
		else
			return position / cellSize;
	}
	
	private int getOffsetInSector(int position, int cellSize) {
		int tmp = position % cellSize;
		return tmp < 0 ? tmp + cellSize : tmp;
	}
	
	private byte getFlags(Point position) {
		byte[][] arr = getFlagArrayOfPosition(position, false);
		if (arr != null)
			return getFlags(position, arr);
		else
			return 0;
	}
	
	private byte getFlags(Point position, byte[][] arr) {
		int x = getOffsetInSector(position.getX(), SECTOR_WIDTH);
		int y = getOffsetInSector(position.getY(), SECTOR_HEIGHT);
		return arr[x][y];
	}
	
	private void setFlags(Point position, byte flags) {
		byte[][] arr = getFlagArrayOfPosition(position, true);
		setFlags(position, flags, arr);
	}
	
	private void setFlags(Point position, byte flags, byte[][] arr) {
		int x = getOffsetInSector(position.getX(), SECTOR_WIDTH);
 		int y = getOffsetInSector(position.getY(), SECTOR_HEIGHT);
		arr[x][y] = flags;
	}
}

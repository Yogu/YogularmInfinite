package de.yogularm.building;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import de.yogularm.components.Component;
import de.yogularm.components.Component;
import de.yogularm.components.ComponentCollection;
import de.yogularm.components.general.Platform;
import de.yogularm.geometry.Point;
import de.yogularm.geometry.Rect;
import de.yogularm.geometry.RectTrace;
import de.yogularm.geometry.Straight;
import de.yogularm.geometry.Vector;
import de.yogularm.utils.ArrayDeque;
import de.yogularm.utils.Deque;

/**
 * Every grid cell can have the following flags:
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
	private static final byte FLAG_TEMPORARILY_BLOCKED = 0x10; // by platforms
	private static final byte FLAG_KEEP_FREE = 0x02; // may be blocked temporarily
	
	private static final byte FLAG_SAFE = 0x04;
	private static final byte FLAG_TAKEN = 0x08; // by solid or non-solid component
	
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
	
	/**
	 * Places the specified component and makes related changes to the flag set
	 * 
	 * @param component The component to add
	 * @param position The position which overrides the component's original position
	 * @return true, if the component has successfully been added, or false if the flag set denies
	 *   the component to be placed
	 * @see #canPlace canPlace() to check whether a non-solid block can be placed
	 * @see #canPlaceSolid canPlaceSolid() to check whether a solid block can be placed
	 */
	public boolean place(Component component, Point position) {
		byte flags = getFlags(position);
		boolean isPlatform = component instanceof Platform;
		boolean isCharacter = component instanceof de.yogularm.components.Character;
		boolean isBlock = !isPlatform && !isCharacter
			&& component instanceof Component && ((Component)component).isSolid();
		boolean isClimbable = component instanceof Component && ((Component)component).isClimbable();

		Collection<Point> platformCells = null;
		
		// ============== Check ==============
		
		if (isPlatform) {
			platformCells = getPlatformCells((Platform)component);
			if (!areAlwaysFree(platformCells))
				return false;
		} else if (isCharacter) {
			// Don't place characters in platform's area
			if ((flags & (FLAG_BLOCKED | FLAG_TEMPORARILY_BLOCKED)) != 0)
				return false;
			flags |= FLAG_TEMPORARILY_BLOCKED;
		} else {
			if ((flags & FLAG_TAKEN) != 0
				|| (isBlock && (flags & (FLAG_KEEP_FREE | FLAG_TEMPORARILY_BLOCKED)) != 0))
				return false;
			
			flags |= FLAG_TAKEN;
			if (isBlock) {
				flags |= FLAG_BLOCKED;
			} else if (isClimbable)
				flags |= FLAG_SAFE;
		}

		// ============== Do ==============
		
		component.setPosition(position.toVector());
		StackEntry entry = stack.peek();
		if (entry.components != null)
			stack.peek().components.add(component);
		else
			components.add(component);
		setFlags(position, flags);
		
		if (isBlock)
			makeSafe(position.add(0, 1));
		
		if (platformCells != null) {
			for (Point cell : platformCells) {
				addFlags(cell, FLAG_TEMPORARILY_BLOCKED);
			}
		}
		
		return true;
	}
	
	/**
	 * Checks whether the player can freely move in the specified position.
	 * 
	 * <p>If the cell is temporarily blocked (e.g. by a moving platform), this method returns
	 * <code>true</code>, anyway. See {@link #isAlwaysFree(Point)}.</p>
	 * 
	 * <p>Note that this method returns <code>true</code> on an item although no more component can be
	 * placed there. See {@link #canPlace(Point)} to check whether a component can be placed.</p>
	 *   
	 * @param position
	 * @return <code>true</code>, if the specified position is free for moving
	 */
	public boolean isFree(Point position) {
		return (getFlags(position) & FLAG_BLOCKED) == 0;
	}
	
	public boolean areFree(Collection<Point> cells) {
		for (Point cell : cells)
			if (!isFree(cell))
				return false;
		return true;
	}
	
	/**
	 * Checks whether the specified cell is never blocked.
	 * 
	 * <p>Note that this method returns <code>true</code> on an item although no more component can be
	 * placed there. See {@link #canPlace(Point)} to check whether a component can be placed.</p>
	 *   
	 * @param position
	 * @return <code>true</code>, if the specified position is free for moving
	 */
	public boolean isAlwaysFree(Point position) {
		return (getFlags(position) & (FLAG_BLOCKED | FLAG_TEMPORARILY_BLOCKED)) == 0;
	}
	
	public boolean areAlwaysFree(Collection<Point> cells) {
		for (Point cell : cells)
			if (!isAlwaysFree(cell))
				return false;
		return true;
	}
	
	/**
	 * Checks whether the player can stay at the specified cell without falling down
	 * 
	 * @param position the cell to check
	 * @return <code>true</code>, if the specified cell is safe
	 */
	public boolean isSafe(Point position) {
		byte flags = getFlags(position);
		return (flags & FLAG_SAFE) != 0 && (flags & FLAG_BLOCKED) == 0;
	}
	
	public boolean areSafe(Collection<Point> cells) {
		for (Point cell : cells)
			if (!isSafe(cell))
				return false;
		return true;
	}
	
	/**
	 * Prevents the specified cell from being blocked in future. The cell may be blocked temporarily,
	 * anyway.
	 * 
	 * @param position the cell to keep free
	 * @return <code>false</code>, if flag is already blocked 
	 */
	public boolean keepFree(Point position) {
		byte flags = getFlags(position);
		if ((flags & FLAG_BLOCKED) != 0)
			return false;
		
		setFlags(position, (byte)(flags | FLAG_KEEP_FREE));
		return true;
	}
	
	/**
	 * Tries to keep all specified cells free. If one cell can't be kept free, nothing is done.
	 * 
	 * @param cells The cells to keep free
	 * @return
	 */
	public boolean keepFree(Collection<Point> cells) {
		// Maybe check first, then apply? May be faster than push&pop
		push();
		try {
			for (Point cell : cells)
				if (!keepFree(cell)) {
					popAndDiscard();
					return false;
				}
			popAndApply();
			return true;
		} catch (RuntimeException e) {
			popAndDiscard();
			throw e;
		}
	}
	
	public boolean isKeptFree(Point position) {
		return (getFlags(position) & FLAG_KEEP_FREE) != 0;
	}
	
	public boolean areKeptFree(Collection<Point> cells) {
		for (Point cell : cells)
			if (!isKeptFree(cell))
				return false;
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
		byte[][] newArr = de.yogularm.utils.Arrays.copyOf(arr, arr.length);
		for (int x = 0; x < arr.length; x++) {
			newArr[x] = de.yogularm.utils.Arrays.copyOf(newArr[x], newArr[x].length);
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
	
	private void addFlags(Point position, byte flags) {
		setFlags(position, (byte)(getFlags(position) | flags));
	}
	
	@SuppressWarnings("unused")
  private void removeFlags(Point position, byte flags) {
		setFlags(position, (byte)(getFlags(position) & ~flags));
	}
	
	private Collection<Point> getPlatformCells(Platform platform) {
		Vector[] targets = platform.getTargets();
		Vector origin = platform.getOrigin();
		Rect dimensions = new Rect(-0.5f, -0.5f, 1.5f, 1.5f); // platforms are inaccurate
		Collection<Point> cells = new ArrayList<Point>();
		for (int i = 0; i < targets.length; i++) {
			Vector source = origin.add(targets[i]);
			Vector target = origin.add(targets[(i + 1 < targets.length) ? i + 1 : 0]);
			Straight straight = new Straight(source, target);
			RectTrace trace = new RectTrace(straight, dimensions, source.getX(), target.getX());
			cells.addAll(trace.getCollidingCells());
		}
		return cells;
	}
}

package de.yogularm.building;

import java.util.HashMap;
import java.util.Map;

import de.yogularm.components.Body;
import de.yogularm.components.Component;
import de.yogularm.components.ComponentCollection;
import de.yogularm.geometry.Point;

/**
 * Every grid position can have the following flags:
 * 
 *  - blocked
 *  - keep free
 *  - safe (player can stay here)
 * 
 * @author Jan
 *
 */
public class BuildingSite {
	private ComponentCollection components;
	private Map<Point, byte[][]> map = new HashMap<Point, byte[][]>();

	private static final int SECTOR_WIDTH = 20;
	private static final int SECTOR_HEIGHT = 15;

	private static final byte FLAG_BLOCKED = 0x01;
	private static final byte FLAG_KEEP_FREE = 0x02;
	private static final byte FLAG_SAFE = 0x04;
	private static final byte FLAG_TAKEN = 0x08; // by non-solid component
	
	public BuildingSite(ComponentCollection components) {
		this.components = components;
	}

	public ComponentCollection getComponents() {
		return components;
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
	
	private byte[][] getFlagArrayOfPosition(Point position, boolean createIfMissing) {
		// -1 / x = 0, but we want it to be in sector -1
		Point point = new Point(
			getSector(position.getX(), SECTOR_WIDTH),
			getSector(position.getY(), SECTOR_HEIGHT));
		
	  if (map.containsKey(point))
	  	return map.get(point);
	  else if (createIfMissing) {
	  	byte[][] array = new byte[SECTOR_WIDTH][SECTOR_HEIGHT];
	  	map.put(point, array);
	  	return array;
	  } else
	  	return null;
	}
	
	private int getSector(int position, int cellSize) {
		if (position < 0)
			// position -1 is the last entry in sector -1; -20 is the first one in sector -1
			return (position - 1) % cellSize - 1;
		else
			return position % cellSize;
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

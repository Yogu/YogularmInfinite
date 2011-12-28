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
	
	public BuildingSite(ComponentCollection components) {
		this.components = components;
	}

	public ComponentCollection getComponents() {
		return components;
	}
	
	public boolean place(Component component, Point position) {
		if (!canPlace(position))
			return false;
		
		if (component instanceof Body && ((Body)component).isSolid())
			makeSafe(position.add(0, 1));
		
		component.setPosition(position.toVector());
		components.add(component);
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
	
	public boolean canPlace(Point position) {
		byte flags = getFlags(position);
		return (flags & FLAG_BLOCKED) == 0 && (flags & FLAG_KEEP_FREE) == 0;
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
			position.getX() / SECTOR_WIDTH - (position.getX() < 0 ? 1 : 0),
	  	position.getY() / SECTOR_HEIGHT - (position.getY() < 0 ? 1 : 0));
		
	  if (map.containsKey(point))
	  	return map.get(point);
	  else if (createIfMissing) {
	  	byte[][] array = new byte[SECTOR_WIDTH][SECTOR_HEIGHT];
	  	map.put(point, array);
	  	return array;
	  } else
	  	return null;
	}
	
	private byte getFlags(Point position) {
		byte[][] arr = getFlagArrayOfPosition(position, false);
		if (arr != null)
			return getFlags(position, arr);
		else
			return 0;
	}
	
	private byte getFlags(Point position, byte[][] arr) {
		// position -1 is the last entry in sector -1
		int x = position.getX() % SECTOR_WIDTH + (position.getX() < 0 ? SECTOR_WIDTH : 0);
		int y = position.getY() % SECTOR_HEIGHT + (position.getY() < 0 ? SECTOR_HEIGHT : 0);
		return arr[x][y];
	}
	
	private void setFlags(Point position, byte flags) {
		byte[][] arr = getFlagArrayOfPosition(position, true);
		setFlags(position, flags, arr);
	}
	
	private void setFlags(Point position, byte flags, byte[][] arr) {
		// position -1 is the last entry in sector -1
		int x = position.getX() % SECTOR_WIDTH + (position.getX() < 0 ? SECTOR_WIDTH : 0);
		int y = position.getY() % SECTOR_HEIGHT + (position.getY() < 0 ? SECTOR_HEIGHT : 0);
		arr[x][y] = flags;
	}
}

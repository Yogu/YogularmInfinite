package de.yogularm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.yogularm.event.EventListener;

public class ComponentTree implements ComponentCollection {
	private int sectorWidth;
	private int sectorHeight;
	private int minX, maxX, minY, maxY; // absolutely ranges of the world
	private Map<Point, List<Component>> tree = new HashMap<Point, List<Component>>();
	private int count;
	
	private static final float BUFFER_FRACTION = 0.1f;
	
	public ComponentTree(int sectorWidth, int sectorHeight) {
		this.sectorWidth = sectorWidth;
		this.sectorHeight = sectorHeight;
	}
	
	private List<Component> getListOfPosition(Vector position, boolean createIfMissing) {
		Point point = new Point(
			(int)(position.getX() / sectorWidth),
	  	(int)(position.getY() / sectorHeight));
	  if (tree.containsKey(point))
	  	return tree.get(point);
	  else if (createIfMissing) {
	  	List<Component> list = new ArrayList<Component>();
	  	minX = Math.min(minX, point.getX());
	  	maxX = Math.max(maxX, point.getX());
	  	minY = Math.min(minY, point.getY());
	  	maxY = Math.max(maxY, point.getY());
	  	tree.put(point, list);
	  	return list;
	  } else
	  	return null;
	}
	
	@Override
  public Collection<Component> getComponentsAround(Rect range) {
		List<Component> result = new ArrayList<Component>();

		// Limit to this.max/minX/Y because there are no components outside these limits
		// No Math.ceil()! Reason: e.g. tree[0, 0] reaches from (0,0) to (1, 1)
		int minX = Math.max(this.minX, (int)(range.getLeft() / sectorWidth - BUFFER_FRACTION));
		int maxX = Math.min(this.maxX, (int)(range.getRight() / sectorWidth + BUFFER_FRACTION));
		int minY = Math.max(this.minY, (int)(range.getBottom() / sectorHeight - BUFFER_FRACTION));
		int maxY = Math.min(this.maxY, (int)(range.getTop() / sectorHeight + BUFFER_FRACTION));
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				List<Component> list = tree.get(new Point(x, y));
				if (list != null) {
					result.addAll(list);
				}
			}
		}

		//System.out.println(minX + " - " + maxX + "; " + minY + " - " + maxY);
		//System.out.println(Thread.currentThread().getStackTrace()[2] + ": " + (maxX - minX + 1) * (maxY - minY + 1));
		
	  return result;
  }

	public Collection<Component> getComponentsAround(Vector position) {
		List<Component> list = getListOfPosition(position, false);
		if (list != null)
			return new ArrayList<Component>(list);
		else
			return new ArrayList<Component>(0);
	}

	@Override
  public boolean contains(Component component) {
		List<Component> list = getListOfPosition(component.getPosition(), false);
	  return list != null && list.contains(component);
  }

	@Override
  public void add(final Component component) {
		List<Component> list = getListOfPosition(component.getPosition(), true);
		if (!list.contains(component)) {
		  list.add(component);
		  count++;
		  
		  // Track changes
		  component.onMoved.addListener(new EventListener<Vector>() {
				public void call(Object sender, Vector oldPosition) {
					List<Component> oldList = getListOfPosition(oldPosition, false);
					List<Component> newList = getListOfPosition(component.getPosition(), true);
					if (newList != oldList) {
						if (oldList != null && oldList.remove(component))
							newList.add(component);
						else // the component must have been removed
							component.onMoved.removeListener(this);
					}
				}
			});
		}
  }

	@Override
  public void remove(Component component) {
		List<Component> list = getListOfPosition(component.getPosition(), false);
	  if (list != null) {
	  	if (list.remove(component))
	  		count--;
	  }
  }
	
	/**
	 * Gets the total count of all components
	 * @return
	 */
	public int getCount() {
		return count;
	}
}

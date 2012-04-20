package de.yogularm.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.yogularm.event.EventListener;
import de.yogularm.geometry.Point;
import de.yogularm.geometry.Rect;
import de.yogularm.geometry.Vector;

public class ComponentTree implements ObservableComponentCollection {
	private int sectorWidth;
	private int sectorHeight;
	private int minX, maxX, minY, maxY; // absolutely ranges of the world
	private Map<Point, List<Component>> tree = new HashMap<Point, List<Component>>();
	private int count;
	private List<ComponentCollectionListener> listeners = new ArrayList<ComponentCollectionListener>();
	private Map<Integer, Component> byID = new HashMap<Integer, Component>();
	
	/**
	 * The fraction of a sector dimension that is added to a rectangle when receiving components in
	 * that sector
	 */
	private static final float BUFFER_FRACTION = 0.1f;
	
	public ComponentTree(int sectorWidth, int sectorHeight) {
		this.sectorWidth = sectorWidth;
		this.sectorHeight = sectorHeight;
	}
	
	private Point getSector(Vector position) {
		return new Point(
				(int)Math.floor(position.getX() / sectorWidth),
		  	(int)Math.floor(position.getY() / sectorHeight));
	}
	
	private List<Component> getListOfPosition(Vector position, boolean createIfMissing) {
		Point sector = getSector(position);
	  if (tree.containsKey(sector))
	  	return tree.get(sector);
	  else if (createIfMissing) {
	  	List<Component> list = new ArrayList<Component>();
	  	minX = Math.min(minX, sector.getX());
	  	maxX = Math.max(maxX, sector.getX());
	  	minY = Math.min(minY, sector.getY());
	  	maxY = Math.max(maxY, sector.getY());
	  	tree.put(sector, list);
	  	return list;
	  } else
	  	return null;
	}
	
	@Override
  public Collection<Component> getComponentsAround(Rect range) {
		List<Component> result = new ArrayList<Component>();

		// Limit to this.max/minX/Y because there are no components outside these limits
		// No Math.ceil()! Reason: e.g. tree[0, 0] reaches from (0,0) to (1, 1)
		int minX = Math.max(this.minX, (int)Math.floor(range.getLeft() / sectorWidth - BUFFER_FRACTION));
		int maxX = Math.min(this.maxX, (int)Math.floor(range.getRight() / sectorWidth + BUFFER_FRACTION));
		int minY = Math.max(this.minY, (int)Math.floor(range.getBottom() / sectorHeight - BUFFER_FRACTION));
		int maxY = Math.min(this.maxY, (int)Math.floor(range.getTop() / sectorHeight + BUFFER_FRACTION));
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				List<Component> list = tree.get(new Point(x, y));
				if (list != null) {
					result.addAll(list);
				}
			}
		}
		
	  return result;
  }

	public Collection<Component> getComponentsAround(Vector position) {
		List<Component> list = getListOfPosition(position, false);
		if (list != null)
			return new ArrayList<Component>(list);
		else
			return new ArrayList<Component>(0);
	}
	
	/**
	 * Gets a list of all components in the given sector
	 * 
	 * @param sector The sector whose components to receive
	 * @return
	 */
	public List<Component> getComponentsOfSector(Point sector) {
		if (tree.containsKey(sector))
			return new ArrayList<Component>(tree.get(sector));
		else
			return new ArrayList<Component>();
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
		  component.setCollection(this);
		  count++;
		  
		  synchronized (listeners) {
		  	Point sector = getSector(component.getPosition());
		  	for (ComponentCollectionListener listener : listeners) {
		  		listener.componentAdded(this, component, sector);
		  	}
		  }
		  
		  // Track changes
		  component.onMoved.addListener(new EventListener<Vector>() {
				public void call(Object sender, Vector oldPosition) {
			  	Point oldSector = getSector(oldPosition);
			  	Point newSector = getSector(component.getPosition());
			  	boolean sectorChanged = !oldSector.equals(newSector);
			  	
			  	if (sectorChanged) {
						List<Component> oldList = getListOfPosition(oldPosition, false);
						List<Component> newList = getListOfPosition(component.getPosition(), true);
						if (newList != oldList) {
							if (oldList != null && oldList.remove(component)) {
								newList.add(component);
							} else {
								 // the component must have been removed
								component.onMoved.removeListener(this);
								return;
							}
						}
			  	}

				  synchronized (listeners) {
				  	for (ComponentCollectionListener listener : listeners) {
				  		listener.componentMoved(ComponentTree.this, component, oldSector, newSector, sectorChanged);
				  	}
				  }
				}
			});
		}
	  
	  byID.put(component.getID(), component);
  }

	@Override
  public void remove(Component component) {
		List<Component> list = getListOfPosition(component.getPosition(), false);
	  if (list != null) {
	  	if (list.remove(component)) {
	  		count--;
			  
			  synchronized (listeners) {
			  	Point sector = getSector(component.getPosition());
			  	for (ComponentCollectionListener listener : listeners) {
			  		listener.componentRemoved(this, component, sector);
			  	}
			  }
	  	}
	  }
	  
	  byID.remove(component.getID());
  }
	
	/**
	 * Gets the total count of all components
	 * @return
	 */
	public int getCount() {
		return count;
	}

	@Override
	public void addListener(ComponentCollectionListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	@Override
	public void removeListener(ComponentCollectionListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	@Override
	public Component getByID(int id) {
		return byID.get(id);
	}
}

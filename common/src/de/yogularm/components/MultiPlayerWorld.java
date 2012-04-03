package de.yogularm.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.yogularm.building.Builder2;
import de.yogularm.building.BuildingSite;
import de.yogularm.building.test.TestBuilder;
import de.yogularm.geometry.Point;
import de.yogularm.geometry.Rect;

public class MultiPlayerWorld {
	private ObservableComponentCollection components;
	private BuildingSite buildingSite;
	private Builder2 builder;
	private Map<Point, Integer> observedSectors = new HashMap<Point, Integer>();
	private Set<Point> newlyObservedSectors = new HashSet<Point>();
	private List<MultiPlayerWorldListener> listeners = new ArrayList<MultiPlayerWorldListener>();
	
	public static final int SECTOR_WIDTH = 48;
	public static final int SECTOR_HEIGHT = 48;
	
	public MultiPlayerWorld() {
		components = new ComponentTree(SECTOR_WIDTH, SECTOR_HEIGHT);
		buildingSite = new BuildingSite(components);
		builder = new TestBuilder();
		builder.init(buildingSite);
		// TODO: only for testing
		builder.build(new Rect(0, 0, SECTOR_WIDTH, SECTOR_HEIGHT));
	}
	
	public ObservableComponentCollection getComponents() {
		return components;
	}

	public void update(float elapsedTime) {
		build();
		
		Collection<Point> sectors;
		synchronized (observedSectors) {
			sectors = new ArrayList<Point>(observedSectors.keySet());
		}
		
		for (Point sector : sectors) {
			List<Component> components = this.components.getComponentsOfSector(sector);
			for (Component component : components) {
				if (!component.isToRemove())
					component.update(elapsedTime);
				if (component.isToRemove())
					components.remove(component);
				
				if (component.hasChanged()) {
					component.clearChanged();
					
					synchronized (listeners) {
						for (MultiPlayerWorldListener listener : listeners) {
							listener.componentChanged(this, component, sector);
						}
					}
				}
			}
		}
	}

	public void build() {
		Set<Point> newSectors;
		synchronized (newlyObservedSectors) {
			if (newlyObservedSectors.size() == 0)
				return;
			newSectors = new HashSet<Point>(newlyObservedSectors);
			newlyObservedSectors.clear();
		}
		for (Point sector : newSectors) {
			builder.build(getRectOfSector(sector));
		}
	}
	
	public void stopObservation(Collection<Point> sectors) {
		synchronized (observedSectors) {
			for (Point sector : sectors) {
				Integer count = observedSectors.get(sectors);
				if (count != null) {
					count--;
					if (count > 0)
						observedSectors.put(sector, count);
					else
						observedSectors.remove(sector);
				}
			}
		}
	}
	
	public void observeSectors(Collection<Point> sectors) {
		synchronized (observedSectors) {
			for (Point sector : sectors) {
				Integer count = observedSectors.get(sectors);
				if (count != null)
					observedSectors.put(sector, count + 1);
				else {
					synchronized (newlyObservedSectors) {
						newlyObservedSectors.add(sector);
					}
					observedSectors.put(sector, 1);
				}
			}
		}
	}
	
	private Rect getRectOfSector(Point sector) {
		int x = sector.getX();
		int y = sector.getY();
		return new Rect(x * SECTOR_WIDTH, y * SECTOR_HEIGHT, (x + 1) * SECTOR_WIDTH, (y + 1) * SECTOR_HEIGHT);
	}
	
	public void addListener(MultiPlayerWorldListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}
	
	public void removeListener(MultiPlayerWorldListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}
}

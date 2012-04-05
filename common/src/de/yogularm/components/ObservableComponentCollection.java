package de.yogularm.components;

import java.util.List;

import de.yogularm.geometry.Point;

public interface ObservableComponentCollection extends ComponentCollection {
	void addListener(ComponentCollectionListener listener);
	void removeListener(ComponentCollectionListener listener);
	
	List<Component> getComponentsOfSector(Point sector);
	
	Component getByID(int id);
}

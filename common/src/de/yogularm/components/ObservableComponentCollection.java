package de.yogularm.components;

import java.util.Collection;
import java.util.List;

import de.yogularm.geometry.Point;

public interface ObservableComponentCollection extends ComponentCollection {
	void addListener(ComponentCollectionListener listener);
	void removeListener(ComponentCollectionListener listener);
	
	List<Component> getComponentsOfSector(Point sector);
	
	/**
	 * Removes all components in the given sector and adds the given components to that sector.
	 * 
	 * Before calling this method, make sure that the components really are in the specified sector.
	 * 
	 * @param sector The sector whose components to replace
	 */
	void replaceComponentsOfSector(Point sector, Collection<Component> components);
}

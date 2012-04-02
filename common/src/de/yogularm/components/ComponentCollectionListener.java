package de.yogularm.components;

import de.yogularm.geometry.Point;

public interface ComponentCollectionListener {
	/**
	 * Is called when a component is added to the collection
	 * 
	 * @param collection The component collection to which the component is added
	 * @param sector The sector of the component's initial position
	 * @param component The new component
	 */
	void componentAdded(ObservableComponentCollection collection, Component component, Point sector);
	
	/**
	 * Is called when a component is removed from the collection
	 * 
	 * @param collection The component collection from which the component is removed
	 * @param sector The sector of the component's last position
	 * @param component The removed component
	 */
	void componentRemoved(ObservableComponentCollection collection, Component component, Point sector);
	
	/**
	 * Is called when a component has moved
	 * 
	 * @param collection The component collection
	 * @param component The component that has moved
	 * @param lastSector The original sector of the component
	 * @param newSector The current sector of the component
	 * @param boolean sectorHasChanged true, if the component has changed the sector during moving
	 */
	void componentMoved(ObservableComponentCollection collection, Component component,
			Point lastSector, Point newSector, boolean sectorHasChanged);
}

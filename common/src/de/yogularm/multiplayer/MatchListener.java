package de.yogularm.multiplayer;

import de.yogularm.components.Component;
import de.yogularm.geometry.Point;

public interface MatchListener {
	/**
	 * Is called when a component is added to the world
	 * 
	 * @param sector The sector of the component's initial position
	 * @param component The new component
	 */
	void componentAdded(Component component, Point sector);
	
	/**
	 * Is called when a component is removed from the world
	 * 
	 * @param sector The sector of the component's last position
	 * @param component The removed component
	 */
	void componentRemoved(Component component, Point sector);
	
	/**
	 * Is called when a component has moved from one sector to a different one
	 * 
	 * @param component The component that has moved
	 * @param lastSector The original sector of the component
	 * @param newSector The current sector of the component
	 */
	void componentChangedSector(Component component, Point lastSector, Point newSector);

	void componentChanged(Component component, Point sector);
	
	/**
	 * Is called when position or momentum of a component has changed
	 * 
	 * @param component The component which has changed
	 * @param sector The component's sector
	 */
	void quickChange(Component component, Point sector);

	void stateChanged(MatchState oldState, MatchState newState);
}

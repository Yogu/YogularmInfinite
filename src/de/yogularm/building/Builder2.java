package de.yogularm.building;

import de.yogularm.geometry.Rect;

public interface Builder2 {
	/**
	 * Takes the specified building site and builds the beginning so that [0, 0] is a safe position
	 * 
	 * @param buildingSite
	 */
	public void init(BuildingSite buildingSite);
	
	/**
	 * Builds until the the specified rectangle is complete
	 * 
	 * @param bounds
	 */
	public void build(Rect bounds);
}

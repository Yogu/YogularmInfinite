package de.yogularm;

public interface Builder {
	/**
	 * Assigns the world and creates a new random seed
	 * 
	 * @param world The world to be used for next build() calls
	 * @param buldingPosition The position where the player could stand safely before this builder
	 *   starts building
	 */
	void init(ComponentCollection components, Vector buildingPosition);
	
	void build();
	
	Vector getBuildingPosition();
}

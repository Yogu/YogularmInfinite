package de.yogularm.building;

import java.util.List;

import de.yogularm.geometry.Point;

interface MovingStrategy {
	/**
	 * @return null if moving is not possible
	 */
	List<Point> getTrace(MovingDetails move);
}
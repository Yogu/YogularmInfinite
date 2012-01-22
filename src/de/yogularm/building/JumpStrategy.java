package de.yogularm.building;

import java.util.List;

import de.yogularm.geometry.Point;

class JumpStrategy extends FallStrategy {
	JumpStrategy(BuildingPath buildingPath) {
	  super(buildingPath);
  }

	public List<Point> getTrace(MovingDetails move) {
		return super.getTrace(move, true);
	}
}
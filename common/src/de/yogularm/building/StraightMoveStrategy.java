package de.yogularm.building;

import java.util.ArrayList;
import java.util.List;

import de.yogularm.geometry.Point;

class StraightMoveStrategy implements MovingStrategy {
	/**
   * 
   */
  private final BuildingPath buildingPath;

	/**
   * @param buildingPath
   */
  StraightMoveStrategy(BuildingPath buildingPath) {
    this.buildingPath = buildingPath;
  }

	public List<Point> getTrace(MovingDetails move) {
		Point source = move.source;
		Point target = move.target;
		if (source.getY() == target.getY()) {
			List<Point> list = new ArrayList<Point>();
			int min = Math.min(source.getX(), target.getX());
			int max = Math.max(source.getX(), target.getX());
			for (int x = min; x <= max; x++) {
				Point p = new Point(x, source.getY());
				if (!this.buildingPath.getBuildingSite().isSafe(p) || !this.buildingPath.getBuildingSite().isFree(p))
					return null;
				list.add(p);
			}
			System.out.println("    Works with straight move");
			return list;
		} else
			return null;
	}
}
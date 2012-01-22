package de.yogularm.building;

import java.util.ArrayList;
import java.util.List;

import de.yogularm.geometry.Point;

class ClimbStrategy implements MovingStrategy {
	/**
   * 
   */
  private final BuildingPath buildingPath;

	/**
   * @param buildingPath
   */
  ClimbStrategy(BuildingPath buildingPath) {
    this.buildingPath = buildingPath;
  }

	public List<Point> getTrace(MovingDetails move) {
		Point source = move.source;
		Point target = move.target;
		if (source.getX() == target.getX()) {
			List<Point> list = new ArrayList<Point>();
			int min = Math.min(source.getY(), target.getY());
			int max = Math.max(source.getY(), target.getY());
			for (int y = min; y <= max; y++) {
				Point p = new Point(source.getX(), y);
				if (!this.buildingPath.getBuildingSite().isSafe(p) || !this.buildingPath.getBuildingSite().isFree(p))
					return null;
				list.add(p);
			}
			System.out.println("    Works with climbing");
			return list;
		} else
			return null;
	}
}
package de.yogularm.building;

import java.util.ArrayList;
import java.util.List;

import de.yogularm.components.ComponentCollection;
import de.yogularm.geometry.Point;

public class BuildingPath {
	private BuildingSite buildingSite;
	private Point currentWaypoint;
	private List<Point> reachablePositions;
	
	private static final int MAX_REACHABLE_DISTANCE = 5;
	
	public BuildingPath(BuildingSite buildingSite, Point startPoint) {
		this.buildingSite = buildingSite;
		currentWaypoint = startPoint;
	}
	
	public boolean setWaypoint(Point position) {
		if (!buildingSite.canPlace(position) || !buildingSite.isSafe(position))
			return false;
		
		/*if (!canMove(currentWaypoint, position))
			return false;*/
		
		currentWaypoint = position;
		buildingSite.keepFree(position);
		return true;
	}
	
	public Point getCurrentWaypoint() {
		return currentWaypoint;
	}
	
	public BuildingSite getBuildingSite() {
		return buildingSite;
	}

	public ComponentCollection getComponents() {
		return buildingSite.getComponents();
	}
	
	public List<Point> getReachablePositions() {
		if (reachablePositions == null)
			reachablePositions = calculateReachablePositions();
		return reachablePositions;
	}
	
	private List<Point> calculateReachablePositions() {
		List<Point> list = new ArrayList<Point>();
		Point c = getCurrentWaypoint();
		
		// Straight sidewards move
		list.add(c.add(1, 0));
		list.add(c.add(-1, 0));
		
		return list;
	}

	/*private static interface MovingStrategy {
		boolean canMove(Point source, Point target);
	}
	
	@SuppressWarnings("serial")
  private static List<MovingStrategy> movingStrategies = new ArrayList<MovingStrategy>() {{
  	// Straight move
		add(new MovingStrategy() {
			public boolean canMove(Point source, Point target) {
				return target.getY() == source.getY() && Math.abs(target.getX() - source.getX()) <= 1;
			}
		});

  	// Straight move
		add(new MovingStrategy() {
			public boolean canMove(Point source, Point target) {
				return target.getY() == source.getY() && Math.abs(target.getX() - source.getX()) <= 1;
			}
		});
	}};
	
	private boolean canMove(Point source, Point target) {
		for (MovingStrategy strategy : movingStrategies)
			if (strategy.canMove(source, target))
				return true;
		return false;
	}*/
}

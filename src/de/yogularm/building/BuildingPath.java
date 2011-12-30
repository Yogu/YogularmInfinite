package de.yogularm.building;

import java.util.ArrayList;
import java.util.List;

import de.yogularm.Config;
import de.yogularm.components.Component;
import de.yogularm.components.ComponentCollection;
import de.yogularm.components.debug.ParabolaComponent;
import de.yogularm.geometry.Parabola;
import de.yogularm.geometry.Point;
import de.yogularm.geometry.Vector;

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
		if (!buildingSite.isFree(position) || !buildingSite.isSafe(position))
			return false;
		
		/*if (!canMove(currentWaypoint, position))
			return false;*/
		
		currentWaypoint = position;
		buildingSite.keepFree(position);
		reachablePositions = null;
		return true;
	}
	
	public Point getCurrentWaypoint() {
		return currentWaypoint;
	}
	
	public void place(Component component, Point position) {
		buildingSite.place(component, position);
		reachablePositions = null;
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
		
		Point s = getCurrentWaypoint();
		Vector c = s.toVector().add(0.5f, 0); // Center of player start
		
		// Jump apex (Maximum turning point)
		float apexTime = Config.PLAYER_JUMP_SPEED / Config.GRAVITY_ACCELERATION; // v0 = g*t
		float apexY = 0.5f * Config.GRAVITY_ACCELERATION * apexTime * apexTime; // s = 0.5*a*t^2
		float apexX = apexTime * Config.PLAYER_SPEED;
		Parabola parabola = new Parabola(new Vector(apexX, apexY), Vector.ZERO);
		
		if (Config.DEBUG_BUILDING) {
			ParabolaComponent parabolaComponent = new ParabolaComponent(getComponents(), parabola, 0, 20, 20);
			parabolaComponent.setPosition(c);
			getComponents().add(parabolaComponent);
		}
		
		for (int y = (int)apexY; y >= -MAX_REACHABLE_DISTANCE; y--) {
			float maxX = parabola.getX2(y);
			for (int x = 1; x <= maxX; x++) {
				Point p = s.add(x, y);
				if (getBuildingSite().isFree(p))
					list.add(p);
				p = s.add(-x, y);
				if (getBuildingSite().isFree(p))
					list.add(p);
			}
		}
		
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

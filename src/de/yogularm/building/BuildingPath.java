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
	private List<Point> lastTrace;
	
	private static final int MAX_REACHABLE_DISTANCE = 5;
	
	public BuildingPath(BuildingSite buildingSite, Point startPoint) {
		this.buildingSite = buildingSite;
		currentWaypoint = startPoint;
	}
	
	public boolean setWaypoint(Point position) {
		if (!buildingSite.isFree(position) || !buildingSite.isSafe(position))
			return false;
		
		List<Point> trace = getTrace(currentWaypoint, position);
		if (trace == null)
			return false;
		
		for (Point p : trace) {
			getBuildingSite().keepFree(p);
		}
		
		lastTrace = trace;
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
	
	public List<Point> getLastTrace() {
		return lastTrace;
	}
	
	private List<Point> calculateReachablePositions() {
		List<Point> list = new ArrayList<Point>();
		
		Point s = getCurrentWaypoint();
		Vector c = s.toVector().add(0.5f, 0); // Center of player start
		
		// Jump apex (Maximum turning point)
		float apexTime = Config.PLAYER_JUMP_SPEED / Config.GRAVITY_ACCELERATION; // v0 = g*t
		float apexY = 0.5f * Config.GRAVITY_ACCELERATION * apexTime * apexTime; // s = 0.5*a*t^2
		float apexX = apexTime * Config.PLAYER_SPEED;
		// x = v*t => t = x/v
		// y = 0.5*g*t^2     => y = 0.5 * g * (x/v)^2 = 0.5 * g/v^2 * x^2
		// Apex is source, factor a is 0.5 * g/v^2
		float a = - 0.5f * Config.GRAVITY_ACCELERATION / Config.PLAYER_SPEED / Config.PLAYER_SPEED;
		Parabola parabola = new Parabola(a, /*new Vector(apexX, apexY), */Vector.ZERO);
		
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

	private interface MovingStrategy {
		/**
		 * @return null if moving is not possible
		 */
		List<Point> getTrace(Point source, Point target);
	}
	
	private class StraightMoveStrategy implements MovingStrategy {
		public List<Point> getTrace(Point source, Point target) {
			if (source.getY() == target.getY()) {
				List<Point> list = new ArrayList<Point>();
				int min = Math.min(source.getX(), target.getX());
				int max = Math.max(source.getX(), target.getX());
				for (int x = min; x <= max; x++) {
					Point p = new Point(x, source.getX());
					if (!getBuildingSite().isSafe(p) || !getBuildingSite().isFree(p))
						return null;
					list.add(p);
				}
				return list;
			} else
				return null;
		}
	}
	
	private class FreeFallMoveStrategy implements MovingStrategy {
		public List<Point> getTrace(Point source, Point target) {
			if (target.getY() < source.getY()) {
				List<Point> list = new ArrayList<Point>();

				int dir = target.getX() > source.getX() ? 1 : -1;
				
				// x = v*t => t = x/v
				// y = 0.5*g*t^2     => y = 0.5 * g * (x/v)^2 = 0.5 * g/v^2 * x^2
				// Apex is source, factor a is 0.5 * g/v^2
				float a = - 0.5f * Config.GRAVITY_ACCELERATION / Config.PLAYER_SPEED / Config.PLAYER_SPEED;
				Parabola parabola = new Parabola(a, source.toVector().add(dir > 0 ? 1 : 0, 0)); // bottom center
				Parabola bottomInner = parabola.move(new Vector(-dir, 0));
				Parabola topOuter = parabola.move(new Vector(dir, 1));
				Parabola bottomOuter = parabola.move(new Vector(dir, 0));
				if (Config.DEBUG_BUILDING) {
					getComponents().add(new ParabolaComponent(getComponents(), parabola, source.getX() + dir > 0 ? 0 : 1, target.getX() + 5*dir, 10));
					getComponents().add(new ParabolaComponent(getComponents(), bottomInner, source.getX() + dir > 0 ? 0 : 1, target.getX() + 5*dir, 10));
					getComponents().add(new ParabolaComponent(getComponents(), topOuter, source.getX() + dir > 0 ? 0 : 1, target.getX() + 5*dir, 10));
				}
				
				int min = Math.min(source.getX() + dir, target.getX());
				int max = Math.max(source.getX() + dir, target.getX());
				for (int x = min; x <= max; x++) {
					int innerX = dir > 0 ? x : x + 1;
					int outerX = dir < 0 ? x : x + 1;
					// Just (int) does not behave like floor!
					int minY = Math.max(target.getY(), (int)Math.floor(bottomInner.getY(outerX)));
					 // -1 because x and y represent the grid positions
					int maxY = (int)Math.ceil(topOuter.getY(innerX) - 1);
					
					// Target reached, now break and fall down to the target
					if (x == target.getX()) {
						// Make sure that player is above target
						float lowestBottom = bottomOuter.getY(outerX);
						if (lowestBottom < target.getY())
							return null;
						minY = Math.min(minY, target.getY());
					}
					
					for (int y = minY; y <= maxY; y++) {
						Point p = new Point(x, y);
						if (!getBuildingSite().isFree(p))
							return null;
						list.add(p);
					}
				}
				return list;
			} else
				return null;
		}
	}
	
	@SuppressWarnings("serial")
  private List<MovingStrategy> movingStrategies = new ArrayList<MovingStrategy>() {{
  	add(new StraightMoveStrategy());
  	add(new FreeFallMoveStrategy());
	}};

	/**
	 * @return null if moving is not possible
	 */
	public List<Point> getTrace(Point source, Point target) {
		for (MovingStrategy strategy : movingStrategies) {
			List<Point> trace = strategy.getTrace(source, target);
			if (trace != null)
				return trace;
		}
		return null;
	}
}

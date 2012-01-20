package de.yogularm.building;

import java.util.ArrayList;
import java.util.List;

import de.yogularm.Config;
import de.yogularm.components.Component;
import de.yogularm.components.ComponentCollection;
import de.yogularm.geometry.Parabola;
import de.yogularm.geometry.Point;
import de.yogularm.geometry.Vector;
import de.yogularm.utils.ArrayDeque;
import de.yogularm.utils.Deque;

public class BuildingPath {
	private class StackEntry {
		private Point currentWaypoint;
		private List<Point> reachablePositions;
		private List<Point> lastTrace;
	}

	private BuildingSite buildingSite;
	private Deque<StackEntry> stack = new ArrayDeque<StackEntry>();

	private static final int MAX_REACHABLE_DISTANCE = 3;

	public BuildingPath(BuildingSite buildingSite, Point startPoint) {
		this.buildingSite = buildingSite;
		StackEntry first = new StackEntry();
		first.currentWaypoint = startPoint;
		stack.push(first);
	}

	public void push() {
		StackEntry entry = new StackEntry();
		entry.currentWaypoint = stack.peek().currentWaypoint;
		stack.push(entry);
		buildingSite.push();
	}

	/**
	 * Applies all changes made in the deepest sandbox
	 * 
	 * @throws java.lang.IllegalStateException pop is called more often than push
	 */
	public void popAndApply() {
		if (!canPop())
			throw new IllegalStateException("Tried to call pop more often than push");

		StackEntry entry = stack.pop();
		stack.pop();
		stack.push(entry);
		buildingSite.popAndApply();
	}

	/**
	 * Discards the changes made in the deepest sandbox
	 * 
	 * @throws java.lang.IllegalStateException pop is called more often than push
	 */
	public void popAndDiscard() {
		if (!canPop())
			throw new IllegalStateException("Tried to call pop more often than push");
		stack.pop();
		buildingSite.popAndDiscard();
	}

	public boolean canPop() {
		return stack.size() > 1 && buildingSite.canPop();
	}

	public boolean setWaypoint(Point position) {
		if (!buildingSite.isFree(position) || !buildingSite.isSafe(position))
			return false;

		StackEntry entry = stack.peek();
		List<Point> trace = getTrace(entry.currentWaypoint, position);
		if (trace == null)
			return false;

		for (Point p : trace) {
			getBuildingSite().keepFree(p);
		}

		entry.lastTrace = trace;
		entry.currentWaypoint = position;
		buildingSite.keepFree(position);
		entry.reachablePositions = null;
		return true;
	}

	public Point getCurrentWaypoint() {
		return stack.peek().currentWaypoint;
	}

	public boolean place(Component component, Point position) {
		boolean success = buildingSite.place(component, position);
		if (success)
			stack.peek().reachablePositions = null;
		return success;
	}

	public BuildingSite getBuildingSite() {
		return buildingSite;
	}

	public ComponentCollection getComponents() {
		return buildingSite.getComponents();
	}

	public List<Point> getReachablePositions() {
		StackEntry entry = stack.peek();
		if (entry.reachablePositions == null)
			entry.reachablePositions = calculateReachablePositions();
		return entry.reachablePositions;
	}

	public List<Point> getLastTrace() {
		return stack.peek().lastTrace;
	}

	private List<Point> calculateReachablePositions() {
		List<Point> list = new ArrayList<Point>();
		Point s = getCurrentWaypoint();
		Parabola parabola = new Parabola(getParabolaFactor(), getJumpApex());

		for (int y = (int) jumpApex.getY(); y >= -MAX_REACHABLE_DISTANCE; y--) {
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
					Point p = new Point(x, source.getY());
					if (!getBuildingSite().isSafe(p) || !getBuildingSite().isFree(p))
						return null;
					list.add(p);
				}
				System.out.println("    Works with straight move");
				return list;
			} else
				return null;
		}
	}

	private class ClimbStrategy implements MovingStrategy {
		public List<Point> getTrace(Point source, Point target) {
			if (source.getX() == target.getX()) {
				List<Point> list = new ArrayList<Point>();
				int min = Math.min(source.getY(), target.getY());
				int max = Math.max(source.getY(), target.getY());
				for (int y = min; y <= max; y++) {
					Point p = new Point(source.getX(), y);
					if (!getBuildingSite().isSafe(p) || !getBuildingSite().isFree(p))
						return null;
					list.add(p);
				}
				System.out.println("    Works with climbing");
				return list;
			} else
				return null;
		}
	}

	private abstract class FallStrategy implements MovingStrategy {
		protected List<Point> getTrace(Point source, Point target, boolean jump) {
			if (!jump && (target.getY() >= source.getY()))
				return null;
			if (jump && (target.getY() > source.getY() + getJumpApex().getY())) {
				System.out.println("   Target too high");
				return null;
			}

			List<Point> list = new ArrayList<Point>();
			int dir = target.getX() > source.getX() ? 1 : -1;

			Vector apex = source.toVector().add(dir > 0 ? 1 : 0, 0); // bottom center in worst case (small feet)
			if (jump)
				apex = apex.add(getJumpApex().multiply(dir, 1));
			Parabola parabola = new Parabola(getParabolaFactor(), apex);

			boolean isTargetBeforeApex = 
				((dir > 0 && target.getX() < apex.getX())
				|| (dir < 0 && target.getX() + 1 > apex.getX())); // +1: right edge of target

			int min = Math.min(source.getX(), target.getX());
			int max = Math.max(source.getX(), target.getX());
			for (int x = min; x <= max; x++) {
				float centerX = x + 0.5f;
				float innerX = centerX - 0.5f * dir;
				float outerX = centerX + 0.5f * dir;
				float bodyInnerX = innerX - 0.5f * dir; // worst-case parabola, large body
				float bodyOuterX = outerX + dir; // best-case parabola, large body
				bodyInnerX = Math.min(max + 1, Math.max(min, bodyInnerX)); // max + 1 because right-most block is 1 wide 
				bodyOuterX = Math.min(max + 1, Math.max(min, bodyOuterX));
				
				int minY = (int)Math.floor(parabola.getMinY(bodyInnerX, bodyOuterX));
				int maxY = (int)Math.ceil(parabola.getMaxY(bodyInnerX, bodyOuterX));

				// Never fall "through" the safe source or target position
				// If before apex, then source, otherwise target
				// x == source.getX() is required because apex.getX() == outerX when falling
				boolean isBeforeApex = Math.signum(apex.getX() - outerX) == dir; 
				if (x == source.getX() || isBeforeApex)
					minY = Math.max(minY, source.getY());
				else 
					minY = Math.max(minY, target.getY());
				
				if (isTargetBeforeApex && x == target.getX() - dir) {
					// Maybe the player has to stop before the target and wait until it falls down again
					maxY = Math.max(maxY, (int)Math.ceil(apex.getY()));
				} else if (x == target.getX()) {
					// Make sure that player is above target
					// If the position is before the apex, take the apex (the player then doesn't move any more forward)
					float theX = isTargetBeforeApex ? apex.getX() : innerX;
					float theY = parabola.getY(theX);

					minY = target.getY();
						
					if (isTargetBeforeApex) {
						// Player ends the upwards-moving before or at the target and then falls down
						maxY =  Math.max(maxY, (int)Math.ceil(apex.getY()));
					} else {
						// The player moves until the target and then falls down
					}
					
					if (theY < target.getY()) {
						System.out.printf("    Not reaching target (%d), only at height %f\n", target.getY(), theY);;
						return null;
					}
				}
				
				//System.out.printf("%d: %d - %d\n", x, minY, maxY);

				for (int y = minY; y <= maxY; y++) {
					Point p = new Point(x, y);
					if (!getBuildingSite().isFree(p)) {
						System.out.println("    Blocked: " + p);
						return null;
					}
					list.add(p);
				}
			}
			System.out.println("    Works" + (jump ? " with jumping" : " without jumping"));
			return list;
		}
	}
	
	// Alternative code, using RectTrace
	/*
	 * private abstract class FallStrategy implements MovingStrategy {
		protected List<Point> getTrace(Point source, Point target, boolean jump) {
			if (!jump && (target.getY() >= source.getY()))
				return null;
			if (jump && (target.getY() > source.getY() + getJumpApex().getY())) {
				System.out.println("   Target too high");
				return null;
			}

			List<Point> list = new ArrayList<Point>();
			int dir = target.getX() > source.getX() ? 1 : -1;

			Vector apex = source.toVector().add(dir > 0 ? 1 : 0, 0); // bottom center in worst case (small feet)
			if (jump)
				apex = apex.add(getJumpApex().multiply(dir, 1));
			Parabola parabola = new Parabola(getParabolaFactor(), apex);
			
			// the dimensions include all possibilities (small & large feet, small & large bocy)
			Rect rectDimensions = new Rect(0, 0, 1.5f, 1);
			RectTrace trace = new RectTrace(parabola, rectDimensions, source.getX(), target.getX());

			boolean isTargetBeforeApex = 
				((dir > 0 && target.getX() < apex.getX())
				|| (dir < 0 && target.getX() + 1 > apex.getX())); // +1: right edge of target

			IntegerRange xRange = trace.getColumnRange();
			for (int x = xRange.getMin(); x <= xRange.getMax(); x++) {
				float centerX = x + 0.5f;
				float innerX = centerX - 0.5f * dir;
				float outerX = centerX + 0.5f * dir;
				IntegerRange yRange = trace.getColumnYRange(x);
				int minY = yRange.getMin();
				int maxY = yRange.getMax();

				// Never fall "through" the safe source or target position
				// If before apex, then source, otherwise target
				// x == source.getX() is required because apex.getX() == outerX when falling
				boolean isBeforeApex = Math.signum(apex.getX() - outerX) == dir; 
				if (x == source.getX() || isBeforeApex)
					minY = Math.max(minY, source.getY());
				else 
					minY = Math.max(minY, target.getY());
				
				if (isTargetBeforeApex && x == target.getX() - dir) {
					// Maybe the player has to stop before the target and wait until it falls down again
					maxY = Math.max(maxY, (int)Math.ceil(apex.getY()));
				} else if (x == target.getX()) {
					// Make sure that player is above target
					// If the position is before the apex, take the apex (the player then doesn't move any more forward)
					float theX = isTargetBeforeApex ? apex.getX() : innerX;
					float theY = parabola.getY(theX);

					minY = target.getY();
						
					if (isTargetBeforeApex) {
						// Player ends the upwards-moving before or at the target and then falls down
						maxY =  Math.max(maxY, (int)Math.ceil(apex.getY()));
					} else {
						// The player moves until the target and then falls down
					}
					
					if (theY < target.getY()) {
						System.out.printf("    Not reaching target (%d), only at height %f\n", target.getY(), theY);;
						return null;
					}
				}
				
				//System.out.printf("%d: %d - %d\n", x, minY, maxY);

				for (int y = minY; y <= maxY; y++) {
					Point p = new Point(x, y);
					if (!getBuildingSite().isFree(p)) {
						System.out.println("    Blocked: " + p);
						return null;
					}
					list.add(p);
				}
			}
			System.out.println("    Works" + (jump ? " with jumping" : " without jumping"));
			return list;
		}
	}
	 */

	private class FreeFallStrategy extends FallStrategy {
		public List<Point> getTrace(Point source, Point target) {
			return super.getTrace(source, target, false);
		}
	}

	private class JumpStrategy extends FallStrategy {
		public List<Point> getTrace(Point source, Point target) {
			return super.getTrace(source, target, true);
		}
	}

	@SuppressWarnings("serial")
	private List<MovingStrategy> movingStrategies = new ArrayList<MovingStrategy>() {
		{
			add(new StraightMoveStrategy());
			add(new ClimbStrategy());
			add(new FreeFallStrategy());
			add(new JumpStrategy());
		}
	};

	/**
	 * @return null if moving is not possible
	 */
	private List<Point> getTrace(Point source, Point target) {
		for (MovingStrategy strategy : movingStrategies) {
			List<Point> trace = strategy.getTrace(source, target);
			if (trace != null)
				return trace;
		}
		return null;
	}

	private Vector jumpApex;

	private Vector getJumpApex() {
		if (jumpApex == null)
			jumpApex = calculateJumpApex();
		return jumpApex;
	}
	
	/**
	 * Gets the factor to be used in a parabola describing free fall or jumping
	 * 
	 * @return the factor (always positive)
	 */
	private float getParabolaFactor() {
		// x = v*t => t = x/v
		// y = 0.5*g*t^2 => y = 0.5 * g * (x/v)^2 = 0.5 * g/v^2 * x^2
		return -0.5f * Config.GRAVITY_ACCELERATION / Config.PLAYER_SPEED / Config.PLAYER_SPEED;
	}

	private Vector calculateJumpApex() {
		// Jump apex (Maximum turning point)
		float apexTime = Config.PLAYER_JUMP_SPEED / Config.GRAVITY_ACCELERATION; // v0 = g*t
		float apexY = 0.5f * Config.GRAVITY_ACCELERATION * apexTime * apexTime; // s = 0.5*a*t^2
		float apexX = apexTime * Config.PLAYER_SPEED;
		return new Vector(apexX, apexY);
	}
}

package de.yogularm.building;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import de.yogularm.Config;
import de.yogularm.components.Component;
import de.yogularm.components.ComponentCollection;
import de.yogularm.components.debug.ParabolaComponent;
import de.yogularm.geometry.Parabola;
import de.yogularm.geometry.Point;
import de.yogularm.geometry.Vector;

// TODO: BuildingPath should support push/pop for fields like currentWaypoint and reachablePositions

public class BuildingPath {
	private class StackEntry {
		private Point currentWaypoint;
		private List<Point> reachablePositions;
		private List<Point> lastTrace;
	}

	private BuildingSite buildingSite;
	private Deque<StackEntry> stack = new ArrayDeque<StackEntry>();

	private static final int MAX_REACHABLE_DISTANCE = 5;

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

	public void place(Component component, Point position) {
		buildingSite.place(component, position);
		stack.peek().reachablePositions = null;
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
		Vector c = s.toVector().add(0.5f, 0); // Center of player start

		Vector jumpApex = getJumpApex();
		// x = v*t => t = x/v
		// y = 0.5*g*t^2 => y = 0.5 * g * (x/v)^2 = 0.5 * g/v^2 * x^2
		float a = -0.5f * Config.GRAVITY_ACCELERATION / Config.PLAYER_SPEED / Config.PLAYER_SPEED;
		Parabola parabola = new Parabola(a, jumpApex);

		if (Config.DEBUG_BUILDING) {
			ParabolaComponent parabolaComponent =
			  new ParabolaComponent(getComponents(), parabola, 0, 20, 20);
			parabolaComponent.setPosition(c);
			getComponents().add(parabolaComponent);
		}

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

	private abstract class FallMoveStrategy implements MovingStrategy {
		protected List<Point> getTrace(Point source, Point target, boolean jump) {
			if (!jump && (target.getY() >= source.getY()))
				return null;
			if (jump && (target.getY() > source.getY() + getJumpApex().getY()))
				return null;

			List<Point> list = new ArrayList<Point>();
			int dir = target.getX() > source.getX() ? 1 : -1;

			// x = v*t => t = x/v
			// y = 0.5*g*t^2 => y = 0.5 * g * (x/v)^2 = 0.5 * g/v^2 * x^2
			// Apex is source, factor a is 0.5 * g/v^2
			float a = -0.5f * Config.GRAVITY_ACCELERATION / Config.PLAYER_SPEED / Config.PLAYER_SPEED;
			Vector apex = source.toVector().add(dir > 0 ? 1 : 0, 0);
			if (jump)
				apex = apex.add(getJumpApex().multiply(dir, 1));
			Parabola parabola = new Parabola(a, apex); // bottom center
			/*Parabola bottomInner = parabola.move(new Vector(-dir, 0));
			Parabola topOuter = parabola.move(new Vector(dir, 1));
			Parabola bottomOuter = parabola.move(new Vector(dir, 0));*/
			if (Config.DEBUG_BUILDING) {
				int debugMin = -100;
				int debugMax = 100;
				// source.getX() + dir > 0 ? 0 : 1, target.getX() + 5 * dir
				getComponents().add(new ParabolaComponent(getComponents(), parabola, debugMin, debugMax, 10));
				/*getComponents().add(new ParabolaComponent(getComponents(), parabola, debugMin, debugMax, 10));
				getComponents().add(new ParabolaComponent(getComponents(), bottomInner, debugMin, debugMax, 10));
				getComponents().add(new ParabolaComponent(getComponents(), topOuter, debugMin, debugMax, 10));*/
			}

			int min = Math.min(source.getX() + (!jump ? dir : 0), target.getX());
			int max = Math.max(source.getX() + (!jump ? dir : 0), target.getX());
			for (int x = min; x <= max; x++) {
				int innerX = Math.min(max, Math.max(min, x - dir));
				int outerX = Math.min(max, Math.max(min, x + dir));
				
				int minY = (int)Math.floor(parabola.min(innerX, outerX));
				int maxY = (int)Math.ceil(parabola.max(innerX, outerX)); 
				
				/*// Just (int) does not behave like floor!
				int minY = Math.max(target.getY(), (int) Math.floor(bottomInner.getY(outerX)));
				// -1 because x and y represent the grid positions
				int maxY = (int) Math.ceil(topOuter.getY(innerX) - 1);*/

				// Target reached, now break and fall down to the target
				if (x == target.getX()) {
					// Make sure that player is above target
					// Don't use minY becuase it's enaugh to reach the target with the front
					int innerMinY = (int)Math.floor(parabola.min(x, innerX));
					if (innerMinY < target.getY())
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
		}
	}

	private class FreeFallMoveStrategy extends FallMoveStrategy {
		public List<Point> getTrace(Point source, Point target) {
			return super.getTrace(source, target, false);
		}
	}

	private class JumpMoveStrategy extends FallMoveStrategy {
		public List<Point> getTrace(Point source, Point target) {
			return super.getTrace(source, target, true);
		}
	}

	@SuppressWarnings("serial")
	private List<MovingStrategy> movingStrategies = new ArrayList<MovingStrategy>() {
		{
			add(new StraightMoveStrategy());
			add(new FreeFallMoveStrategy());
			add(new JumpMoveStrategy());
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

	private Vector calculateJumpApex() {
		// Jump apex (Maximum turning point)
		float apexTime = Config.PLAYER_JUMP_SPEED / Config.GRAVITY_ACCELERATION; // v0 = g*t
		float apexY = 0.5f * Config.GRAVITY_ACCELERATION * apexTime * apexTime; // s = 0.5*a*t^2
		float apexX = apexTime * Config.PLAYER_SPEED;
		return new Vector(apexX, apexY);
	}
}

package de.yogularm.building;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.yogularm.Config;
import de.yogularm.components.Component;
import de.yogularm.components.ComponentCollection;
import de.yogularm.components.general.Platform;
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

	public boolean setWaypoint(Point target) {
		return setWaypointUsingPlatform(target, null);
	}
	
	public boolean setWaypointUsingPlatform(Point target, Platform platform) {
		if (!buildingSite.isFree(target) || !buildingSite.isSafe(target))
			return false;

		StackEntry entry = stack.peek();
		List<Point> trace = getTrace(new MovingDetails(entry.currentWaypoint, target, platform));
		if (trace == null)
			return false;

		for (Point p : trace) {
			getBuildingSite().keepFree(p);
		}

		entry.lastTrace = trace;
		entry.currentWaypoint = target;
		buildingSite.keepFree(target);
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
			entry.reachablePositions = calculateReachablePositions(getCurrentWaypoint());
		return entry.reachablePositions;
	}

	public List<Point> getLastTrace() {
		return stack.peek().lastTrace;
	}

	public List<Point> calculateReachablePositions(Point origin) {
		List<Point> list = new ArrayList<Point>();
		Parabola parabola = new Parabola(getParabolaFactor(), getJumpApex());

		for (int y = (int) jumpApex.getY(); y >= -MAX_REACHABLE_DISTANCE; y--) {
			float maxX = parabola.getX2(y);
			for (int x = 1; x <= maxX; x++) {
				Point p = origin.add(x, y);
				if (getBuildingSite().isFree(p))
					list.add(p);
				p = origin.add(-x, y);
				if (getBuildingSite().isFree(p))
					list.add(p);
			}
		}

		return list;
	}
	
	@SuppressWarnings("serial")
	private Collection<MovingStrategy> movingStrategies = new ArrayList<MovingStrategy>() {
		{
			add(new StraightMoveStrategy(BuildingPath.this));
			add(new ClimbStrategy(BuildingPath.this));
			add(new FreeFallStrategy(BuildingPath.this));
			add(new JumpStrategy(BuildingPath.this));
			add(new PlatformMovingStrategy(BuildingPath.this));
		}
	};

	/**
	 * @return null if moving is not possible
	 */
	List<Point> getTrace(MovingDetails move) {
		for (MovingStrategy strategy : movingStrategies) {
			if (move.platform == null || strategy instanceof PlatformMovingStrategy) {
				List<Point> trace = strategy.getTrace(move);
				if (trace != null)
					return trace;
			}
		}
		return null;
	}

	private Vector jumpApex;

	Vector getJumpApex() {
		if (jumpApex == null)
			jumpApex = calculateJumpApex();
		return jumpApex;
	}
	
	/**
	 * Gets the factor to be used in a parabola describing free fall or jumping
	 * 
	 * @return the factor (always positive)
	 */
	float getParabolaFactor() {
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

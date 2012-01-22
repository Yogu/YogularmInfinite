package de.yogularm.building;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.yogularm.geometry.Point;
import de.yogularm.geometry.Rect;
import de.yogularm.geometry.RectTrace;
import de.yogularm.geometry.Straight;
import de.yogularm.geometry.Vector;

class PlatformMovingStrategy implements MovingStrategy {
	private final BuildingPath buildingPath;

	PlatformMovingStrategy(BuildingPath buildingPath) {
		this.buildingPath = buildingPath;
	}

	private class IndexedVector {
		public final int index;
		public final Vector vector;

		public IndexedVector(int index, Vector vector) {
			this.index = index;
			this.vector = vector;
		}
	}

	public List<Point> getTrace(final MovingDetails move) {
		if (move.platform == null)
			return null;
		System.out.println("   Trying move with platform");
		
		Vector[] targets = move.platform.getTargets();
		Vector origin = move.platform.getOrigin();
		List<IndexedVector> sortedTargets = getSortedTargets(move);
		
		for (IndexedVector startTarget : sortedTargets) {
			MovingDetails firstMove = new MovingDetails(move.source, startTarget.vector.toPoint(), null);
			Collection<Point> firstTrace = buildingPath.getTrace(firstMove);
			if (firstTrace != null) {
				System.out.println("   Can reach platform target " + startTarget.index);
				Collection<Point> secondTrace = new ArrayList<Point>();
				Vector lastTarget = startTarget.vector;
				for (int i = 1; i < targets.length; i++) {
					int currentIndex = (startTarget.index + i) % targets.length; // rotating
					Vector currentTarget = origin.add(targets[currentIndex]);
					Collection<Point> newTrace = getRidingTrace(lastTarget, currentTarget);
					secondTrace.addAll(newTrace);
					if (buildingPath.getBuildingSite().areFree(secondTrace)) {
						System.out.println("   Can ride to target " + currentIndex);
						MovingDetails thirdMove = new MovingDetails(currentTarget.toPoint(), move.target, null);
						Collection<Point> thirdTrace = buildingPath.getTrace(thirdMove);
						if (thirdTrace != null) {
							System.out.println("   Works with platform");
							List<Point> totalTrace = new ArrayList<Point>();
							totalTrace.addAll(firstTrace);
							totalTrace.addAll(secondTrace);
							totalTrace.addAll(thirdTrace);
							return totalTrace;
						}
					}
					lastTarget = currentTarget;
				}
			}
		}
		
		return null;

		// for each platform stop:
		// is reachable?
		// for each following platform stop:
		// can be ridden?
		// is target reachable up from there?
	}
	
	private List<IndexedVector> getSortedTargets(final MovingDetails move) {
		Vector[] targets = move.platform.getTargets();
		Vector origin = move.platform.getOrigin();
		List<IndexedVector> sortedTargets = new ArrayList<IndexedVector>();
		for (int i = 0; i < targets.length; i++) {
			sortedTargets.add(new IndexedVector(i, targets[i].add(origin)));
		}

		Collections.sort(sortedTargets, new Comparator<IndexedVector>() {
			public int compare(IndexedVector o1, IndexedVector o2) {
				float distance1 =
				  Vector.getDistance(o1.vector.add(move.platform.getOrigin()), move.source.toVector());
				float distance2 =
				  Vector.getDistance(o2.vector.add(move.platform.getOrigin()), move.source.toVector());
				return Float.compare(distance1, distance2);
			}
		});
		return sortedTargets;
	}
	
	private Collection<Point> getRidingTrace(Vector source, Vector target) {
		// note to x: whether standing near left or right edge
		// note to y: source&target are platform positions, player is standing on the platform
		Rect dimensions = new Rect(-0.5f, 1, 1.5f, 2);
		Straight straight = new Straight(source, target);
		RectTrace trace = new RectTrace(straight, dimensions, source.getX(), target.getX());
		return trace.getCollidingCells();
	}
}
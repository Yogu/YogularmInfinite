package de.yogularm.building;

import java.util.ArrayList;
import java.util.List;

import de.yogularm.geometry.Parabola;
import de.yogularm.geometry.Point;
import de.yogularm.geometry.Vector;

abstract class FallStrategy implements MovingStrategy {
  private final BuildingPath buildingPath;
  FallStrategy(BuildingPath buildingPath) {
    this.buildingPath = buildingPath;
  }

	protected List<Point> getTrace(MovingDetails move, boolean jump) {
		Point source = move.source;
		Point target = move.target;
		if (!jump && (target.getY() >= source.getY()))
			return null;
		if (jump && (target.getY() > source.getY() + this.buildingPath.getJumpApex().getY())) {
			System.out.println("    Target too high");
			return null;
		}

		List<Point> list = new ArrayList<Point>();
		int dir = target.getX() > source.getX() ? 1 : -1;

		Vector apex = source.toVector().add(dir > 0 ? 1 : 0, 0); // bottom center in worst case (small feet)
		if (jump)
			apex = apex.add(this.buildingPath.getJumpApex().multiply(dir, 1));
		Parabola parabola = new Parabola(this.buildingPath.getParabolaFactor(), apex);

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
				if (!this.buildingPath.getBuildingSite().isFree(p)) {
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
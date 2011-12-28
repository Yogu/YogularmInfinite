package de.yogularm.geometry;

public class RectPath {
	private Rect source;
	private Rect target;
	private Rect bounds;
	private Straight topStraight;
	private Straight topLeftStraight;
	private Straight topRightStraight;
	private Straight bottomStraight;
	private Straight bottomLeftStraight;
	private Straight bottomRightStraight;
	
	public RectPath(Vector source, Vector target, Vector size) {
		if (source == null)
			throw new NullPointerException("source is null");
		if (target == null)
			throw new NullPointerException("target is null");
		if (size == null)
			throw new NullPointerException("size is null");
		if (size.getX() < 0 || size.getY() < 0)
			throw new IllegalArgumentException("At least one component of size is negative");
		
		this.source = new Rect(source, source.add(size));
		this.target = new Rect(target, target.add(size));
		
		bounds = measureBounds();
		createStraights();
	}
	
	private Rect measureBounds() {
		float left = Math.min(source.getLeft(), target.getLeft());
		float right = Math.max(source.getRight(), target.getRight());
		float bottom = Math.min(source.getBottom(), target.getBottom());
		float top = Math.max(source.getTop(), target.getTop());
		return new Rect(left, top, right, bottom);
	}
	
	private void createStraights() {
		topLeftStraight = new Straight(source.getTopLeft(), target.getTopLeft());
		topRightStraight = new Straight(source.getTopRight(), target.getTopRight());
		if (topLeftStraight.isVertical() || topLeftStraight.getY(0) > topRightStraight.getY(0)) {
			topStraight = topLeftStraight;
		} else {
			topStraight = topRightStraight;
		}

		bottomLeftStraight = new Straight(source.getBottomLeft(), target.getBottomLeft());
		bottomRightStraight = new Straight(source.getBottomRight(), target.getBottomRight());
		if (bottomLeftStraight.isVertical() || bottomLeftStraight.getY(0) > bottomRightStraight.getY(0)) {
			bottomStraight = bottomLeftStraight;
		} else {
			bottomStraight = bottomRightStraight;
		}
	}
	
	public boolean overlaps(Rect other) {
		if (!other.overlaps(bounds))
			return false;
		
		if (topStraight.isVertical() || topStraight.isHorizontal())
			return true;
		else
			return (
					   topStraight.isAbove(other.getTopLeft())
					|| topStraight.isAbove(other.getTopRight())
					|| topStraight.isAbove(other.getBottomLeft())
					|| topStraight.isAbove(other.getBottomRight()))
				&& (
						 bottomStraight.isBelow(other.getTopLeft())
					|| bottomStraight.isBelow(other.getTopRight())
					|| bottomStraight.isBelow(other.getBottomLeft())
					|| bottomStraight.isBelow(other.getBottomRight())
				);
	}
}

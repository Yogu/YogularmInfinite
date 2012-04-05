package de.yogularm.geometry;


public class IntRect {
	private Point min;
	private Point max;

	public IntRect(Point min, Point max) {
		if (min == null)
			throw new IllegalArgumentException("min is null");
		if (max == null)
			throw new IllegalArgumentException("max is null");
		this.min = min;
		this.max = max;
		normalize();
	}

	public IntRect(int x1, int y1, int x2, int y2) {
		min = new Point(x1, y1);
		max = new Point(x2, y2);
		normalize();
	}

	private void normalize() {
		int minX, maxX, minY, maxY;
		minX = Math.min(min.getX(), max.getX());
		maxX = Math.max(min.getX(), max.getX());
		minY = Math.min(min.getY(), max.getY());
		maxY = Math.max(min.getY(), max.getY());
		if (minX != min.getX() || minY != min.getY())
			min = new Point(minX, minY);
		if (maxX != max.getX() || maxY != max.getY())
			max = new Point(maxX, maxY);
	}

	public Point getMin() {
		return min;
	}

	public Point getMax() {
		return max;
	}

	public Point getBottomLeft() {
		return min;
	}

	public Point getBottomRight() {
		return new Point(max.getX(), min.getY());
	}

	public Point getTopLeft() {
		return new Point(min.getX(), max.getY());
	}

	public Point getTopRight() {
		return max;
	}

	public int getLeft() {
		return min.getX();
	}

	public int getRight() {
		return max.getX();
	}

	public int getBottom() {
		return min.getY();
	}

	public int getTop() {
		return max.getY();
	}

	public Point getSize() {
		return new Point(max.getX() - min.getX(), max.getY() - min.getY());
	}

	public int getWidth() {
		return max.getX() - min.getX();
	}

	public int getHeight() {
		return max.getY() - min.getY();
	}

	public boolean equals(IntRect other) {
		return min.equals(other.min) && max.equals(other.max);
	}

	public IntRect add(Point offset) {
		return new IntRect(min.add(offset), max.add(offset));
	}

	public boolean contains(Point point) {
		return point.getX() >= getLeft() && point.getX() <= getRight() && point.getY() >= getBottom()
				&& point.getY() <= getTop();
	}

	@Override
	public int hashCode() {
		return min.hashCode() ^ max.hashCode();
	}

	public String toString() {
		return String.format("Rect[%s, %s]", min, max);
	}
}

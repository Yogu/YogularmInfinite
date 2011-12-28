package de.yogularm.geometry;

import de.yogularm.Config;

public class Rect {
	private Vector minVector;
	private Vector maxVector;
	
	public Rect(Vector minVector, Vector maxVector) {
		if (minVector == null)
			throw new IllegalArgumentException("minVector is null");
		if (maxVector == null)
			throw new IllegalArgumentException("maxVector is null");
		this.minVector = minVector;
		this.maxVector = maxVector;
		normalize();
	}
	
	public Rect(float x1, float y1, float x2, float y2) {
		minVector = new Vector(x1, y1);
		maxVector = new Vector(x2, y2);
		normalize();
	}
	
	private void normalize() {
		float minX, maxX, minY, maxY;
		minX = Math.min(minVector.getX(), maxVector.getX());
		maxX = Math.max(minVector.getX(), maxVector.getX());
		minY = Math.min(minVector.getY(), maxVector.getY());
		maxY = Math.max(minVector.getY(), maxVector.getY());
		if (minX != minVector.getX() || minY != minVector.getY())
			minVector = new Vector(minX, minY);
		if (maxX != maxVector.getX() || maxY != maxVector.getY())
			maxVector = new Vector(maxX, maxY);
	}
	
	public Vector getMinVector() {
		return minVector;
	}
	
	public Vector getMaxVector() {
		return maxVector;
	}
	
	public Vector getBottomLeft() {
		return minVector;
	}
	
	public Vector getBottomRight() {
		return new Vector(maxVector.getX(), minVector.getY());
	}
	
	public Vector getTopLeft() {
		return new Vector(minVector.getX(), maxVector.getY());
	}
	
	public Vector getTopRight() {
		return maxVector;
	}
	
	public float getLeft() {
		return minVector.getX();
	}
	
	public float getRight() {
		return maxVector.getX();
	}
	
	public float getBottom() {
		return minVector.getY();
	}
	
	public float getTop() {
		return maxVector.getY();
	}
	
	public Vector getSize() {
		return new Vector(maxVector.getX() - minVector.getX(), maxVector.getY() - minVector.getY());
	}
	
	public Vector getCenter() {
		return new Vector(
			(maxVector.getX() + minVector.getX()) / 2,
			(maxVector.getY() + minVector.getY()) / 2);
	}
	
	public float getWidth() {
		return maxVector.getX() - minVector.getX();
	}
	
	public float getHeight() {
		return maxVector.getY() - minVector.getY();
	}
	
	public Rect changeSize(Vector size) {
		Vector center = getCenter();
		Vector min = center.subtract(size.divide(2));
		Vector max = center.add(size.divide(2));
		return new Rect(min, max);
	}
	
	public Rect changeCenter(Vector center) {
		Vector size = getSize();
		Vector min = center.subtract(size.divide(2));
		Vector max = center.add(size.divide(2));
		return new Rect(min, max);
	}
	
	public boolean overlaps(Rect other) {
		return
			   other.getMaxVector().getX() > minVector.getX() + Config.EPSILON
			&& other.getMinVector().getX() < maxVector.getX() - Config.EPSILON
			&& other.getMaxVector().getY() > minVector.getY() + Config.EPSILON
			&& other.getMinVector().getY() < maxVector.getY() - Config.EPSILON;
	}
	
	public boolean equals(Rect other) {
		return minVector.equals(other.minVector) && maxVector.equals(other.maxVector);
	}
	
	public Rect add(Vector offset) {
		return new Rect(minVector.add(offset), maxVector.add(offset));
	}
}

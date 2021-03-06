package de.yogularm.geometry;

public class Straight implements NumericFunction {
	private Vector p1;
	private Vector p2;
	
	public Straight(Vector p1, Vector p2) {
		if (p1 == null)
			throw new NullPointerException("p1 is null");
		if (p2 == null)
			throw new NullPointerException("p2 is null");
		
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public boolean isHorizontal() {
		return p1.getY() == p2.getY();
	}
	
	public boolean isVertical() {
		return p1.getX() == p2.getX();
	}
	
	public float getY(float x) {
		// get the percentage x-position of the test point between the vectors
		float relative = (x - p1.getX()) / (p2.getX() - p1.getX());
		
		// use linear interpolation to get the y component
		return p1.getY() * (1 - relative) + p2.getY() * relative;
	}
	
	public float getX(float y) {
		// get the percentage y-position of the test point between the vectors
		float relative = (y - p1.getY()) / (p2.getY() - p1.getY());
		
		// use linear interpolation to get the x component
		return p1.getX() * (1 - relative) + p2.getX() * relative;
	}
	
	public boolean isAbove(Vector point) {
		return getY(point.getX()) > point.getY();
	}
	
	public boolean isBelow(Vector point) {
		return getY(point.getX()) < point.getY();
	}

	public float getMinY(float minX, float maxX) {
		if (isVertical())
			return Math.min(p1.getY(), p2.getY());
		else
			return Math.min(getY(minX), getY(maxX));
	}

	public float getMaxY(float minX, float maxX) {
		if (isVertical())
			return Math.max(p1.getY(), p2.getY());
		else
			return Math.max(getY(minX), getY(maxX));
	}
	
	public String toString() {
		return String.format("Straight through %s and %s", p1, p2);
	}
}

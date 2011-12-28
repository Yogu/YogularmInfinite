package de.yogularm.geometry;

public class Point {
	private int x;
	private int y;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public static Point getZero() {
		return new Point(0, 0);
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public String toString() {
		return x + ", " + y;
	}
	
	public Vector toVector() {
		return new Vector(x, y);
	}
	
	public Point add(Point other) {
		return new Point(x + other.x, y + other.y);	
	}
	
	public Point add(int x, int y) {
		return new Point(this.x + x, this.y + y);
	}
	
	public boolean equals(Point other) {
		return other != null && other.x == x && other.y == y;
	}
	
	public boolean equals(Object other) {
		return other instanceof Point && equals((Point)other);
	}
	
	public int hashCode() {
		return x ^ y;
	}
}

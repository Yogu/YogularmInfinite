package de.yogularm.geometry;

public class Point {
	private int x;
	private int y;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
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

package de.yogularm;

public class Vector {
	private final float x;
	private final float y;
	
	public Vector(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public static Vector getZero() {
		return new Vector(0, 0);
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public Vector changeX(float newX) {
		return new Vector(newX, this.y);
	}
	
	public Vector changeY(float newY) {
		return new Vector(this.x, newY);
	}
	
	public Vector add(Vector other) {
		return new Vector(x + other.x, y + other.y);
	}
	
	public Vector subtract(Vector other) {
		return new Vector(x - other.x, y - other.y);
	}
	
	public Vector multiply(float value) {
		return new Vector(x * value, y * value);
	}
	
	public Vector divide(float value) {
		return new Vector(x / value, y / value);
	}
	
	public float getLength() {
		return (float) Math.sqrt(x * x + y * y);
	}
	
	public boolean isZero() {
		return x == 0 && y == 0;
	}
	
	public static float getDistance(Vector a, Vector b) {
		return b.subtract(a).getLength();
	}
	
	public boolean equals(Vector other) {
		return x == other.x && y == other.y;
	}
	
	public Vector normalize() {
		float length = getLength();
		return new Vector(x / length, y / length);
	}
	
	public float getAngleToXAxis() {
		Vector norm = normalize();
		return (float)Math.toDegrees(Math.atan2(norm.y, norm.x) - Math.atan2(0, -1));
	}
	
	public Vector round() {
		return new Vector(Math.round(x), Math.round(y));
	}
	
	public String toString() {
		return String.format("(%f, %f)", x, y);
	}
}

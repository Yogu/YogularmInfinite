package de.yogularm.geometry;


public class Vector {
	private final float x;
	private final float y;

	public Vector(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public static final Vector ZERO = new Vector(0, 0);

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

	public Vector add(float x, float y) {
		return new Vector(this.x + x,this. y + y);
	}

	public Vector subtract(Vector other) {
		return new Vector(x - other.x, y - other.y);
	}

	public Vector multiply(Vector other) {
		return new Vector(x * other.x, y * other.y);
	}

	public Vector multiply(float x, float y) {
		return new Vector(this.x * x, this.y * y);
	}

	public Vector multiply(float value) {
		return new Vector(x * value, y * value);
	}

	public Vector divide(float value) {
		return new Vector(x / value, y / value);
	}
	
	public Vector negate() {
		return new Vector(-x, -y);
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
	
	public boolean equals(Object other) {
		if (other instanceof Vector)
			return equals((Vector)other);
		else
			return super.equals(other);
	}
	
	public int hashCode() {
		// copyied from Point2D.hashCode();
    long bits = Double.doubleToLongBits(x);
    bits ^= Double.doubleToLongBits(y) * 31;
    return (((int) bits) ^ ((int) (bits >> 32)));
	}

	public Vector normalize() {
		float length = getLength();
		if (length == 1)
			return this;
		else
			return new Vector(x / length, y / length);
	}

	/**
	 * 
	 * @return the the angle in degrees
	 */
	public float getAngleToXAxis() {
		Vector norm = normalize();
		return (float) Math.toDegrees(Math.atan2(norm.y, norm.x));
				//- Math.atan2(0, -1));
	}

	/**
	 * 
	 * @return the the angle in degrees
	 */
	public static float getAngle(Vector v1, Vector v2) {
    return (float)Math.toDegrees(Math.acos(getDotProduct(v1.normalize(), v2.normalize())));
	}
	
	public static float getDotProduct(Vector v1, Vector v2) {
		return v1.x * v2.x + v1.y * v2.y;
	}

	public Vector round() {
		return new Vector(Math.round(x), Math.round(y));
	}

	/**
	 * Rounds both components down and returns the new vector
	 * 
	 * @return the vector with both components rounded down
	 */
	public Vector floor() {
		return new Vector((float)Math.floor(x), (float)Math.floor(y));
	}

	/**
	 * Rounds both components up and returns the new vector
	 * 
	 * @return the vector with both components rounded up
	 */
	public Vector ceil() {
		return new Vector((float)Math.ceil(x), (float)Math.ceil(y));
	}

	/**
	 * Formats this vector to a string, including x and y component
	 * 
	 * @return a string that represents this vector
	 */
	public String toString() {
		return String.format("(%f, %f)", x, y);
	}

	/**
	 * Gets the value of the given component
	 * 
	 * @param axis the axis that specifies the component (x or y)
	 * @return the value of the component
	 */
	public float getComponent(Axis axis) {
		switch (axis) {
		case HORIZONTAL:
			return getX();
		case VERTICAL:
			return getY();
		default:
			return 0;
		}
	}

	/**
	 * Changes the value of the given axis and returns the new vector
	 * 
	 * @param axis the axis whose component to change
	 * @param value the component's new value
	 * @return the new vector with the changed component
	 */
	public Vector changeComponent(Axis axis, float value) {
		switch (axis) {
		case HORIZONTAL:
			return changeX(value);
		case VERTICAL:
			return changeY(value);
		default:
			return this;
		}
	}
	
	/**
	 * Gets the direction this vector points to, if it is parallel to one of the axes
	 * 
	 * @return the direction of this vector, or Direction.NONE, if this vector is
	 *   neither parallel to x axis, nor to y axis.
	 */
	public Direction getDirection() {
		if (x == 0 && y < 0)
			return Direction.DOWN;
		else if (x == 0 && y > 0)
			return Direction.UP;
		else if (y == 0 && x > 0)
			return Direction.RIGHT;
		else if (y == 0 & x < 0)
			return Direction.LEFT;
		else
			return Direction.NONE;
	}
}

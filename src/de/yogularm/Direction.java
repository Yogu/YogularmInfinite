package de.yogularm;

/**
 * Indicates a direction parallel to x or y axis
 */
public enum Direction {
	NONE, UP, DOWN, LEFT, RIGHT;

	/**
	 * Gets the axis this direction is parallel to
	 * 
	 * @return the axis or Axis.NONE, if this direction is NONE
	 */
	public Axis getAxis() {
		switch (this) {
		case UP:
		case DOWN:
			return Axis.VERTICAL;
		case LEFT:
		case RIGHT:
			return Axis.HORIZONTAL;
		default:
			return Axis.NONE;
		}
	}
}

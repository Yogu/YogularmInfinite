package de.yogularm.input;

public interface Input {
	/**
	 * Gets the x component of the joystick position
	 * 
	 * @return a value between -1 and 1, where -1 indicates left and 1 indicates right
	 */
	float getXControl();

	/**
	 * Gets the y component of the joystick position
	 * 
	 * @return a value between -1 and 1, where -1 indicates down and 1 indicates up
	 */
	float getYControl();
}

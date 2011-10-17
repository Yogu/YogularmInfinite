package de.yogularm.input;

public abstract class DigitalInput implements Input {
	public abstract boolean isLeft();
	public abstract boolean isRight();
	public abstract boolean isUp();
	public abstract boolean isDown();

	public float getXControl() {
		if (isLeft() && !isRight())
			return -1;
		else if (isRight() && !isLeft())
			return 1;
		else
			return 0;
	}
	
	public float getYControl() {
		if (isDown() && !isUp())
			return -1;
		else if (isUp() && !isDown())
			return 1;
		else
			return 0;
	}
}

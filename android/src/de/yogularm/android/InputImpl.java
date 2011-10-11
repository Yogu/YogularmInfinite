package de.yogularm.android;

import de.yogularm.input.Input;

public class InputImpl implements Input {
	private boolean isLeft;
	private boolean isRight;
	private boolean isUp;
	private boolean isDown;

	@Override
	public boolean isLeft() {
		return isLeft;
	}

	@Override
	public boolean isRight() {
		return isRight;
	}

	@Override
	public boolean isUp() {
		return isUp;
	}

	@Override
	public boolean isDown() {
		return isDown;
	}
	
	public void setIsLeft(boolean value) {
		isLeft = value;
	}
	
	public void setIsRight(boolean value) {
		isRight = value;
	}
	
	public void setIsUp(boolean value) {
		isUp = value;
	}
	
	public void setIsDown(boolean value) {
		isDown = value;
	}
}

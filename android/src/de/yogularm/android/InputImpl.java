package de.yogularm.android;

import de.yogularm.input.Input;

public class InputImpl implements Input {
	private float x;
	private float y;
	
	public float getXControl() {
		return x;
	}
	
	public float getYControl() {
		return y;
	}
	
	public void setX(float value) {
		x = value;
	}
	
	public void setY(float value) {
		y = value;
	}
	
	public void apply(Input input) {
		x = input.getXControl();
		y = input.getYControl();
	}
}

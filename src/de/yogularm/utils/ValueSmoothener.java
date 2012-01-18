package de.yogularm.utils;

public class ValueSmoothener {
	private double developingValue;
	private double developingTime;
	private double finalValue;
	private double currentValue;
	private long lastSetTime;
	private double maxStepTime;
	
	public ValueSmoothener(double stepTime) {
		maxStepTime = stepTime;
		lastSetTime = System.nanoTime();
	}
	
	public void init(double value) {
		developingValue = 0;
		lastSetTime = System.nanoTime();
		finalValue = value;
	}
	
	public void set(double value) {
		currentValue = value;
		double elapsed = (System.nanoTime() - lastSetTime) / 1000000000.0; // ns to s
		lastSetTime = System.nanoTime();
		developingValue += value * elapsed;
		developingTime += elapsed;
		if (developingTime > maxStepTime) {
			finalValue = developingValue / developingTime;
			developingTime = 0;
			developingValue = 0;
		}
	}
	
	public double getCurrent() {
		return currentValue;
	}
	
	public double getSmooth() {
		return finalValue;
	}
}

package de.yogularm.drawing;

public class Color {
	private float red;
	private float green;
	private float blue;
	private float alpha;
	
	public Color(float red, float green, float blue, float alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	
	public Color(float red, float green, float blue) {
		this(red, green, blue, 1);
	}
	
	public float getRed() {
		return red;
	}
	
	public float getGreen() {
		return green;
	}
	
	public float getBlue() {
		return blue;
	}
	
	public float getAlpha() {
		return alpha;
	}
	
	public Color multiply(Color other) {
		return new Color(red * other.red, green * other.green, blue * other.blue, alpha * other.alpha);
	}
	
	public Color multiply(float factor) {
		return new Color(red * factor, green * factor, blue * factor, alpha * factor);
	}
	
	public static final Color transparent = new Color(0, 0, 0, 0);
	public static final Color black = new Color(0, 0, 0, 1);
	public static final Color white = new Color(1, 1, 1, 1);
}

package de.yogularm.geometry;

public class Parabola {
	// y = a * (x - d)^2 + e
	private float a;
	private float d;
	private float e;
	
	public Parabola(float a, float d, float e) {
		this.a = a;
		this.d = d;
		this.e = e;
	}
	
	public Parabola(float a, Vector apex) {
		this.a = a;
		this.d = apex.getX();
		this.e = apex.getY();
	}
	
	public Parabola(Vector apex, Vector other) {
		d = apex.getX();
		e = apex.getY();

		float x = other.getX();
		float y = other.getY();
		
		// parabola with y = a*(x-d)^2 + e
		// => a = (y-e) / (d-x)^2 (http://www.wolframalpha.com/input/?i=solve+y%3Da*%28x-d%29^2%2Be+for+a)
		a = (y - e) / (d - x) / (d - x);
	}
	
	public String toString() {
		return String.format("y = %f * (x - %f)^2 + %f", a, d, e);
	}
	
	public Parabola move(Vector offset) {
		return new Parabola(a, d + offset.getX(), e + offset.getY());
	}
	
	public float getX1(float y) {
		// x = (ad-sqrt(-a(e-y)))/a (http://www.wolframalpha.com/input/?i=solve+y%3Da*%28x-d%29^2%2Be+for+x)
		return (float)(a * d + Math.sqrt(-a * (e - y))) / a;
	}
	
	public float getX2(float y) {
		// x = (ad+sqrt(-a(e-y)))/a (http://www.wolframalpha.com/input/?i=solve+y%3Da*%28x-d%29^2%2Be+for+x)
		return (float)(a * d - Math.sqrt(-a * (e - y))) / a;
	}
	
	public float getY(float x) {
		// y = a * (x - d)^2 + e
		return a * (x - d) * (x - d) + e;
	}
	
	public float min() {
		if (a < 0)
			return Float.NEGATIVE_INFINITY;
		else
			return e;
	}
	
	public float max() {
		if (a > 0)
			return Float.POSITIVE_INFINITY;
		else
			return e;
	}
	
	/**
	 * Calculates the minimum y value in the specified range
	 * @param minX The left border of the range for x
	 * @param maxX The right border of the range for x
	 * @return The minimum y value within the specified range
	 */
	public float min(float minX, float maxX) {
		float min = Math.min(minX, maxX);
		float max = Math.max(minX, maxX);
		float result = Math.min(getY(minX), getY(maxX));
		
		// If the apex is within the range, include it 
		if (min <= d && d <= max)
			result = Math.min(result, e);
		
		return result;
	}
	
	/**
	 * Calculates the maximum y value in the specified range
	 * @param minX The left border of the range for x
	 * @param maxX The right border of the range for x
	 * @return The maximum y value within the specified range
	 */
	public float max(float minX, float maxX) {
		float min = Math.min(minX, maxX);
		float max = Math.max(minX, maxX);
		float result = Math.max(getY(minX), getY(maxX));
		
		// If the apex is within the range, include it 
		if (min <= d && d <= max)
			result = Math.max(result, e);
		
		return result;
	}
	
	public Vector getApex() {
		return new Vector(d, e);
	}
	
	public Parabola changeApex(Vector newApex) {
		return new Parabola(a, newApex);
	}
}
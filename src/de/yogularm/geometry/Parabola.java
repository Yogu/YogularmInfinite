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
}
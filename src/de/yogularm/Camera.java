package de.yogularm;

import de.yogularm.drawing.RenderContext;


public class Camera {
	private Rect bounds;
	
	public Camera() {
		bounds = new Rect(0, 0, 0 ,0);
	}
	
	public Rect getBounds() {
		return bounds;
	}
	
	public void setBounds(Rect bounds) {
		if (bounds == null)
			throw new NullPointerException("center is null");
		
		this.bounds = bounds;
	}
	
	public void applyMatrix(RenderContext context) {
		context.resetTranformation();
		context.translate(bounds.getMinVector().negate());
	}
	
	public void scroll(Vector target, float elapsedTime) {
		Vector distance = target.subtract(bounds.getCenter());
		// let player move the center quarter of the screen without scrolling
		distance = distance.changeX(Math.signum(distance.getX()) * Math.max(0, Math.abs(distance.getX()) - bounds.getWidth() * Config.SCROLL_BUFFER / 2 ));
		distance = distance.changeY(Math.signum(distance.getY()) * Math.max(0, Math.abs(distance.getY()) - bounds.getHeight() * Config.SCROLL_BUFFER / 2 ));
		bounds = bounds.add(distance.multiply(Config.SCROLL_SPEED * elapsedTime));
	}
}

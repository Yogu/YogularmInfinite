package de.yogularm;

import de.yogularm.drawing.RenderContext;

public class Camera {
	private Rect bounds;
	
	public Camera() {
		bounds = new Rect(0, 0, 1 ,1);
	}
	
	/**
	 * Gets a rectangle that covers the whole area the camera displays
	 * 
	 * @return The camera's display bounds
	 */
	public Rect getBounds() {
		return bounds;
	}

	/**
	 * Sets a rectangle that covers the whole area the camera displays
	 * 
	 * @param bounds The new display bounds
	 */
	public void setBounds(Rect bounds) {
		if (bounds == null)
			throw new NullPointerException("bounds is null");
		
		this.bounds = bounds;
	}
	
	/**
	 * Transforms the given render context to render the scene as seen from this camera
	 * 
	 * @param context The render context to modify
	 */
	public void applyMatrix(RenderContext context) {
		context.resetTranformation();
		context.translate(bounds.getMinVector().negate());
	}
	
	/**
	 * Moves the camera to make sure that the target is visible
	 * 
	 * @param target The position to focus on
	 * @param elapsedTime The time in seconds that has been elapsed since last call
	 */
	public void scroll(Vector target, float elapsedTime) {
		Vector distance = target.subtract(bounds.getCenter());
		// let player move the center quarter of the screen without scrolling
		distance = distance.changeX(Math.signum(distance.getX()) * Math.max(0, Math.abs(distance.getX()) - bounds.getWidth() * Config.SCROLL_BUFFER / 2 ));
		distance = distance.changeY(Math.signum(distance.getY()) * Math.max(0, Math.abs(distance.getY()) - bounds.getHeight() * Config.SCROLL_BUFFER / 2 ));
		bounds = bounds.add(distance.multiply(Config.SCROLL_SPEED * elapsedTime));
	}
}

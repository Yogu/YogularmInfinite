package de.yogularm;

import javax.media.opengl.GL2;

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
	
	public void applyMatrix(GL2 gl) {
		gl.glLoadIdentity();
	  gl.glTranslatef(-bounds.getMinVector().getX(), -bounds.getMinVector().getY(), 0);
	}
	
	public void scroll(Vector target, float elapsedTime) {
		Vector distance = target.subtract(bounds.getCenter());
		// let player move the center quarter of the screen witho ut scrolling
		distance = distance.changeX(Math.signum(distance.getX()) * Math.max(0, Math.abs(distance.getX()) - bounds.getWidth()  / 8 ));
		distance = distance.changeY(Math.signum(distance.getY()) * Math.max(0, Math.abs(distance.getY()) - bounds.getHeight() / 8 ));
		bounds = bounds.add(distance.multiply(Config.SCROLL_SPEED * elapsedTime));
	}
}

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
		float xOffset = Math.min(bounds.getWidth() / 2 - Config.SCROLL_MIN_BUFFER, Config.SCROLL_OFFSET);
		float yOffset = Math.min(bounds.getHeight() / 2 - Config.SCROLL_MIN_BUFFER, Config.SCROLL_OFFSET);
		if (distance.getX() < 0)
			distance = distance.changeX(Math.min(distance.getX() + xOffset, 0));
		else
			distance = distance.changeX(Math.max(distance.getX() - xOffset, 0));
		if (distance.getY() < 0)
			distance = distance.changeY(Math.min(distance.getY() + yOffset, 0));
		else
			distance = distance.changeY(Math.max(distance.getY() - yOffset, 0));
		bounds = bounds.add(distance.multiply(Config.SCROLL_SPEED * elapsedTime));
	}
}

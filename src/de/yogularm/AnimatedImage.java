package de.yogularm;

import javax.media.opengl.GL2;

public class AnimatedImage implements Drawable {
	private Animation animation;
	private float currentTime;
	
	public AnimatedImage(Animation animation) {
		if (animation == null)
			throw new NullPointerException("animation is null");
		this.animation = animation;
	}
	
	public void update(float elapsedTime) {
		currentTime += elapsedTime;
		currentTime = currentTime % animation.getLength();
	}
	
	public void reset() {
		currentTime = 0;
	}
	
	public void draw(GL2 gl) {
		draw(gl, 1);
	}

	public void draw(GL2 gl, float opacity) {
		animation.getImage(currentTime).draw(gl, opacity);
	}
	
	public Animation getAnimation() {
		return animation;
	}
}

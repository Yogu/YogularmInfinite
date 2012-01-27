package de.yogularm.drawing;

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

	public void draw(RenderContext context) {
		animation.getImage(currentTime).draw(context);
	}
	
	public Animation getAnimation() {
		return animation;
	}
}

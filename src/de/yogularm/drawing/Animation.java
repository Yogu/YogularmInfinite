package de.yogularm.drawing;



public class Animation {
	private Image[] images;
	private float[] imageTimes;
	private float length;
	
	public Animation(Image[] images, float frameLength) {
		if (images == null)
			throw new NullPointerException("images is null");
		if (images.length == 0)
			throw new IllegalArgumentException("images must contain at least one element");
		this.images = images;
		
		imageTimes = new float[images.length];
		for (int i = 0; i < images.length; i++)
			imageTimes[i] = frameLength;
		length = frameLength * images.length;
	}
	
	public Animation(Image[] images, float[] imageTimes) {
		if (images == null)
			throw new NullPointerException("images is null");
		if (imageTimes == null)
			throw new NullPointerException("imageTimes is null");
		if (images.length == 0)
			throw new IllegalArgumentException("images must contain at least one element");
		if (images.length != imageTimes.length)
			throw new IllegalArgumentException("images and imageTimes should be arrays of the same length");
		this.images = images;
		this.imageTimes = imageTimes;
		for (int i = 0; i < imageTimes.length; i++) {
			length += imageTimes[i];
		}
		if (length <= 0)
			throw new IllegalArgumentException("Total length must be positive");
	}
	
	public Image getImage(float time) {
		time = time % length;
		for (int i = 0; i < imageTimes.length; i++) {
			time -= imageTimes[i];
			if (time <= 0)
				return images[i];
		}
		return images[images.length];
	}
	
	public float getLength() {
		return length;
	}
	
	public AnimatedImage getInstance() {
		return new AnimatedImage(this);
	}
}

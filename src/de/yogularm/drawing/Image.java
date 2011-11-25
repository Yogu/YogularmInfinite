package de.yogularm.drawing;

import de.yogularm.Rect;
import de.yogularm.Vector;

public class Image implements Cloneable, Drawable {
	private Texture texture;
	private Rect range;
	private Vector size;
	
	public Image(Texture texture, Rect range, Vector size) {
		if (texture == null)
			throw new IllegalArgumentException("texture is null");
		if (range == null)
			throw new IllegalArgumentException("range is null");
		if (size == null)
			throw new IllegalArgumentException("size is null");
		
		this.texture = texture;
		this.range = range;
		this.size = size;
	}
	
	public Image(Texture texture, Rect range) {
		this(texture, range, new Vector(1, 1));
	}
	
	public Image(Texture texture) {
		this(texture, new Rect(0, 0, 1, 1));
	}
	
	public void draw(RenderContext context) {
		context.bindTexture(texture);
		context.drawRect(new Rect(Vector.getZero(), size), range);
	}
	
	public void update(float elapsedTime) {
		
	}
}

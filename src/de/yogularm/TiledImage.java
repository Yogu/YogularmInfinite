package de.yogularm;

import com.jogamp.opengl.util.texture.Texture;

public class TiledImage {
	private Texture texture;
	private int countX;
	private int countY;
	private int space;
	private float spaceWidth;
	private float spaceHeight;
	private float tileWidth;
	private float tileHeight;
	
	public TiledImage(Texture texture, int countX, int countY, int space) {
		if (texture == null)
			throw new NullPointerException("texture is null");
		if (countX <= 0)
			throw new IllegalArgumentException("countX must be positive");
		if (countY <= 0)
			throw new IllegalArgumentException("countY must be positive");
		
		this.texture = texture;
		this.countX = countX;
		this.countY = countY;
		this.space = space;
		this.spaceWidth = space / (float)texture.getWidth();
		this.spaceHeight = space / (float)texture.getHeight();
		if (texture.getWidth() > 0)
			tileWidth = (1f - (countX - 1) * spaceWidth) / countX;
		if (texture.getHeight() > 0)
			tileHeight = (1f - (countY - 1) * spaceHeight) / countY;
	}
	
	public TiledImage(Texture texture, int countX, int countY) {
		this(texture, countX, countY, 4);
	}
	
	public TiledImage(Texture texture, int countXY) {
		this(texture, countXY, countXY);
	}
	
	public Image get(int x, int y) {
		if (x < 0 || x >= countX || y < 0| y >= countY)
			throw new IllegalArgumentException("Arguments out of range");
		
		return new Image(texture,
			new Rect(x * (tileWidth + spaceWidth), y * (tileHeight + spaceHeight),
							 x * (tileWidth + spaceWidth) + tileWidth,
							 y * (tileHeight + spaceHeight) + tileHeight));
	}
	
	public Image get(int index) {
		return get(index / countX, index % countX);
	}
	
	public Image[] getRange(int start, int count) {
		if (start < 0 || count < 0 || start + count >= countX * countY)
			throw new IllegalArgumentException("Arguments out of range");
		
		Image[] images = new Image[count];
		for (int i = start; i < count; i++) {
			images[i] = get(i);
		}
		return images;
	}
}

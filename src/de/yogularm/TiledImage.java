package de.yogularm;

import com.jogamp.opengl.util.texture.Texture;

public class TiledImage {
	private Texture texture;
	private int countX;
	private int countY;
	private int space;
	private float tileWidth;
	private float tileHeight;
	
	public TiledImage(Texture texture, int countX, int countY, int space) {
		if (texture == null)
			throw new NullPointerException("texture is null");
		
		this.texture = texture;
		this.countX = countX;
		this.countY = countY;
		this.space = space;
		if (texture.getWidth() > 0)
			tileWidth = (1f - (countX - 1) * space / (float)texture.getWidth()) / countX;
		if (texture.getHeight() > 0)
			tileHeight = (1f - (countY - 1) * space / (float)texture.getHeight()) / countY;
	}
	
	public TiledImage(Texture texture, int countX, int countY) {
		this(texture, countX, countY, 4);
	}
	
	public TiledImage(Texture texture, int countXY) {
		this(texture, countXY, countXY);
	}
	
	public Image get(int x, int y) {
		return new Image(texture, new Rect(x * tileWidth, y * tileHeight, (x + 1) * tileWidth, (y + 1) * tileHeight));
	}
}

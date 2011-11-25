package de.yogularm.drawing;

import de.yogularm.Rect;

public class TiledImage {
	private Texture texture;
	private int countX;
	private int countY;
	private float tileWidth;
	private float tileHeight;
	private float borderWidth;
	private float borderHeight;

	private static final float DEFAULT_BORDER = 1 / 32f;

	public TiledImage(Texture texture, int countX, int countY, float border) {
		if (texture == null)
			throw new NullPointerException("texture is null");
		if (countX <= 0)
			throw new IllegalArgumentException("countX must be positive");
		if (countY <= 0)
			throw new IllegalArgumentException("countY must be positive");

		this.texture = texture;
		this.countX = countX;
		this.countY = countY;
		tileWidth = 1f / countX;
		tileHeight = 1f / countY;
		borderWidth = border * tileWidth;
		borderHeight = border * tileHeight;
	}

	public TiledImage(Texture texture, int countX, int countY) {
		this(texture, countX, countY, DEFAULT_BORDER);
	}

	public Image get(int x, int y) {
		if (x < 0 || x >= countX || y < 0 | y >= countY)
			throw new IllegalArgumentException("Arguments out of range");

		return new Image(texture, new Rect(
			x * tileWidth + borderWidth,
			y * tileHeight + borderHeight,
			(x + 1) * tileWidth - borderWidth,
			(y + 1) * tileHeight - borderHeight));
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

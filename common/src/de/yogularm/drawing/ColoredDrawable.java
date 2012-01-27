package de.yogularm.drawing;

public class ColoredDrawable implements Drawable {
	private Drawable drawable;
	private Color color = Color.white;
	
	public ColoredDrawable(Drawable drawable) {
		if (drawable == null)
			throw new NullPointerException("drawable is null");
		this.drawable = drawable;
	}
	
	public ColoredDrawable(Drawable drawable, Color color) {
		this(drawable);
		if (color == null)
			throw new NullPointerException("color is null");
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		if (color == null)
			throw new NullPointerException("color is null");
		this.color = color;
	}
	
	public Drawable getDrawable() {
		return drawable;
	}
	
	public void draw(RenderContext context) {
		context.setColor(color);
		drawable.draw(context);
	}

	public void update(float elapsedTime) {
		
	}
}

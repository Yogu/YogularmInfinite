package de.yogularm.drawing;

import de.yogularm.geometry.Vector;

public class RenderTransformation implements Drawable {
	private Drawable drawable;
	private Vector offset = Vector.getZero();
	private Vector scale = new Vector(1, 1);
	private float angle;
	private boolean isVerticallyMirrored;
	private Vector rotationCenter = Vector.getZero();
	
	public RenderTransformation(Drawable drawable) {
		if (drawable == null)
			throw new NullPointerException("drawable is null");
		this.drawable = drawable;
	}
	
	public RenderTransformation(Drawable drawable, Vector offset, Vector scale, float angle) {
		this(drawable);
		
		if (offset == null)
			throw new NullPointerException("offset is null");
		this.offset = offset;

		if (scale == null)
			throw new NullPointerException("scale is null");
		this.scale = scale;
		
		this.angle = angle;
	}

	public RenderTransformation(Drawable drawable, Vector offset, Vector scale, float angle, Vector rotationCenter) {
		this(drawable, offset, scale, angle);

		if (rotationCenter == null)
			throw new NullPointerException("rotationCenter is null");
		this.rotationCenter = rotationCenter;
	}

	public RenderTransformation(Drawable drawable, Vector offset, float angle, Vector rotationCenter) {
		this(drawable, offset, new Vector(1, 1), angle, rotationCenter);
	}

	public RenderTransformation(Drawable drawable, Vector offset, float angle) {
		this(drawable, offset, new Vector(1, 1), angle);
	}
	
	public Drawable getDrawable() {
		return drawable;
	}
	
	public void setDrawable(Drawable drawable) {
		if (drawable == null)
			throw new NullPointerException("drawable is null");
		this.drawable = drawable;
	}
	
	public Vector getOffset() {
		return offset;
	}
	
	public void setOffset(Vector value) {
		if (value == null)
			throw new NullPointerException("value is null");
		offset = value;
	}
	
	public Vector getRotationCenter() {
		return rotationCenter;
	}
	
	public void setRotationCenter(Vector value) {
		if (value == null)
			throw new NullPointerException("value is null");
		rotationCenter = value;
	}
	
	public float getAngle() {
		return angle;
	}
	
	public void setAngle(float value) {
		angle = value;
	}
	
	public Vector getScale() {
		return scale;
	}
	
	public void setScale(Vector value) {
		if (value == null)
			throw new NullPointerException("value is null");
		scale = value;
	}
	
	public void setIsVerticallyMirrored(boolean value) {
		isVerticallyMirrored = value;
	}
	
	public boolean isVerticallyMirrored() {
		return isVerticallyMirrored;
	}

	public void draw(RenderContext context) {
		context.beginTransformation();
			context.translate(offset.add(new Vector(isVerticallyMirrored ? 1 : 0, 0)));
			context.translate(rotationCenter);
			context.rotate(angle);
			context.scale(scale.multiply(new Vector(isVerticallyMirrored ? -1 : 1, 1)));
			context.translate(rotationCenter.negate());
			drawable.draw(context);
		context.endTransformation();
	}
	
	public static void draw(RenderContext context, Drawable drawable, float x, float y, float width, float height) {
		RenderTransformation transformation = new RenderTransformation(drawable);
		transformation.setOffset(new Vector(x, y));
		transformation.setScale(new Vector(width, height));
		transformation.draw(context);
	}
	
	public void update(float elapsedTime) {
		drawable.update(elapsedTime);
	}
}

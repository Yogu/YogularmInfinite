package de.yogularm.drawing;

import javax.media.opengl.GL2;

import de.yogularm.OpenGLHelper;
import de.yogularm.Vector;

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

	public void draw(GL2 gl) {
		gl.glPushMatrix();
		gl.glTranslatef(offset.getX() + (isVerticallyMirrored ? 1 : 0), offset.getY(), 0);
		gl.glTranslatef(rotationCenter.getX(), rotationCenter.getY(), 0);
		gl.glRotatef(angle, 0, 0, 1);
		gl.glScalef(scale.getX() * (isVerticallyMirrored ? -1 : 1), scale.getY(), 1);
		gl.glTranslatef(-rotationCenter.getX(), -rotationCenter.getY(), 0);
		
		drawable.draw(gl);

		gl.glPopMatrix();
		OpenGLHelper.checkErrors(gl);
	}
	
	public static void draw(GL2 gl, Drawable drawable, float x, float y, float width, float height) {
		RenderTransformation transformation = new RenderTransformation(drawable);
		transformation.setOffset(new Vector(x, y));
		transformation.setScale(new Vector(width, height));
		transformation.draw(gl);
	}
	
	public void update(float elapsedTime) {
		drawable.update(elapsedTime);
	}
}

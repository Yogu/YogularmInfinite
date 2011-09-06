package de.yogularm;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.texture.Texture;

public class Image {
	private Texture texture;
	private Rect range;
	private Vector size;
	private boolean isMirrored;
	private float angle;
	private float opacity = 1;
	
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
	
	public boolean isMirrored() {
		return isMirrored;
	}
	
	public void setIsMirrored(boolean value) {
		isMirrored = value;
	}
	
	public float getAngle() {
		return angle;
	}
	
	public void setAngle(float value) {
		angle = value;
	}
	
	public Vector getSize() {
		return size;
	}
	
	public void setSize(Vector value) {
		if (value == null)
			throw new NullPointerException("value is null");
		size = value;
	}
	
	public float getOpacity() {
		return opacity;
	}
	
	public void setOpactiy(float value) {
		opacity = value;
	}
	
	public void draw(GL2 gl) {
		draw(gl, Vector.getZero());
	}
	
	public void draw(GL2 gl, Vector position) {
		gl.glPushMatrix();
		gl.glTranslatef(position.getX(), position.getY(), 0);
		gl.glRotatef(angle, 0, 0, 1);

		float max = isMirrored ? range.getMinVector().getX() : range.getMaxVector().getX();
		float min = isMirrored ? range.getMaxVector().getX() : range.getMinVector().getX();
		
		texture.bind();
		gl.glColor4f(1, 1, 1, opacity);
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(min, range.getMaxVector().getY());
		gl.glVertex3f(0, 0, 0);
		gl.glTexCoord2f(max, range.getMaxVector().getY());
		gl.glVertex3f(size.getX(), 0, 0);
		gl.glTexCoord2f(max, range.getMinVector().getY());
		gl.glVertex3f(size.getX(), size.getY(), 0);
		gl.glTexCoord2f(min, range.getMinVector().getY());
		gl.glVertex3f(0, size.getY(), 0);
		gl.glEnd();

		gl.glPopMatrix();
		OpenGLHelper.checkErrors(gl);
	}
}

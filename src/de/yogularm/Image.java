package de.yogularm;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.texture.Texture;

import de.yogularm.drawing.Drawable;

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
	
	public void draw(GL2 gl) {
		texture.bind();
		drawQuad(gl);
	}
	
	protected void drawQuad(GL2 gl) {
		float max = range.getMaxVector().getX();
		float min = range.getMinVector().getX();
		
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
	}
	
	public void update(float elapsedTime) {
		
	}
}

package de.yogularm;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

public class SimpleArrow implements Drawable {
	private static final float HEAD_SIZE = 0.2f;
	
	public float getLength(float length) {
		return length;
	}
	
	public void draw(GL2 gl) {
		draw(gl, 1);
	}

	public void draw(GL2 gl, float opacity) {
		gl.glColor4f(1, 0, 0, 1);
		gl.glLineWidth(2);
		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex2f(0, 0);
		gl.glVertex2f(1, 0);
		gl.glVertex2f(1 - HEAD_SIZE, -HEAD_SIZE);
		gl.glVertex2f(1, 0);
		gl.glVertex2f(1 - HEAD_SIZE, HEAD_SIZE);
		gl.glEnd();
		OpenGLHelper.checkErrors(gl);
	}

	public void update(float elapsedTime) { }
	
	public static void render(GL2 gl, Vector offset, float length, float angle) {
		SimpleArrow arrow = new SimpleArrow();
		RenderTransformation transformation = new RenderTransformation(arrow);
		transformation.setOffset(offset);
		transformation.setAngle(angle);
		transformation.setScale(new Vector(length, 1));
		transformation.draw(gl);
	}
}

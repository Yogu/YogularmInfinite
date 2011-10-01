package de.yogularm.drawing;

import javax.media.opengl.GL2;

import de.yogularm.OpenGLHelper;

public class Renderer {
	public static void render(GL2 gl, Renderable renderable) {
		// Initialize color for the case that no ColoredDrawable is used
		gl.glColor4f(1, 1, 1, 1);
		
		gl.glPushMatrix();
			gl.glTranslatef(renderable.getPosition().getX(), renderable.getPosition().getY(), 0);
			renderable.getDrawable().draw(gl);
		gl.glPopMatrix();
		OpenGLHelper.checkErrors(gl);
	}
}

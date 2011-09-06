package de.yogularm;

import javax.media.opengl.GL2;

public class Renderer {
	public static void render(GL2 gl, Renderable renderable) {
		gl.glPushMatrix();
			gl.glTranslatef(renderable.getPosition().getX(), renderable.getPosition().getY(), 0);
			renderable.draw(gl);
		gl.glPopMatrix();
		OpenGLHelper.checkErrors(gl);
	}
}

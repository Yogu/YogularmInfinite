package de.yogularm;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.GLU;

public class OpenGLHelper {
	private OpenGLHelper() {
		
	}
	
	public static void renderRect(GL2 gl) {
		renderRect(gl, 1, 1);
	}
	
	public static void renderRect(GL2 gl, float w, float h) {
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(w, 0, 0);
		gl.glVertex3f(w, h, 0);
		gl.glVertex3f(0, h, 0);
		gl.glEnd();
	}
	
	public static void checkErrors(GL2 gl) {
		int error = gl.glGetError();
		if (error != GL.GL_NO_ERROR) {
			throw new GLException(new GLU().gluErrorString(error));
		}
	}
}

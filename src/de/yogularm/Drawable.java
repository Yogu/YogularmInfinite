package de.yogularm;

import javax.media.opengl.GL2;

public interface Drawable {
	void draw(GL2 gl);
	void draw(GL2 gl, float opacity);
	void update(float elapsedTime);
}

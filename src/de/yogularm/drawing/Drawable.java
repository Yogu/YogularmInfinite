package de.yogularm.drawing;

import javax.media.opengl.GL2;

public interface Drawable {
	void draw(GL2 gl);
	void update(float elapsedTime);
}

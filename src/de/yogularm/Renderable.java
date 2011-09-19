package de.yogularm;

import javax.media.opengl.GL2;

public interface Renderable extends Locatable {
	void draw(GL2 gl);
}

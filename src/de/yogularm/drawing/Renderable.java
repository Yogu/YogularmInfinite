package de.yogularm.drawing;

import javax.media.opengl.GL2;

import de.yogularm.Locatable;

public interface Renderable extends Locatable {
	Drawable getDrawable();
}

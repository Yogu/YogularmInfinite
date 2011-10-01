package de.yogularm.drawing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.media.opengl.GL2;

public class CombinedDrawable implements Drawable {
	private List<Drawable> drawables = new ArrayList<Drawable>();
	
	public void add(Drawable drawable) {
		if (drawable != null)
			drawables.add(drawable);
	}
	
	public void addAll(Collection<Drawable> collection) {
		drawables.addAll(collection);
	}
	
	public void draw(GL2 gl) {
		for (Drawable drawable : drawables)
			drawable.draw(gl);
	}

	public void update(float elapsedTime) {
		
	}
}

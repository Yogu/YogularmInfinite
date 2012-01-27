package de.yogularm.drawing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CombinedDrawable implements Drawable {
	private List<Drawable> drawables = new ArrayList<Drawable>();
	
	public CombinedDrawable() {
		
	}
	
	public CombinedDrawable(List<Drawable> drawables) {
		if (drawables == null)
			throw new NullPointerException("drawables is null");
		this.drawables = drawables;
	}
	
	public void add(Drawable drawable) {
		if (drawable != null)
			drawables.add(drawable);
	}
	
	public void addAll(Collection<Drawable> collection) {
		drawables.addAll(collection);
	}
	
	public void draw(RenderContext context) {
		for (Drawable drawable : drawables)
			drawable.draw(context);
	}

	public void update(float elapsedTime) {
		
	}
}

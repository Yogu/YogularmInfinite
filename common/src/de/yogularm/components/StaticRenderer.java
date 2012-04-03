package de.yogularm.components;

import de.yogularm.drawing.Drawable;

public class StaticRenderer extends ComponentRenderer {
	private Drawable drawable;
	
	public StaticRenderer(Component component, Drawable drawable) {
		super(component);
		this.drawable = drawable;
	}
	
	public Drawable getDrawable() {
		return drawable;
	}
}

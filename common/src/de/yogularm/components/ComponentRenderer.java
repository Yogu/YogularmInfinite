package de.yogularm.components;

import de.yogularm.drawing.AnimatedImage;
import de.yogularm.drawing.Animation;
import de.yogularm.drawing.Drawable;

public abstract class ComponentRenderer {
	private Component component;
	private Drawable drawable;
	
	public ComponentRenderer(Component component) {
		this.component = component;
	}
	
	public void update(float elapsedTime) {
		if (drawable != null)
			drawable.update(elapsedTime);
	}
	
	public Drawable getDrawable() {
		return drawable;
	}

	protected void setAnimation(Animation animation) {
		if ((this.drawable instanceof AnimatedImage)) {
			AnimatedImage animatedImage = (AnimatedImage) drawable;
			if (animatedImage.getAnimation() == animation)
				return;
		}
		drawable = animation.getInstance();
	}
	
	protected void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}
	
	protected Component getComponent() {
		return component;
	}
}

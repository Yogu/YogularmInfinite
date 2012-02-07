package de.yogularm.components;

import de.yogularm.drawing.AnimatedImage;
import de.yogularm.drawing.Animation;
import de.yogularm.drawing.Drawable;
import de.yogularm.event.Event;
import de.yogularm.geometry.Vector;

public class Component implements Locatable {
	private Vector position;
	private ComponentCollection collection;
	private boolean isRemoved = false;
	private Drawable drawable;

	/**
	 * An event that is called when this component is moved
	 * 
	 * The event parameter specifies the former position.
	 */
	public final Event<Vector> onMoved = new Event<Vector>(this);

	public Component(ComponentCollection collection) {
		if (collection == null)
			throw new NullPointerException("collection is null");
		this.collection = collection;
		position = Vector.getZero();
	}

	public ComponentCollection getCollection() {
		return collection;
	}

	public Vector getPosition() {
		return position;
	}

	public void setPosition(Vector position) {
		if (position == null)
			throw new IllegalArgumentException("position is null");

		Vector oldPosition = this.position;
		this.position = position;
		if (!position.equals(oldPosition))
			onMoved.call(oldPosition);
	}

	public void update(float elapsedTime) {
		if (drawable != null)
			drawable.update(elapsedTime);
	}

	public void remove() {
		isRemoved = true;
	}

	public boolean isToRemove() {
		return isRemoved;
	}

	protected void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}

	protected void setAnimation(Animation animation) {
		if ((this.drawable instanceof AnimatedImage)) {
			AnimatedImage animatedImage = (AnimatedImage) drawable;
			if (animatedImage.getAnimation() == animation)
				return;
		}
		drawable = animation.getInstance();
	}

	public Drawable getDrawable() {
		return drawable;
	}
}

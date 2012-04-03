package de.yogularm.components;

import de.yogularm.drawing.Drawable;
import de.yogularm.drawing.Renderable;
import de.yogularm.event.Event;
import de.yogularm.geometry.Vector;

public abstract class FormalComponent implements Renderable {
	private Vector position;
	private transient ComponentCollection collection;
	private transient boolean isRemoved = false;
	private ComponentLogic logic;
	private ComponentRenderer renderer;

	/**
	 * An event that is called when this component is moved
	 * 
	 * The event parameter specifies the former position.
	 */
	public final Event<Vector> onMoved = new Event<Vector>(this);

	public FormalComponent(ComponentCollection collection) {
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
		if (renderer != null)
			renderer.update(elapsedTime);
		if (logic != null)
			logic.update(elapsedTime);
	}

	public void remove() {
		isRemoved = true;
	}

	public boolean isToRemove() {
		return isRemoved;
	}
	
	@Override
	public Drawable getDrawable() {
		return renderer.getDrawable();
	}
	
	public ComponentLogic getLogic() {
		return logic;
	}
	
	public void setLogic(ComponentLogic logic) {
		this.logic = logic;
	}
	
	public ComponentRenderer getRenderer() {
		return renderer;
	}
	
	public void setRenderer(ComponentRenderer renderer) {
		this.renderer = renderer;
	}
}

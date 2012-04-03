package de.yogularm.components;

import de.yogularm.drawing.Renderable;

public abstract class Entity extends Component implements Renderable {
	public Entity(ComponentCollection collection) {
		super(collection);
	}
}

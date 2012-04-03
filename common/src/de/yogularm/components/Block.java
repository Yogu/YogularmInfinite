package de.yogularm.components;

import de.yogularm.drawing.Renderable;

public abstract class Block extends Body implements Renderable {
	private static final long serialVersionUID = 4754651165967956486L;

	public Block(ComponentCollection collection) {
		super(collection);
	}
}

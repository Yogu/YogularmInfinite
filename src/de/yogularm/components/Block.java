package de.yogularm.components;

import de.yogularm.drawing.Renderable;

public abstract class Block extends Body implements Renderable {
	public Block(ComponentCollection collection) {
		super(collection);
	}
}

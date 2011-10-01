package de.yogularm;

import de.yogularm.drawing.Renderable;

public abstract class Block extends Body implements Renderable {
	public Block(World world) {
		super(world);
	}
}

package de.yogularm;

public abstract class Entity extends Body implements Renderable {
	public Entity(World world) {
		super(world);
	}
}

package de.yogularm;

import javax.media.opengl.GL2;

public class Component implements Locatable {
	private Vector position;
	private World world;
	private boolean isRemoved = false;
	private Drawable drawable;
	
	public Component(World world) {
		if (world == null)
			throw new NullPointerException("world is null");
		this.world = world;
		position = Vector.getZero();
	}
	
	public World getWorld() {
		return world;
	}
	
	public Vector getPosition() {
		return position;
	}
	
	public void setPosition(Vector position) {
		if (position == null)
			throw new IllegalArgumentException("position is null");
		
		this.position = position;
	}
	
	public void update(float elapsedTime) {
		
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
	
	protected Drawable getDrawable() {
		return drawable;
	}
	
	public void draw(GL2 gl) {
		if (drawable != null)
			drawable.draw(gl);
	}
}

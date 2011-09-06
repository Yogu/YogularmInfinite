package de.yogularm;

import de.yogularm.components.Arrow;

public abstract class Item extends Body implements Renderable {
	public Item(World world) {
		super(world);
		setIsSolid(false);
	}

	protected void onCollision(Body other, Direction direction, boolean isCauser) {
		super.onCollision(other, direction, isCauser);
		
		if ((other instanceof Player) || (other instanceof Arrow)) {
			remove();
		}
	}
}

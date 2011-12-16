package de.yogularm;

import de.yogularm.components.Arrow;
import de.yogularm.drawing.Renderable;

public abstract class Item extends Body implements Renderable {
	public Item(ComponentCollection collection) {
		super(collection);
		setIsSolid(false);
	}

	protected void onCollision(Body other, Direction direction, boolean isCauser) {
		super.onCollision(other, direction, isCauser);
		
		if ((other instanceof Player) || (other instanceof Arrow)) {
			remove();
		}
	}
}

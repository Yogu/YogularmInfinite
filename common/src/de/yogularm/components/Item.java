package de.yogularm.components;

import de.yogularm.components.general.Arrow;
import de.yogularm.drawing.Renderable;
import de.yogularm.geometry.Direction;

public abstract class Item extends Component implements Renderable {
	public Item(ComponentCollection collection) {
		super(collection);
		setIsSolid(false);
	}

	protected void onCollision(Component other, Direction direction, boolean isCauser) {
		super.onCollision(other, direction, isCauser);
		
		if ((other instanceof Player) || (other instanceof Arrow)) {
			remove();
		}
	}
}

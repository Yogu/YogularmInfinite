package de.yogularm.components.general;

import de.yogularm.Res;
import de.yogularm.components.ComponentCollection;
import de.yogularm.components.Item;

public class Coin extends Item {
	public Coin(ComponentCollection collection) {
		super(collection);
		setDrawable(Res.images.coin);
	}
}

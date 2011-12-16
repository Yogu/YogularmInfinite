package de.yogularm.components;

import de.yogularm.ComponentCollection;
import de.yogularm.Item;
import de.yogularm.Res;

public class Coin extends Item {
	public Coin(ComponentCollection collection) {
		super(collection);
		setDrawable(Res.images.coin);
	}
}

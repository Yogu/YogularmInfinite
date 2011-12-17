package de.yogularm.components;

import de.yogularm.ComponentCollection;
import de.yogularm.Item;
import de.yogularm.Res;

public class Heart extends Item {
	public Heart(ComponentCollection collection) {
		super(collection);
		setDrawable(Res.images.heart);
	}
}

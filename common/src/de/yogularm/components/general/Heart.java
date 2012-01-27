package de.yogularm.components.general;

import de.yogularm.Res;
import de.yogularm.components.ComponentCollection;
import de.yogularm.components.Item;

public class Heart extends Item {
	public Heart(ComponentCollection collection) {
		super(collection);
		setDrawable(Res.images.heart);
	}
}

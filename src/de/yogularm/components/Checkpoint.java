package de.yogularm.components;

import de.yogularm.ComponentCollection;
import de.yogularm.Item;
import de.yogularm.Res;

public class Checkpoint extends Item {
	public Checkpoint(ComponentCollection collection) {
		super(collection);
		setDrawable(Res.images.checkpoint);
	}
}

package de.yogularm.components.general;

import de.yogularm.Res;
import de.yogularm.components.ComponentCollection;
import de.yogularm.components.Item;

public class Checkpoint extends Item {
	public Checkpoint(ComponentCollection collection) {
		super(collection);
		setDrawable(Res.images.checkpoint);
	}
}

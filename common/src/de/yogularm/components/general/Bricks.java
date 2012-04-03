package de.yogularm.components.general;

import de.yogularm.Res;
import de.yogularm.components.Block;
import de.yogularm.components.ComponentCollection;

public class Bricks extends Block {
	public Bricks(ComponentCollection collection) {
		super(collection);
		setDrawable(Res.images.bricks);
	}
}

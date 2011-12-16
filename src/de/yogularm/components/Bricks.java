package de.yogularm.components;

import de.yogularm.Block;
import de.yogularm.ComponentCollection;
import de.yogularm.Res;

public class Bricks extends Block {
	public Bricks(ComponentCollection collection) {
		super(collection);
		setDrawable(Res.images.bricks);
	}
}

package de.yogularm.components;

import de.yogularm.Block;
import de.yogularm.ComponentCollection;
import de.yogularm.Res;

public class Stone extends Block {
	public Stone(ComponentCollection collection) {
		super(collection);
		setDrawable(Res.images.stone);
	}
}

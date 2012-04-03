package de.yogularm.components.general;

import de.yogularm.Res;
import de.yogularm.components.Block;
import de.yogularm.components.ComponentCollection;

public class Stone extends Block {
	public Stone(ComponentCollection collection) {
		super(collection);
		setDrawable(Res.images.stone);
	}
}

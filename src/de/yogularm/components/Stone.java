package de.yogularm.components;

import de.yogularm.Block;
import de.yogularm.Image;
import de.yogularm.Rect;
import de.yogularm.Res;
import de.yogularm.World;

public class Stone extends Block {
	public Stone(World world) {
		super(world);
		setDrawable(Res.images.stone);
	}
}

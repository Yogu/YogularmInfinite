package de.yogularm.components;

import de.yogularm.Block;
import de.yogularm.Image;
import de.yogularm.Rect;
import de.yogularm.Res;
import de.yogularm.World;

public class Bricks extends Block {
	public Bricks(World world) {
		super(world);
		setDrawable(Res.images.bricks);
	}
}

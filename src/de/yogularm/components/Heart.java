package de.yogularm.components;

import de.yogularm.Item;
import de.yogularm.Res;
import de.yogularm.World;

public class Heart extends Item {
	public Heart(World world) {
		super(world);
		setDrawable(Res.images.heart);
	}
}

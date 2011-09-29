package de.yogularm.components;

import de.yogularm.Item;
import de.yogularm.Res;
import de.yogularm.World;

public class Checkpoint extends Item {
	public Checkpoint(World world) {
		super(world);
		setDrawable(Res.images.checkpoint);
	}
}

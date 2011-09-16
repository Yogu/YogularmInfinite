package de.yogularm.components;

import de.yogularm.Image;
import de.yogularm.Item;
import de.yogularm.Rect;
import de.yogularm.Res;
import de.yogularm.World;

public class Coin extends Item {
	public Coin(World world) {
		super(world);
		setImage(Res.images.coin);
	}
}

package de.yogularm.components;

import de.yogularm.Image;
import de.yogularm.Item;
import de.yogularm.Rect;
import de.yogularm.Res;
import de.yogularm.World;

public class Coin extends Item {
	public Coin(World world) {
		super(world);
		setImage(new Image(Res.textures.blocks, new Rect(0.25f, 0, 0.5f, 0.25f)));
	}
}

package de.yogularm.components;

import de.yogularm.Image;
import de.yogularm.Item;
import de.yogularm.Rect;
import de.yogularm.Res;
import de.yogularm.World;

public class Heart extends Item {
	public Heart(World world) {
		super(world);
		setImage(new Image(Res.textures.blocks, new Rect(0.75f, 0, 1, 0.25f)));
	}
}

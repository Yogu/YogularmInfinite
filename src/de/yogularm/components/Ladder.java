package de.yogularm.components;

import de.yogularm.Block;
import de.yogularm.Config;
import de.yogularm.Rect;
import de.yogularm.Res;
import de.yogularm.Vector;
import de.yogularm.World;

public class Ladder extends Block {	
	public Ladder(World world) {
		super(world);
		setDrawable(Res.images.ladder);
		setBounds(new Rect(0, 0, 1, 1));
		setIsClimbable(true);
		setIsSolid(false);
	}
}

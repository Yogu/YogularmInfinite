package de.yogularm.components;

import de.yogularm.Block;
import de.yogularm.ComponentCollection;
import de.yogularm.Rect;
import de.yogularm.Res;

public class Ladder extends Block {	
	public Ladder(ComponentCollection collection) {
		super(collection);
		setDrawable(Res.images.ladder);
		setBounds(new Rect(0.16125f, 0, 0.90125f, 1));
		setIsClimbable(true);
		setIsSolid(false);
	}
}

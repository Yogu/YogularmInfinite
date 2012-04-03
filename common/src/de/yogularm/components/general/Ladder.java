package de.yogularm.components.general;

import de.yogularm.Res;
import de.yogularm.components.Component;
import de.yogularm.components.ComponentCollection;
import de.yogularm.geometry.Rect;

public class Ladder extends Component {	
	public Ladder(ComponentCollection collection) {
		super(collection);
		setDrawable(Res.images.ladder);
		setBounds(new Rect(0.16125f, 0, 0.90125f, 1));
		setIsClimbable(true);
		setIsSolid(false);
	}
}

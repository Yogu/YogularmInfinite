package de.yogularm.components.general;

import de.yogularm.Res;
import de.yogularm.components.Component;
import de.yogularm.components.ComponentCollection;

public class Bricks extends Component {
	public Bricks(ComponentCollection collection) {
		super(collection);
		setDrawable(Res.images.bricks);
	}
}

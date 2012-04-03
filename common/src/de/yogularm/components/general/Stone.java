package de.yogularm.components.general;

import de.yogularm.Res;
import de.yogularm.components.Component;
import de.yogularm.components.ComponentCollection;
import de.yogularm.components.StaticRenderer;

public class Stone extends Component {
	public Stone(ComponentCollection collection) {
		super(collection);
		setRenderer(new StaticRenderer(this, Res.images.stone));
	}
}

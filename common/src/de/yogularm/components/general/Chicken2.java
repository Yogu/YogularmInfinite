package de.yogularm.components.general;

import de.yogularm.components.Component2;
import de.yogularm.components.ComponentLogic;

public class Chicken2 extends Component2 {
	public Chicken2() {
		setLogic(new ChickenLogic());
		setRenderer(new ChickenRenderer());
	}
	
	public static class ChickenLogic implements ComponentLogic {
		
	}
	
	public static class ChickenRenderer implements ChickenRenderer
}

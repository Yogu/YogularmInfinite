package de.yogularm.building;

import de.yogularm.BuilderBase;
import de.yogularm.components.Chicken;
import de.yogularm.components.Stone;

public class Farm extends BuilderBase {	
	public void doBuild() {
		int length = 10;
		for (int i = 0; i < length; i++)
			place(Stone.class, i + 1, 0);
		place(Chicken.class, length / 2 + 1, 1);
		moveBuildingPosition(length, 0);
	}
}

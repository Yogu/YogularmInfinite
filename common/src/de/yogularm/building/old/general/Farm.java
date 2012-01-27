package de.yogularm.building.old.general;

import de.yogularm.building.old.BuilderBase;
import de.yogularm.components.general.Chicken;
import de.yogularm.components.general.Stone;

public class Farm extends BuilderBase {	
	public void build() {
		int length = 10;
		for (int i = 0; i < length; i++)
			place(Stone.class, i + 1, 0);
		place(Chicken.class, length / 2 + 1, 1);
		moveBuildingPosition(length, 0);
	}
}

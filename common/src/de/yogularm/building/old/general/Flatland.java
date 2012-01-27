package de.yogularm.building.old.general;

import java.util.Random;

import de.yogularm.building.old.BuilderBase;
import de.yogularm.components.general.Stone;

public class Flatland extends BuilderBase {
	private static final int MIN_LENGTH = 2;
	private static final int MAX_LENGTH = 5;
	
	public void build() {
		Random random = new Random();
		int length = MIN_LENGTH + random.nextInt(MAX_LENGTH - MIN_LENGTH + 1);
		for (int i = 1; i <= length; i++)
			place(Stone.class, i, 0);
		moveBuildingPosition(length, 0);
	}
}

package de.yogularm.building;

import java.util.Random;

import de.yogularm.BuilderBase;
import de.yogularm.components.Stone;

public class Stairs extends BuilderBase {
	private static final int MIN_LENGTH = 1;
	private static final int MAX_LENGTH = 4;
	
	public void build() {
		Random random = new Random();
		int length = MIN_LENGTH + random.nextInt(MAX_LENGTH - MIN_LENGTH);
		int direction = 1 - 2 * random.nextInt(2);
		for (int i = 1; i <= length; i++)
			place(Stone.class, i, i * direction);
		moveBuildingPosition(length, length * direction);
	}
}

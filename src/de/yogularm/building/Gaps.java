package de.yogularm.building;

import java.util.Random;

import de.yogularm.components.general.Stone;

public class Gaps extends BuilderBase {
	private static final int MIN_LENGTH = 1;
	private static final int MAX_LENGTH = 3;
	private static final int MAX_HEIGHT = 1;
	
	public void build() {
		Random random = new Random();
		int length = MIN_LENGTH + random.nextInt(MAX_LENGTH - MIN_LENGTH);
		int height = random.nextInt(MAX_HEIGHT + 1);
		place(Stone.class, length + 1, height);
		moveBuildingPosition(length + 1, height);
	}
}

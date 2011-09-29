package de.yogularm.building;

import java.util.Random;

import de.yogularm.BuilderBase;
import de.yogularm.components.Checkpoint;
import de.yogularm.components.Chicken;
import de.yogularm.components.Shooter;
import de.yogularm.components.Stone;

public class Sky2 extends BuilderBase {
	private static final int[] MAX_X_OFFSET_BY_Y_OFFSET = {
		3 /* up */, 4 /* flat */,
		5 /* down 1 */, 5 /* down 2 */, 5 /* down 3*/
	};
	private static final int MIN_PLATFORM_LENGTH = 1;
	private static final int MAX_PLATFORM_LENGTH = 4;
	
	public void doBuild() {
		Random random = getRandom(0x390401E0);
		int maxYOffset = MAX_X_OFFSET_BY_Y_OFFSET.length - 2;
		int yOffset = Math.min(random.nextInt(maxYOffset * 2 + 1) - maxYOffset, 1);
		int xOffset = random.nextInt(MAX_X_OFFSET_BY_Y_OFFSET[1 - yOffset]) + 1;
		int length = random.nextInt(MAX_PLATFORM_LENGTH - MIN_PLATFORM_LENGTH + 1) + MIN_PLATFORM_LENGTH;
		
		for (int i = 1; i <= length; i++) {
			place(Stone.class, xOffset + i, yOffset);
		}
		moveBuildingPosition(xOffset + length, yOffset);
		
		if (length == 3 && random.nextInt(3) == 0)
			place(Shooter.class, -1, 1);
		
		if (length == 4 && random.nextInt(3) == 0)
			place(Chicken.class, -1, 1);
		
		if (getCurrentIndex() % 20 == 9)
			place(Checkpoint.class, -1, 1);
	}
}

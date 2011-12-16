package de.yogularm.building;

import java.util.Random;

import de.yogularm.BuilderBase;
import de.yogularm.Config;
import de.yogularm.components.Checkpoint;
import de.yogularm.components.Coin;
import de.yogularm.components.Stone;

public class GroundBuilder extends BuilderBase {
	private static final int MAX_LENGTH = 8;
	private static final int MIN_LENGTH = 3;
	
	public void doBuild() {
		Random random = getRandom(0x1C3D10F2);
		int dir = random.nextInt(3) - 1; // -1, 0, 1
		int length = Math.max(0, random.nextInt(MAX_LENGTH - MIN_LENGTH + 1) + MIN_LENGTH);
		for (int i = 0; i < length; i++) {
			place(Stone.class, i + 1, dir);
		}
		moveBuildingPosition(length, dir);

		if (isCheckpoint(0))
			place(Checkpoint.class, 0, 1);
		else
			place(Coin.class, 0, 1);
	}
	
	private boolean isCheckpoint(int indexOffset) {
		int index = getCurrentIndex() + indexOffset;
		return index % Config.CHECKPOINT_RANGE == Config.CHECKPOINT_RANGE - 1; 
	}
}

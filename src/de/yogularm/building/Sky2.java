package de.yogularm.building;

import java.util.Random;

import de.yogularm.BuilderBase;
import de.yogularm.Vector;
import de.yogularm.components.Chicken;
import de.yogularm.components.Coin;
import de.yogularm.components.Platform;
import de.yogularm.components.Shooter;
import de.yogularm.components.Stone;

public class Sky2 extends BuilderBase {
	private static final int[] MAX_X_OFFSET_BY_Y_OFFSET = {
		3 /* up */, 4 /* flat */,
		5 /* down 1 */, 5 /* down 2 */, 5 /* down 3*/
	};
	private static final int MIN_BRIDGE_LENGTH = 1;
	private static final int MAX_BRIDGE_LENGTH = 4;
	private static final int MIN_PLATFORM_LENGTH = 4;
	private static final int MAX_PLATFORM_LENGTH = 8;
	private static final float MIN_PLATFORM_SPEED = 2;
	private static final float MAX_PLATFORM_SPEED = 5;
	
	public void doBuild() {
		buildGap();
		if (isPlatform(0))
			buildPlatform();
		else
			buildBridge();
		
		place(Coin.class, 0, 1);
	}
	
	private void buildGap() {
		Random random = getRandom(0x1180E669);
		int maxYOffset = MAX_X_OFFSET_BY_Y_OFFSET.length - 2;
		int yOffset = Math.min(random.nextInt(maxYOffset * 2 + 1) - maxYOffset, 1);
		int xOffset = random.nextInt(MAX_X_OFFSET_BY_Y_OFFSET[1 - yOffset]) + 1;
		moveBuildingPosition(xOffset, yOffset);
	}
	
	private void buildBridge() {
		Random random = getRandom(0x115D567E);
		int LENGTH = random.nextInt(MAX_BRIDGE_LENGTH - MIN_BRIDGE_LENGTH + 1) + MIN_BRIDGE_LENGTH;
		
		for (int i = 1; i <= LENGTH; i++) {
			place(Stone.class, i, 0);
		}
		moveBuildingPosition(LENGTH, 0);
		
		if (LENGTH == 3 && random.nextInt(3) == 0)
			place(Shooter.class, -1, 1);
		
		if (LENGTH == 4 && random.nextInt(3) == 0)
			place(Chicken.class, -1, 1);
	}
	
	private void buildPlatform() {
		Random random = getRandom(0x2440EC50);
		int length = random.nextInt(MAX_PLATFORM_LENGTH - MIN_PLATFORM_LENGTH + 1) + MIN_PLATFORM_LENGTH;
		float speed = random.nextFloat() * (MAX_PLATFORM_SPEED - MIN_PLATFORM_SPEED) + MIN_PLATFORM_SPEED;
		
		Platform platform = new Platform(getWorld());
		platform.setPlatformSpeed(speed);
		platform.setTargets(new Vector[] { Vector.getZero(), new Vector(length, 0) } );
		place(platform);
		platform.setOrigin();
		moveBuildingPosition(length, 0);
	}
	
	private boolean isPlatform(int indexOffset) {
		Random random = getRandom(0x390401E0, indexOffset);
		return (random.nextFloat() < 0.25f) && !isPlatform(indexOffset - 1);
	}
}

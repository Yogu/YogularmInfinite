package de.yogularm.building;

import java.util.Random;

import de.yogularm.Builder;
import de.yogularm.BuilderBase;
import de.yogularm.Vector;
import de.yogularm.components.Checkpoint;
import de.yogularm.components.Chicken;
import de.yogularm.components.Coin;
import de.yogularm.components.Heart;
import de.yogularm.components.Ladder;
import de.yogularm.components.Platform;
import de.yogularm.components.Shooter;
import de.yogularm.components.Stone;

public class Sky2 extends BuilderBase {
	private static final int[] MAX_X_OFFSET_BY_Y_OFFSET = {
		3 /* up */, 4 /* flat */,
		5 /* down 1 */, 5 /* down 2 */, 5 /* down 3*/
	};
	private static final int[] MAX_X_OFFSET_BY_Y_OFFSET_PLATFORM = {
		1 /* up */, 2 /* flat */,
		3 /* down 1 */, 3 /* down 2 */
	};
	
	private static final int CHECKPOINT_RANGE = 21; // every x structures a checkpoint
	
	private static BuilderSelector subBuilders = new BuilderSelector();
	
	static {
		subBuilders.add(new BridgeBuilder(), 1.0f);
		subBuilders.add(new PlatformBuilder(), 0.4f);
		subBuilders.add(new LadderBuilder(), 0.3f);
	}
	
	public void doBuild() {
		buildGap();

		Builder subBuilder = getSubBuilder(0);
		subBuilder.build(getWorld(), getCurrentIndex());

		if (isCheckpoint(0))
			place(Checkpoint.class, 0, 1);
		else
			place(Coin.class, 0, 1);
	}
	
	private void buildGap() {
		Random random = getRandom(0x1180E669);
		boolean isPlatform =
			(getSubBuilder(0) instanceof PlatformBuilder)
			|| !(getSubBuilder(-1) instanceof BridgeBuilder); // include ladders
		int[] xByY = isPlatform ? MAX_X_OFFSET_BY_Y_OFFSET_PLATFORM : MAX_X_OFFSET_BY_Y_OFFSET;
			
		int maxYOffset = xByY.length - 2;
		int yOffset = Math.min(random.nextInt(maxYOffset * 2 + 1) - maxYOffset, 1);
		int xOffset = random.nextInt(xByY[1 - yOffset]) + 1 /* at least one meter space */;
		moveBuildingPosition(xOffset, yOffset);
	}
	
	private Builder getSubBuilder(int indexOffset) {
		Random random = getRandom(0x390401E0, indexOffset);
		Builder builder;
		do {
			builder = subBuilders.get(random.nextFloat());
			// no two platforms behind each other, that is too difficult
		} while (builder instanceof PlatformBuilder && getSubBuilder(indexOffset - 1 ) instanceof PlatformBuilder);
		return builder;
	}
	
	private boolean isCheckpoint(int indexOffset) {
		int index = getCurrentIndex() + indexOffset;
		return
			// only be checkpoint if this is not a platform
			(index % CHECKPOINT_RANGE == CHECKPOINT_RANGE - 1
				&& !(getSubBuilder(indexOffset) instanceof PlatformBuilder))
				
			// be a checkpoint if last would be one but was a platform
			|| ((index - 1) % CHECKPOINT_RANGE == CHECKPOINT_RANGE - 1
				&& (getSubBuilder(indexOffset - 1) instanceof PlatformBuilder)); 
	}
	
	private static class BridgeBuilder extends BuilderBase {
		private static final int MIN_LENGTH = 1;
		private static final int MAX_LENGTH = 4;
		
		public void doBuild() {
			Random random = getRandom(0x115D567E);
			int length = random.nextInt(MAX_LENGTH - MIN_LENGTH + 1) + MIN_LENGTH;
			
			for (int i = 0; i < length; i++) {
				place(Stone.class, i + 1, 0);
			}
			moveBuildingPosition(length, 0);
			
			if (length == 3 && random.nextInt(2) == 0)
				place(Shooter.class, -1, 1);
			
			if (length == 4 && random.nextInt(2) == 0)
				place(Chicken.class, -1, 1);
		}
	}
	
	private static class PlatformBuilder extends BuilderBase {
		private static final int MIN_LENGTH = 4;
		private static final int MAX_LENGTH = 8;
		private static final int MAX_Y_OFFSET = 4;
		private static final float MIN_SPEED = 2;
		private static final float MAX_SPEED = 5;
		
		public void doBuild() {
			Random random = getRandom(0x2440EC50);
			int length = random.nextInt(MAX_LENGTH - MIN_LENGTH + 1) + MIN_LENGTH;
			float speed = random.nextFloat() * (MAX_SPEED - MIN_SPEED) + MIN_SPEED;

			// due to physics problems, upwards moving platforms are hard to ride on
			int yOffset = random.nextInt(2 * MAX_Y_OFFSET + 1) - MAX_Y_OFFSET;
			
			Platform platform = new Platform(getWorld());
			platform.setPlatformSpeed(speed);
			platform.setTargets(new Vector[] { Vector.getZero(), new Vector(length - 1, yOffset) } );
			place(platform, 1, 0);
			platform.setOrigin();
			moveBuildingPosition(length, yOffset);
		}
	}
	
	private static class LadderBuilder extends BuilderBase {
		private static final int MIN_BRIDGE_LENGTH = 1;////-2; // to make ladders without bridges more common
		private static final int MAX_BRIDGE_LENGTH = 3;//4;
		private static final int MIN_HEIGHT = -3;
		private static final int MAX_HEIGHT = 5;
		
		public void doBuild() {
			Random random = getRandom(0x1448273F);
			int length = Math.max(0, random.nextInt(MAX_BRIDGE_LENGTH - MIN_BRIDGE_LENGTH + 1) + MIN_BRIDGE_LENGTH);
			int height = random.nextInt(MAX_HEIGHT - MIN_HEIGHT + 1) + MIN_HEIGHT;
			
			if (height < 0)
				buildDownward(length, - height);
			else
				buildUpward(length, height);
		}
		
		private void buildUpward(int length, int height) {
			moveBuildingPosition(1, 0);
			
			// bridge
			for (int i = 0; i < length; i++) {
				place(Stone.class, i, 0);
			}
			moveBuildingPosition(length - 1, 0);
			
			// ladder
			for (int i = 0; i < height; i++) {
				place(Ladder.class, 0, i + 1);
			}
			moveBuildingPosition(0, height);
		}
		
		private void buildDownward(int length, int height) {
			length = Math.max(length, 2);
			moveBuildingPosition(1, 0);
			
			// ladder
			for (int i = 0; i < height; i++) {
				place(Ladder.class, 0, 1 - i);
			}
			moveBuildingPosition(0, -height + 1);
			
			// bridge
			for (int i = 0; i < length; i++) {
				place(Stone.class, i, 0);
			}
			moveBuildingPosition(length - 1, 0);
		}
	}
}

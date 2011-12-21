package de.yogularm.building;

import java.util.Random;

import de.yogularm.Builder;
import de.yogularm.BuilderBase;
import de.yogularm.ComponentCollection;
import de.yogularm.Config;
import de.yogularm.Vector;
import de.yogularm.components.Checkpoint;
import de.yogularm.components.Chicken;
import de.yogularm.components.Coin;
import de.yogularm.components.Ladder;
import de.yogularm.components.Platform;
import de.yogularm.components.Shooter;
import de.yogularm.components.Stone;

public class Sky2 extends CompositeBuilder {
	private int index;
	private int maxLength;
	private int forceYDirection;
	private Builder currentBuilder;
	private Builder lastBuilder;
	
	private static final int[] MAX_X_OFFSET_BY_Y_OFFSET = {
		3 /* up */, 4 /* flat */,
		5 /* down 1 */, 5 /* down 2 */, 5 /* down 3*/
	};
	private static final int[] MAX_X_OFFSET_BY_Y_OFFSET_PLATFORM = {
		1 /* up */, 2 /* flat */,
		3 /* down 1 */, 3 /* down 2 */
	};
	
	public void init(ComponentCollection components, Vector buildingPosition) {
		super.init(components, buildingPosition);
		index = 0;
		lastBuilder = null;
		currentBuilder = null;
	}
	
	public Sky2() {
		addBuilder(new BridgeBuilder(), 1.0f);
		addBuilder(new PlatformBuilder(), 0.4f);
		addBuilder(new LadderBuilder(), 0.3f);
	}
	
	public void build() {
		build(0, -1);
	}
	
	/**
	 * Builds the next structure
	 * 
	 * If maxLength is too small, nothing is created.
	 * 
	 * @param forceYDirection negative values to force the structure to go downwards, positivte values
	 *   to force it going upwards, or 0 to let the random number generator decide. If the absolute
	 *   value is 2, ladders or platforms are used to make a great jump into the specified direction
	 * @param maxLength The maximum length of the structure, or -1 to do not specify a max length
	 */
	public void build(int forceYDirection, int maxLength) {
		if (maxLength >= 0 && maxLength < 6)
			return;
		
		lastBuilder = currentBuilder;
		currentBuilder = getSubBuilder();
		
		Vector gap = buildGap(forceYDirection);
		
		// Store length into field so that it can be used by the sub-builders
		this.maxLength = maxLength - (int)gap.getX();
		this.forceYDirection = forceYDirection;

		subBuild(currentBuilder);

		if (isCheckpoint(0))
			place(Checkpoint.class, 0, 1);
		else
			place(Coin.class, 0, 1);

		index++;
	}
	
	private Vector buildGap(int forceYDirection) {
		Random random = new Random();
		boolean isPlatform =
			(currentBuilder instanceof PlatformBuilder)
			|| !(lastBuilder instanceof BridgeBuilder); // include ladders*/
		int[] xByY = isPlatform ? MAX_X_OFFSET_BY_Y_OFFSET_PLATFORM : MAX_X_OFFSET_BY_Y_OFFSET;

		int maxYOffset = xByY.length - 2;
		int yOffset = random.nextInt(maxYOffset * 2 + 1) - maxYOffset;
		if (forceYDirection < 0)
			yOffset = - Math.abs(yOffset);
		else if (forceYDirection > 0)
			yOffset = Math.abs(yOffset);
		yOffset = Math.min(1, yOffset);
		
		int xOffset = random.nextInt(xByY[1 - yOffset]) + 1 /* at least one meter space */;
		moveBuildingPosition(xOffset, yOffset);
		return new Vector(xOffset, yOffset);
	}
	
	private Builder getSubBuilder() {
		Random random = new Random();
		Builder builder;
		do {
			builder = getBuilder(random.nextFloat());
			// no two platforms behind each other, that is too difficult
		} while ((lastBuilder instanceof PlatformBuilder && builder instanceof PlatformBuilder)
			|| (Math.abs(forceYDirection) == 2 && builder instanceof BridgeBuilder));
		return builder;
	}
	
	private boolean isCheckpoint(int indexOffset) {
		int i = index + indexOffset;
		/*return
			// only be checkpoint if this is not a platform
			(i % Config.CHECKPOINT_RANGE == Config.CHECKPOINT_RANGE - 1
				&& !(getSubBuilder(indexOffset) instanceof PlatformBuilder))
				
			// be a checkpoint if last would be one but was a platform
			|| ((i - 1) % Config.CHECKPOINT_RANGE == Config.CHECKPOINT_RANGE - 1
				&& (getSubBuilder(indexOffset - 1) instanceof PlatformBuilder));*/
		return i % Config.CHECKPOINT_RANGE == Config.CHECKPOINT_RANGE - 1;
	}
	
	private class BridgeBuilder extends BuilderBase {
		private static final int MIN_LENGTH = 1;
		private static final int MAX_LENGTH = 4;
		
		public void build() {
			Random random = new Random();
			int ml = maxLength >= 0 ? Math.min(maxLength, MAX_LENGTH) : MAX_LENGTH;
			int length = random.nextInt(ml - MIN_LENGTH + 1) + MIN_LENGTH;
			
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
	
	private class PlatformBuilder extends BuilderBase {
		private static final int MIN_LENGTH = 4;
		private static final int MAX_LENGTH = 8;
		private static final int MAX_Y_OFFSET = 4;
		private static final float MIN_SPEED = 2;
		private static final float MAX_SPEED = 5;
		
		public void build() {
			Random random = new Random();
			int ml = maxLength >= 0 ? Math.min(maxLength, MAX_LENGTH) : MAX_LENGTH;
			int length = random.nextInt(ml - MIN_LENGTH + 1) + MIN_LENGTH;
			float speed = random.nextFloat() * (MAX_SPEED - MIN_SPEED) + MIN_SPEED;

			int yOffset = random.nextInt(2 * MAX_Y_OFFSET + 1) - MAX_Y_OFFSET;
			if (forceYDirection < 0)
				yOffset = - Math.abs(yOffset);
			else if (forceYDirection > 0)
				yOffset = Math.abs(yOffset);
			if (Math.abs(forceYDirection) == 2)
				yOffset = (int)Math.signum(forceYDirection) * MAX_Y_OFFSET;
			
			Platform platform = new Platform(getComponents());
			platform.setPlatformSpeed(speed);
			platform.setTargets(new Vector[] { Vector.getZero(), new Vector(length - 1, yOffset) } );
			place(platform, 1, 0);
			platform.setOrigin();
			moveBuildingPosition(length, yOffset);
		}
	}
	
	private class LadderBuilder extends BuilderBase {
		private static final int MIN_BRIDGE_LENGTH = 1;////-2; // to make ladders without bridges more common
		private static final int MAX_BRIDGE_LENGTH = 3;//4;
		private static final int MIN_HEIGHT = -3;
		private static final int MAX_HEIGHT = 5;
		
		public void build() {
			Random random = new Random();
			int ml = maxLength >= 0 ? Math.min(maxLength, MAX_BRIDGE_LENGTH) : MAX_BRIDGE_LENGTH;
			int length = Math.max(0, random.nextInt(ml - MIN_BRIDGE_LENGTH + 1) + MIN_BRIDGE_LENGTH);
			
			int height = random.nextInt(MAX_HEIGHT - MIN_HEIGHT + 1) + MIN_HEIGHT;
			if (forceYDirection == -1)
				height = - Math.abs(height);
			else if (forceYDirection == 1)
				height = Math.abs(height);
			if (Math.abs(forceYDirection) == 2)
				height = (int)Math.signum(forceYDirection) * MAX_HEIGHT;
			
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

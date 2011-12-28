package de.yogularm.building.old.general;

import de.yogularm.building.old.BuilderBase;
import de.yogularm.components.ComponentCollection;
import de.yogularm.components.general.Ladder;
import de.yogularm.components.general.Platform;
import de.yogularm.components.general.Stone;
import de.yogularm.geometry.Vector;

public class GroundBuilder extends BuilderBase {
	private Sky2 sky = new Sky2();
	private int skyRecreateCounter;

	private double currentHeight; // [m]
	private double currentSlope; // [m/m]
	private double currentSlopeChange; // [1/m]

	private static final double MAX_SLOPE_CHANGE = 3;
	private static final double MAX_SLOPE = 3;
	private static final double MAX_HEIGHT = 100;
	private static final double SLOPE_BREAK_EXPONENT = 4;
	private static final double HEIGHT_BREAK_EXPONENT = 4;

	private static final int MIN_SKY_HEIGHT = 5; // sky is removed temporarily
	private static final int LOW_SKY_HEIGHT = 10; // sky is forced to go upward
	private static final int INIT_SKY_HEIGHT = 10;
	private static final int HIGH_SKY_HEIGHT = 30; // sky is forced to go downward
	private static final int MAX_SKY_HEIGHT = 40; // sky is removed temporarily
	private static final int SKY_RECREATE_DELAY = 10; // distance without sky if removed [m]
	private static final int SKY_DELAY = 5; // sky is created x meters behind the ground

	public void init(ComponentCollection components, Vector buildingPosition) {
		super.init(components, buildingPosition);
		initSky();
	}

	private void initSky() {
		sky.init(getComponents(), getBuildingPosition().add(new Vector(1, INIT_SKY_HEIGHT)));
		skyRecreateCounter = -1;

		place(Stone.class, 1, INIT_SKY_HEIGHT);
		Platform platform = new Platform(getComponents());
		platform.setTargets(new Vector[] { Vector.getZero(), new Vector(0, INIT_SKY_HEIGHT - 1) });
		place(platform, 0, 1);
		platform.setOrigin();
	}

	public void build() {
		buildGround();
		buildSky();

		/*
		 * if (isCheckpoint(0)) place(Checkpoint.class, 0, 1); else
		 * place(Coin.class, 0, 1);
		 */
	}

	private void calcualateNextHeight() {
		double randomSlopeChange = (Math.random() - 0.5) * 2 * MAX_SLOPE_CHANGE;
		double breakingSlopeChange = -Math.signum(currentSlope)
				* Math.abs(Math.pow(currentSlope / MAX_SLOPE, SLOPE_BREAK_EXPONENT));
		currentSlopeChange = randomSlopeChange + breakingSlopeChange;

		currentSlope += currentSlopeChange;
		currentSlope -= Math.signum(currentHeight)
				* Math.abs(Math.pow(currentHeight / MAX_HEIGHT, HEIGHT_BREAK_EXPONENT));

		currentHeight += currentSlope;
	}

	private void buildGround() {
		int oldHeight = (int) getBuildingPosition().getY();
		// Check if Y component of building position has changed
		if (Math.abs(oldHeight - currentHeight) > 2)
			currentHeight = getBuildingPosition().getY();
		calcualateNextHeight();

		int newHeight = (int) currentHeight;
		int increase = newHeight - oldHeight;
		if (increase < 0) {
			for (int i = increase + 1; i < 0; i++)
				place(Stone.class, 0, i);
			moveBuildingPosition(1, increase);
			place(Stone.class);
		} else {
			for (int i = 1; i < increase; i++) {
				place(Ladder.class, 0, i);
				place(Stone.class, 1, i);
			}
			moveBuildingPosition(1, increase);
			place(Stone.class);
		}
	}

	private void buildSky() {
		if (sky.getBuildingPosition().getX() + SKY_DELAY < getBuildingPosition().getX()) {
			float distance = sky.getBuildingPosition().getY() - getBuildingPosition().getY();
			if (skyRecreateCounter >= 0) {
				skyRecreateCounter--;
				if (skyRecreateCounter == 0)
					initSky();
			}/* else if (distance <= MIN_SKY_HEIGHT || distance >= MAX_SKY_HEIGHT)
				skyRecreateCounter = SKY_RECREATE_DELAY;*/
			else {
				int forceYDirection;
				if (distance >= MAX_SKY_HEIGHT)
					forceYDirection = 2;
				if (distance >= HIGH_SKY_HEIGHT)
					forceYDirection = -1;
				else if (distance <= LOW_SKY_HEIGHT)
					forceYDirection = 1;
				else if (distance <= MIN_SKY_HEIGHT)
					forceYDirection = -2;
				else
					forceYDirection = 0;
				sky.build(forceYDirection, -1);
			}
		}
	}

	private boolean isCheckpoint(int indexOffset) {
		return false;
		/*
		 * int index = getCurrentIndex() + indexOffset; return index %
		 * Config.CHECKPOINT_RANGE == Config.CHECKPOINT_RANGE - 1;
		 */
	}
}

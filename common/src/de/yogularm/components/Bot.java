package de.yogularm.components;

import de.yogularm.geometry.Direction;
import de.yogularm.geometry.Vector;

public abstract class Bot extends Character {
	private static final float WALK_SPEED = 4;
	private static final float JUMP_SPEED = 8;
	private static final float STUCK_BIG_CHANGE = 0.5f; // If bot can move such a step, it is not stuck
	private static final float STUCK_TURN_TIME = 1.5f; // Bot should turn after being stuck n seconds
	
	private int direction;
	private boolean stopping;
	private float xOfLastBigChange;
	private float lastXBigChangeTime;
	
	public Bot(ComponentCollection collection) {
		super(collection);
		direction = Math.random() > 0.5 ? 1 : -1;
	}
	
	public void update(float elapsedTime) {
		super.update(elapsedTime);

		walk(elapsedTime);
	}
	
	public int getDirection() {
		return direction;
	}
	
	private void walk(float elapsedTime) {
		if (shouldStop()) {
			setWalkSpeed(0);
			stopping = true;
		} else if (standsOnGround() && stopping) {
			setWalkSpeed(direction * WALK_SPEED);
			stopping = false;
		} else if (!stopping) {
			setWalkSpeed(direction * WALK_SPEED);
		}
			
		if (shouldJump() && standsOnGround())
			setSpeed(getSpeed().changeY(JUMP_SPEED));
		
		if (Math.abs(xOfLastBigChange - getPosition().getX()) > STUCK_BIG_CHANGE) {
			xOfLastBigChange = getPosition().getX();
			lastXBigChangeTime = 0;
		} else {
			lastXBigChangeTime += elapsedTime;
			if (!stopping && standsOnGround() && lastXBigChangeTime > STUCK_TURN_TIME) {
				direction = -direction;
				lastXBigChangeTime = 0;
			}
		}
	}
	
	private boolean shouldStop() {
		return !standsOnGround() && hasBlockBelow(getPosition())
			&& !hasBlockBelow(getPosition().add(new Vector(direction, 0)));
	}
	
	private boolean shouldJump() {
		return hasSolidAt(getPosition().add(new Vector(direction, 0))) // obstacle
			|| standsBeforeGap();
	}
	
	private boolean standsBeforeGap() {
		return !hasBlockBelow(getPosition())
			&& !hasBlockBelow(getPosition().add(new Vector(direction, -1)))
			&& !hasBlockBelow(getPosition().add(new Vector(direction * 2, -1)));
	}

	protected void onCollision(Body other, Direction direction, boolean isCauser) {
		super.onCollision(other, direction, isCauser);
		/*if (other.isSolid()) {
			if (standsOnGround()) {
				if (direction == Direction.LEFT)
					this.direction = 1;
				else if (direction == Direction.RIGHT)
					this.direction = -1;
			}
		}*/
	}
	
	// helper functions
	private boolean hasBlockBelow(Vector position) {
		return ComponentCollectionUtils.hasBlockBelow(getCollection(), position);
	}

	private boolean hasSolidAt(Vector position) {
		return ComponentCollectionUtils.hasSolidAt(getCollection(), position);
	}
}

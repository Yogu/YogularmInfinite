package de.yogularm;

public abstract class Bot extends Character {
	private static final float WALK_SPEED = 1;
	private static final float JUMP_SPEED = 8;
	
	private int direction;
	private boolean stopping;
	
	public Bot(World world) {
		super(world);
		//direction = Math.random() > 0.5 ? 1 : -1;
	}
	
	public void update(float elapsedTime) {
		super.update(elapsedTime);

		//walk(elapsedTime);
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
			
		/*if (shouldJump() && standsOnGround())
			setSpeed(getSpeed().changeY(JUMP_SPEED));*/
	}
	
	private boolean shouldStop() {
		return !standsOnGround() && getWorld().hasBlockBelow(getPosition())
			&& !getWorld().hasBlockBelow(getPosition().add(new Vector(direction, 0)));
	}
	
	private boolean shouldJump() {
		return getWorld().hasSolidAt(getPosition().add(new Vector(direction, 0))) // obstacle
			|| standsBeforeGap();
	}
	
	private boolean standsBeforeGap() {
		return !getWorld().hasBlockBelow(getPosition())
			&& !getWorld().hasBlockBelow(getPosition().add(new Vector(direction, -1)))
			&& !getWorld().hasBlockBelow(getPosition().add(new Vector(direction * 2, -1)));
	}
	
	public int getDirection() {
		return direction;
	}

	protected void onCollision(Body other, Direction direction, boolean isCauser) {
		super.onCollision(other, direction, isCauser);
		/*if (standsOnGround()) {
			if (direction == Direction.LEFT)
				this.direction = 1;
			else if (direction == Direction.RIGHT)
				this.direction = -1;
		}*/
	}
}

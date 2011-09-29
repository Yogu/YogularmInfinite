package de.yogularm;

import javax.media.opengl.GL2;

import de.yogularm.components.Arrow;

public abstract class Character extends Entity {
	private float life = 1;
	private boolean isDead = false;
	private float immuneTime;
	private float walkSpeed = 0;
	private float climbSpeed;
	
	public Character(World world) {
		super(world);
		setIsGravityAffected(true);
		setIsShiftable(true);
	}

	public void update(float elapsedTime) {
		boolean climbing = canClimb() && climbSpeed > 0;
		super.update(elapsedTime);
		if (climbing && !canClimb())
			jump(true);

		applyWalkSpeed(walkSpeed);
		applyClimbSpeed(climbSpeed);
		
		if (immuneTime > 0)
			immuneTime -= elapsedTime;

		if (life <= 0)
			die();

		if (-getSpeed().getY() > Config.DEATH_FALL_SPEED)
			onDeathFall();
	}

	protected void onCollision(Body other, Direction direction, boolean isCauser) {
		super.onCollision(other, direction, isCauser);

		if (other instanceof Arrow)
			decLife(1);
	}

	public void die() {
		onDie();
		isDead = true;
	}

	protected void onDie() {
		remove();
	}

	public boolean isImmune() {
		return immuneTime > 0;
	}

	public void setLife(float life) {
		this.life = life;
	}

	public float getLife() {
		return life;
	}

	public void decLife(float delta) {
		if (immuneTime <= 0) {
			life = Math.max(0, life - delta);
			if (life == 0)
				die();
		}
		if (life > 0)
			immuneTime = Config.IMMUNE_TIME;
	}

	public boolean isDead() {
		return isDead;
	}

	public void draw(GL2 gl) {
		if (immuneTime <= 0)
			super.draw(gl);
		else {
			getDrawable().draw(gl, 0.5f);
		}
	}
	
	public void setWalkSpeed(float speed) {
		walkSpeed = speed;
	}
	
	public float getWalkSpeed() {
		return walkSpeed;
	}
	
	public void setClimbSpeed(float speed) {
		climbSpeed = speed;
	}
	
	public float getClimbSpeed() {
		return climbSpeed;
	}
	
	public void jump() {
		jump(false);
	}
	
	public void jump(boolean unconditional) {
		if (standsOnGround() || canClimb() || unconditional)
			setSpeed(getSpeed().changeY(Config.PLAYER_JUMP_SPEED));
	}
	
	protected float getHeightOverGround() {
		Body blockBelowLeft = getWorld().getBlockBelow(getPosition().changeX((float) Math.floor(getPosition().getX())));
		Body blockBelowRight = getWorld().getBlockBelow(getPosition().changeX((float) Math.ceil(getPosition().getX())));
		float height = Float.POSITIVE_INFINITY;
		if (blockBelowLeft != null)
			height = Math.min(height, getOuterBounds().getBottom() - blockBelowLeft.getOuterBounds().getTop());
		if (blockBelowRight != null)
			height = Math.min(height, getOuterBounds().getBottom() - blockBelowRight.getOuterBounds().getTop());
		return height;
	}
	
	protected void onDeathFall() {
		die();
	}
}

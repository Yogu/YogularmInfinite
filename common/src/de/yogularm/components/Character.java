package de.yogularm.components;

import de.yogularm.Config;
import de.yogularm.components.general.Arrow;
import de.yogularm.drawing.Color;
import de.yogularm.drawing.ColoredDrawable;
import de.yogularm.drawing.Drawable;
import de.yogularm.geometry.Direction;
import de.yogularm.geometry.Vector;

public abstract class Character extends Entity {
	private float life = 1;
	private boolean isDead = false;
	private float immuneTime;
	private float walkSpeed = 0;
	private float climbSpeed;

	public Character(ComponentCollection collection) {
		super(collection);
		setIsGravityAffected(true);
		setIsShiftable(true);
	}

	public void update(float elapsedTime) {
		boolean climbing = isClimbing() && climbSpeed > 0;
		super.update(elapsedTime);
		if (climbing && !isClimbing())
			jump(true);

		applyWalkSpeed(walkSpeed);
		applyClimbSpeed(climbSpeed);

		if (immuneTime > 0)
			immuneTime -= elapsedTime;

		if (life <= 0)
			die();

		if (-getSpeed().getY() > Config.DEATH_FALL_SPEED)
		//if (getPosition().getY() < Config.DEATH_Y_POS)
			onDeathFall();
	}

	protected void onCollision(Component other, Direction direction, boolean isCauser) {
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

	public Drawable getDrawable() {
		Drawable drawable = super.getDrawable();
		if (immuneTime > 0)
			return new ColoredDrawable(drawable, new Color(1, 1, 1, 0.5f));
		else
			return drawable;
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
		if (standsOnGround() || isClimbing() || unconditional)
			setSpeed(getSpeed().changeY(getShiftSpeed().getY() + Config.PLAYER_JUMP_SPEED));
	}

	protected float getHeightOverGround() {
		Vector v = getPosition().changeX((float) Math.floor(getPosition().getX()));
		Component blockBelowLeft = ComponentCollectionUtils.getBlockBelow(getCollection(), v);
		v = getPosition().changeX((float) Math.ceil(getPosition().getX()));
		Component blockBelowRight = ComponentCollectionUtils.getBlockBelow(getCollection(), v);
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

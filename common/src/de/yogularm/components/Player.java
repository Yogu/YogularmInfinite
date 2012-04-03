package de.yogularm.components;

import de.yogularm.Config;
import de.yogularm.Res;
import de.yogularm.components.general.Checkpoint;
import de.yogularm.components.general.Coin;
import de.yogularm.components.general.Heart;
import de.yogularm.geometry.Direction;
import de.yogularm.geometry.Rect;
import de.yogularm.geometry.Vector;

public class Player extends Character {
	private int collectedCoins;
	private float fallTime;
	private Vector checkpoint;
	
	public Player(ComponentCollection collection) {
		super(collection);
		setDrawable(Res.images.yogu);
		setBounds(new Rect(0.18721875f, 0.080890625f, 0.801515625f, 0.9359375f));
		setLife(Config.INIT_LIFE);
		setMass(20);
		setCanClimb(true);
		checkpoint = getPosition();
	}
	
	public void update(float elapsedTime) {
		super.update(elapsedTime);
		updateDrawable(elapsedTime);
	}

	protected void onCollision(Body other, Direction direction, boolean isCauser) {
		super.onCollision(other, direction, isCauser);
		
		if (other instanceof Coin)
			collectedCoins++;
		else if (other instanceof Heart)
			setLife(getLife() + 1);
		else if (other instanceof Checkpoint)
			checkpoint = other.getPosition();
	}
	
	protected void onDie() {
		// do not remove
		setDrawable(Res.images.yoguFalling);
	}
	
	public int getCollectedCoins() {
		return collectedCoins;
	}
	
	public void setDirection(float direction) {
		setWalkSpeed(direction * Config.PLAYER_SPEED);
	}
	
	private void updateDrawable(float elapsedTime) {
		if (getActualSpeed().getY() < -3 && !isClimbing()) {
			if (getHeightOverGround() > 3)
				fallTime += elapsedTime;
		} else
			fallTime = 0;
		if (isDead() || isImmune() || fallTime >= 0.5f)
			setDrawable(Res.images.yoguFalling);
		else if (getWalkSpeed() != 0)
			setAnimation(Res.animations.yoguWalking);
		else
			setDrawable(Res.images.yogu);
	}
	
	protected void onDeathFall() {
		setPosition(checkpoint);
		decLife(1);
	}
}

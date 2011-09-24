package de.yogularm;

import de.yogularm.components.Coin;
import de.yogularm.components.Heart;

public class Player extends Character {
	private int collectedCoins;
	private float fallTime;
	
	public Player(World world) {
		super(world);
		setDrawable(Res.images.yogu);
		setBounds(new Rect(0.18721875f, 0.080890625f, 0.801515625f, 0.9359375f));
		setLife(Config.MAX_LIFE);
		setMass(20);
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
	}
	
	protected void onDie() {
		// do not remove
		setDrawable(Res.images.yoguFalling);
	}
	
	public int getCollectedCoins() {
		return collectedCoins;
	}
	
	public void setDirection(int direction) {
		setWalkSpeed(direction * Config.PLAYER_SPEED);
	}
	
	private void updateDrawable(float elapsedTime) {
		if (getSpeed().getY() < 0 && !canClimb()) {
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
}

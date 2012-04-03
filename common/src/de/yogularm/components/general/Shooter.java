package de.yogularm.components.general;

import de.yogularm.Config;
import de.yogularm.Res;
import de.yogularm.components.Block;
import de.yogularm.components.ComponentCollection;
import de.yogularm.geometry.Rect;
import de.yogularm.geometry.Vector;

public class Shooter extends Block {
	private float rechargeTime = 0;
	 
	public static final float MIN_RECHARGE_TIME = 2;
	public static final float MAX_RECHARGE_TIME = 5;
	public static final float MIN_ANGLE = 15;
	public static final float MAX_ANGLE = 45;
	
	public Shooter(ComponentCollection collection) {
		super(collection);
		setDrawable(Res.images.shooter);
		setBounds(new Rect(0, 0, 1, 0.6015625f));
		rechargeTime = getRechargeTime();
	}
	
	public void update(float elapsedTime) {
		super.update(elapsedTime);
		
		if (!isNetworkComponent()) {
			if (rechargeTime > 0)
				rechargeTime -= elapsedTime;
			if (rechargeTime <= 0)
				shoot();
		}
	}
	
	private void shoot() {
		Arrow arrow = new Arrow(getCollection(), this);
		arrow.setPosition(getPosition().add(new Vector(0, -0.2f))); // arrow shoots out of pipe
		float angle = (float)Math.toRadians(180 - getAngle());
		arrow.setSpeed(new Vector((float)Math.cos(angle), (float)Math.sin(angle)).multiply(Config.ARROW_SPEED));
		getCollection().add(arrow);
		rechargeTime = getRechargeTime();
	}
	
	private float getRechargeTime() {
		return (float)(MIN_RECHARGE_TIME + Math.random() * (MAX_RECHARGE_TIME - MIN_RECHARGE_TIME));
	}
	
	private float getAngle() {
		return (float)(MIN_ANGLE + Math.random() * (MAX_ANGLE - MIN_ANGLE));
	}
}

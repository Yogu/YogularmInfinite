package de.yogularm.components;

import de.yogularm.Block;
import de.yogularm.Config;
import de.yogularm.Image;
import de.yogularm.Rect;
import de.yogularm.Res;
import de.yogularm.Vector;
import de.yogularm.World;

public class Shooter extends Block {
	private float rechargeTime = 0;
	 
	public static final float MIN_RECHARGE_TIME = 2;
	public static final float MAX_RECHARGE_TIME = 5;
	public static final float MIN_ANGLE = 15;
	public static final float MAX_ANGLE = 45;
	
	public Shooter(World world) {
		super(world);
		setDrawable(Res.images.shooter);
		setBounds(new Rect(0, 0, 1, 0.6015625f));
	}
	
	public void update(float elapsedTime) {
		super.update(elapsedTime);
		
		if (rechargeTime > 0)
			rechargeTime -= elapsedTime;
		if (rechargeTime <= 0)
			shoot();
	}
	
	private void shoot() {
		Arrow arrow = new Arrow(getWorld(), this);
		arrow.setPosition(getPosition());
		float angle = (float)Math.toRadians(180 - getAngle());
		arrow.setSpeed(new Vector((float)Math.cos(angle), (float)Math.sin(angle)).multiply(Config.ARROW_SPEED));
		getWorld().addComponent(arrow);
		rechargeTime = getRechargeTime();
	}
	
	private float getRechargeTime() {
		return (float)(MIN_RECHARGE_TIME + Math.random() * (MAX_RECHARGE_TIME - MIN_RECHARGE_TIME));
	}
	
	private float getAngle() {
		return (float)(MIN_ANGLE + Math.random() * (MAX_ANGLE - MIN_ANGLE));
	}
}

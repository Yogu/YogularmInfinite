package de.yogularm.components;

import de.yogularm.Block;
import de.yogularm.Rect;
import de.yogularm.Res;
import de.yogularm.Vector;
import de.yogularm.World;
import de.yogularm.drawing.CombinedDrawable;
import de.yogularm.drawing.RenderTransformation;

public class Platform extends Block {
	private static final float DEFAULT_SPEED = 3;
	private static final float DEFAULT_ACCELERATION = 10;
	private static final float DEFAULT_PAUSE_TIME = 0.4f;
	private static final float ANIMATION_FREQUENCY_PER_PLATFORM_SPEED = 0.5f; // [Hz/(m/s)] = [1/m]
	
	private Vector[] targets = new Vector[0];
	private float speed = DEFAULT_SPEED;
	private float acceleration = DEFAULT_ACCELERATION;
	private int currentTarget;
	private Vector origin;
	private boolean enableBreaking;
	private float pauseTime = DEFAULT_PAUSE_TIME;
	private float elapsedPauseTime;
	private RenderTransformation propellerTransformation;
	private float animationTime;
	
	public Platform(World world) {
		super(world);
		propellerTransformation = new RenderTransformation(Res.images.platformPropeller);
		propellerTransformation.setRotationCenter(new Vector(0.5f, 0.5f));
		CombinedDrawable drawable = new CombinedDrawable();
		drawable.add(propellerTransformation);
		drawable.add(Res.images.platform);
		setDrawable(drawable);
		setBounds(new Rect(0, 0.8125f, 1, 1));
		
		origin = getPosition();
		enableBreaking = true;
	}
	
	public Vector[] getTargets() {
		return targets;
	}
	
	public void setTargets(Vector[] targets) {
		if (targets == null)
			throw new NullPointerException("targets is null");
		this.targets = targets;
	}
	
	public float getPlatformSpeed() {
		return speed;
	}
	
	public void setPlatformSpeed(float speed) {
		this.speed = speed;
	}
	
	public float getAcceleration() {
		return speed;
	}
	
	public void setAcceleration(float acceleration) {
		this.acceleration = acceleration;
	}
	
	public void setPosition(Vector position, boolean moveOrigin) {
		super.setPosition(position);
		if (moveOrigin)
			origin = position;
	}
	
	public void setPosition(Vector position) {
		setPosition(position, false);
	}
	
	public void setOrigin() {
		origin = getPosition();
	}
	
	public void setOrigin(Vector value) {
		if (value == null)
			throw new NullPointerException("value is null");
		origin = value;
	}
	
	public boolean isBreakingEnabled() {
		return enableBreaking;	
	}
	
	public void setIsBreakingEnabled(boolean value) {
		enableBreaking = value;
	}
	
	public void update(float elapsedTime)  {
		super.update(elapsedTime);
		
		if (targets.length > 1) {
			Vector target = targets[currentTarget].add(origin);
			Vector source = currentTarget >= targets.length - 1 ? targets[0] : targets[currentTarget + 1];
			source = source.add(origin);
			if (hasArrived(source, target)) {
				if (enableBreaking && elapsedPauseTime < pauseTime) {
					elapsedPauseTime += elapsedTime;
					doBreaking();
				} else {
					elapsedPauseTime = 0;
					currentTarget++;
					if (currentTarget >= targets.length)
						currentTarget = 0;
				}
			} else {
				if (enableBreaking && needsToBreak(target))
					doBreaking();
				else
					headTo(target);
			}
		}
		
		animationTime += elapsedTime;
		float length = 1 / (ANIMATION_FREQUENCY_PER_PLATFORM_SPEED * speed);
		animationTime %= length;
		propellerTransformation.setScale(new Vector((float)Math.cos(animationTime / length * 2 * Math.PI), 1));
	}
	
	private boolean hasArrived(Vector source, Vector target) {
		// Platform has arrived when at the other side of target than source
		// (angle difference is larger than 90 deg)
		Vector distance = target.subtract(getPosition());
		if (distance.getLength() == 0)
			return true;
		
		Vector path = target.subtract(source);
		float angle = Vector.getAngle(distance, path);
		return angle > 90;
	}
	
	public boolean needsToBreak(Vector target) {
		float distance = target.subtract(getPosition()).getLength();
		float speed = getSpeed().getLength();
		float breakingDistance = speed * speed / (2 * acceleration);
		return distance <= breakingDistance;
	}
	
	private void headTo(Vector target) {
		Vector diff = target.subtract(getPosition());
		Vector direction = diff.normalize();
		applyXForceToSpeed(acceleration * getMass(), direction.getX() * speed);
		applyYForceToSpeed(acceleration * getMass(), direction.getY() * speed);
	}
	
	private void doBreaking() {
		applyXForceToSpeed(acceleration * getMass(), 0);
		applyYForceToSpeed(acceleration * getMass(), 0);
	}
}

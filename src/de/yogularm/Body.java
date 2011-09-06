package de.yogularm;

import de.yogularm.components.Chicken;

public class Body extends Component {
	private Rect bounds;
	private float mass = 1.0f;
	private Vector momentum;
	private Vector collectedForce;
	private boolean isGravityAffected = false;
	private boolean isSolid = true;
	private boolean isShiftable = false;
	private float groundSpeed = 0;
	private float walkSpeed = 0;

	public Body(World world) {
		super(world);
		bounds = new Rect(0, 0, 1, 1);
		collectedForce = Vector.getZero();
		momentum = Vector.getZero();
	}
	
	public Rect getBounds() {
		return bounds;
	}
	
	public Rect getOuterBounds() {
		return bounds.add(getPosition());
	}
	
	public void setBounds(Rect bounds) {
		if (bounds == null)
			throw new NullPointerException("bounds is null");
		this.bounds = bounds;
	}
	
	public Vector getSpeed() {
		return momentum.divide(mass);
	}
	
	public Vector getMomentum() {
		return momentum;
	}
	
	public boolean isGravityAffected() {
		return isGravityAffected;
	}
	
	public void setIsGravityAffected(boolean value) {
		isGravityAffected = value;
	}
	
	public boolean isSolid() {
		return isSolid;
	}
	
	public void setIsSolid(boolean value) {
		isSolid = value;
	}
	
	public boolean isShiftable() {
		return isShiftable;
	}
	
	public void setIsShiftable(boolean value) {
		isShiftable = value;
	}
	
	public float getMass() {
		return mass;
	}
	
	public void setMass(float mass) {
		this.mass = mass;
	}
	
	public void setSpeed(Vector speed) {
		if (speed == null)
			throw new NullPointerException("speed is null");
		this.momentum = speed.multiply(mass);
	}
	
	public float getGroundSpeed() {
		return groundSpeed;
	}
	
	public void setMomentum(Vector momentum) {
		if (momentum == null)
			throw new NullPointerException("momentum is null");
		this.momentum = momentum;
	}
	
	public void applyForce(Vector force) {
		if (force == null)
			throw new NullPointerException("force is null");
		collectedForce = collectedForce.add(force);
	}
	
	public void applyXForceToSpeed(float force, float speed) {
		float speedDiff = speed - getSpeed().getX();
		if (speedDiff != 0) {
			float direction = speedDiff / Math.abs(speedDiff);
			if (Math.abs(speedDiff) / getWorld().getFrameTime() < force)
				System.out.println("force: " + force);
			force = Math.min(force, Math.abs(speedDiff) / getWorld().getFrameTime());
			applyForce(new Vector(direction * force, 0));
		}
	}
	
	public void applyYForceToSpeed(float force, float speed) {
		float speedDiff = speed - getSpeed().getY();
		float direction = speedDiff / Math.abs(speedDiff);
		force = Math.min(force, speedDiff / getWorld().getFrameTime());
		applyForce(new Vector(0, direction * force));
	}
	
	public void setWalkSpeed(float speed) {
		walkSpeed = speed;
	}
	
	public float getWalkSpeed() {
		return walkSpeed;
	}
	
	public void update(float elapsedTime) {
		super.update(elapsedTime);
		
		if (isGravityAffected)
			applyForce(new Vector(0, - Config.GRAVITY_ACCELERATION * mass));
		applyXForceToSpeed(getMass() * Config.GRAVITY_ACCELERATION * Config.ADHESION, groundSpeed + walkSpeed);
		
		momentum = momentum.add(collectedForce.multiply(elapsedTime));
		collectedForce = Vector.getZero();
		
		if (!momentum.isZero()) {
			float oldX = getPosition().getX();
			float oldY = getPosition().getY();
			Vector delta = getSpeed().multiply(elapsedTime);
			// Must handle x and y move separate, otherwise there are strange effects
			Vector targetPosition = getPosition().add(delta.changeY(0));
			tryMoveTo(targetPosition);
			if (getPosition().getX() == oldX && isSolid)
				momentum = momentum.changeX(0);

			targetPosition = getPosition().add(delta.changeX(0));
			tryMoveTo(targetPosition);
			if (getPosition().getY() == oldY && isSolid)
				momentum = momentum.changeY(0);
		}
	}
	
	public boolean canMoveTo(Vector targetPosition) {
		Rect source = bounds.add(getPosition());
		Rect target = bounds.add(targetPosition);
		RectPath path = new RectPath(source.getMinVector(), target.getMinVector(), bounds.getSize());
		for (Component component : getWorld().getComponents()) {
			if ((component != this) && !component.isToRemove()
				&& (component instanceof Body) && ((Body)component).isSolid())
			{
				Body body = (Body)component;
				Rect obstacle = body.getOuterBounds();
				// Ignore Bodies already colliding with this body (otherwise this body would be stuck)
				if (!obstacle.overlaps(source)) {
					// test collision and update target if collides
					if (path.overlaps(obstacle))
						return false;
				}
			}
		}
		return true;
	}
	
	public Vector getImpactOnMove(Vector targetPosition) {
		return getImpactOnMove(targetPosition, false);
	}
	
	private Vector getImpactOnMove(Vector targetPosition, boolean calledOnMove) {
		groundSpeed = 0; 
		
		Rect source = bounds.add(getPosition());
		Rect target = bounds.add(targetPosition);
		RectPath path = new RectPath(source.getMinVector(), target.getMinVector(), bounds.getSize());
		float x = target.getLeft();
		float y = target.getBottom();
		Direction direction;
		if (targetPosition.getX() > getPosition().getX())
			direction = Direction.RIGHT;
		else if (targetPosition.getX() < getPosition().getX())
			direction = Direction.LEFT;
		else if (targetPosition.getY() > getPosition().getY())
			direction = Direction.UP;
		else if (targetPosition.getY() < getPosition().getY())
			direction = Direction.DOWN;
		else
			direction = Direction.NONE;
		
		for (Component component : getWorld().getComponents()) {
			if ((component instanceof Body) && !component.isToRemove() && (component != this)) {
				Body body = (Body)component;
				Rect obstacle = body.getOuterBounds();
				// Ignore Bodies already colliding with this body (otherwise this body would be stuck)
				// disabled because allowed collision on rounding errors
				//if (!obstacle.overlaps(source)) {
					// test collision and update target if collides
					float lastX = x;
					float lastY = y;
					boolean collided = false;
					if (path.overlaps(obstacle)) {
						if (body.isSolid) {
							switch (direction) {
							case RIGHT:
								// If moving to the right: left edge as right-most point
								x = Math.max(source.getLeft(), Math.min(x, obstacle.getLeft() - bounds.getWidth()));
								collided = x != lastX;
								break;
								
							case LEFT:
								// If moving to the left: right edge as left-most point
								x = Math.min(source.getLeft(), Math.max(x, obstacle.getRight()));
								collided = x != lastX;
								break;
							
							case UP:
								// If moving up: lower edge as top-most point
								y = Math.max(source.getBottom(), Math.min(y, obstacle.getBottom() - bounds.getHeight()));
								collided = y != lastY;
								break;
								
							case DOWN:
								// If moving down: upper edge as bottom-most point
								y = Math.min(source.getBottom(), Math.max(y, obstacle.getTop()));
								collided = y != lastY;
								break;
							}
						}

						if (calledOnMove && collided) {
							onCollision(body, direction, true);
							body.onCollision(this, direction, false);
						}
					}
				//}
			}
		}
		
		return new Vector(x, y).subtract(bounds.getMinVector());
	}

	public void tryMoveTo(Vector targetPosition) {
		Vector impact = getImpactOnMove(targetPosition, true);
		if (isSolid)
			setPosition(impact);
		else
			setPosition(targetPosition);
	}
	
	public boolean standsOnGround() {
		return !canMoveTo(getPosition().add(new Vector(0, -0.001f)));
	}
	
	protected void onCollision(Body other, Direction direction, boolean isCauser) {
		/*if (isCauser && direction == Direction.DOWN && other.isSolid && isGravityAffected) {
			groundSpeed = other.getSpeed().getX();
		}*/

		//applyXForceToSpeed(groundSpeed + walkSpeed, getMass() * Config.GRAVITY_ACCELERATION * Config.ADHESION);
		
		if (!isCauser && isShiftable && other.isSolid) {
			// Distribute momentum
			Vector totalMomentum = momentum.add(other.momentum);
			float totalMass = mass + other.mass;
			Vector resultingSpeed = totalMomentum.divide(totalMass);
			
			switch (direction) {
			case LEFT:
			case RIGHT: 
				applyXForceToSpeed(other.momentum.getX() / getWorld().getFrameTime(), resultingSpeed.getX());
				other.applyXForceToSpeed(momentum.getX() / getWorld().getFrameTime(), resultingSpeed.getX());
				break;
			case UP:
			case DOWN:
				applyYForceToSpeed(other.momentum.getX() / getWorld().getFrameTime(), resultingSpeed.getX());
				other.applyYForceToSpeed(momentum.getX() / getWorld().getFrameTime(), resultingSpeed.getX());
				break;
			}
			
			//momentum = totalMomentum.multiply(mass / totalMass);
			//other.momentum = totalMomentum.multiply(other.mass / totalMass);
			
			/*if (direction == Direction.UP || direction == Direction.DOWN) {
				applyXForceToSpeed(other.momentum.getX() / getWorld().getFrameTime(), other.getSpeed().getX());
			}
			if (direction == Direction.RIGHT || direction == Direction.LEFT)
				applyXForceToSpeed(other.momentum.getY() / getWorld().getFrameTime(), other.getSpeed().getY());*/
		}
	}
}

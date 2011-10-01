package de.yogularm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.yogularm.drawing.CombinedDrawable;
import de.yogularm.drawing.Drawable;
import de.yogularm.drawing.RenderTransformation;
import de.yogularm.drawing.SimpleArrow;

public class Body extends Component {
	private Rect bounds;
	private float mass = 1.0f;
	private Vector momentum;
	private Vector actualSpeed; // cached
	private Vector collectedForce;
	private Vector totalForce;
	private boolean isGravityAffected = false;
	private boolean isSolid = true;
	private boolean isShiftable = false;
	private boolean isClimbable = false;
	private float walkSpeed = 0;
	private float climbSpeed = 0;
	private boolean hasWalkSpeed = false;
	private List<ForceToSpeed> forceToSpeed = new ArrayList<ForceToSpeed>();
	private Boolean standsOnGround; // cache
	private boolean canClimb;
	private float massCache = mass;
	private boolean walkSpeedApplied = false;
	private Vector shiftSpeed;
	private Vector actualShiftSpeed;
	private List<Vector> collectedForces; // debug
	private List<Vector> forces; // debug

	private static final float UNSHIFTABLE_MASS = 1e15f;

	private class ForceToSpeed {
		private float speed;
		private float force;
		private Axis axis;

		public ForceToSpeed(float force, float speed, Axis axis) {
			this.speed = speed;
			this.force = force;
			this.axis = axis;
		}

		/**
		 * Gets the force that should be applied, depending on the body's current
		 * speed
		 */
		public Vector getForce() {
			// Direction dir = axis ? Direction.DOWN : Direction.LEFT;
			float speedDiff = speed - getSpeed().getComponent(axis);
			if (speedDiff != 0) {
				float direction = speedDiff / Math.abs(speedDiff);
				float theForce = Math.min(Math.abs(force), Math.abs(speedDiff * mass) / getWorld().getFrameTime());
				return Vector.getZero().changeComponent(axis, direction * theForce);
			}
			return Vector.getZero();
		}
	}

	public Body(World world) {
		super(world);
		bounds = new Rect(0, 0, 1, 1);
		collectedForce = Vector.getZero();
		totalForce = Vector.getZero();
		momentum = Vector.getZero();
		actualSpeed = getSpeed();
		setIsShiftable(false);
		shiftSpeed = Vector.getZero();
		actualShiftSpeed = shiftSpeed;
		if (Config.DISPLAY_FORCES) {
			forces = new ArrayList<Vector>();
			collectedForces = new ArrayList<Vector>();
		}
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
		return mass < UNSHIFTABLE_MASS;// isShiftable;
	}

	public void setIsShiftable(boolean value) {
		if (value)
			mass = massCache;
		else
			mass = UNSHIFTABLE_MASS;
		// isShiftable = value;
	}

	public boolean isClimbable() {
		return isClimbable;
	}

	public void setIsClimbable(boolean value) {
		isClimbable = value;
	}

	public boolean canClimb() {
		return canClimb;
	}

	public float getMass() {
		return mass;
	}

	public void setMass(float mass) {
		if (!isShiftable)
			this.mass = mass;
		massCache = mass;
	}

	public void setSpeed(Vector speed) {
		if (speed == null)
			throw new NullPointerException("speed is null");
		this.momentum = speed.multiply(mass);
	}

	public Vector getSpeed() {
		return momentum.divide(mass);
	}

	/**
	 * Gets the speed the body effectifely has made in the last frame
	 * 
	 * @return the actual speed
	 */
	public Vector getActualSpeed() {
		return actualSpeed;
	}

	/**
	 * Gets the speed with that other bodies shift this body (cached from last frame)
	 * 
	 * @return the shift speed
	 */
	public Vector getShiftSpeed() {
		return actualShiftSpeed;
	}

	public void applyWalkSpeed(float speed) {
		walkSpeed += speed;
		hasWalkSpeed = true;
	}

	public void applyClimbSpeed(float speed) {
		climbSpeed += speed;
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
		if (collectedForces != null)
			collectedForces.add(force);
	}

	public void applyForceToSpeed(Vector force, Vector speed) {
		applyXForceToSpeed(force.getX(), speed.getX());
		applyYForceToSpeed(force.getY(), speed.getY());
	}

	public void applyForceToSpeed(float force, float speed, Axis axis) {
		switch (axis) {
		case HORIZONTAL:
			applyXForceToSpeed(force, speed);
			break;
		case VERTICAL:
			applyYForceToSpeed(force, speed);
			break;
		}
	}

	public void applyXForceToSpeed(float force, float speed) {
		forceToSpeed.add(new ForceToSpeed(force, speed, Axis.HORIZONTAL));
	}

	public void applyYForceToSpeed(float force, float speed) {
		forceToSpeed.add(new ForceToSpeed(force, speed, Axis.VERTICAL));
	}

	public void update(float elapsedTime) {
		super.update(elapsedTime);

		applyForces(elapsedTime);
		move(elapsedTime);
		checkClimbing();
	}

	public Drawable getDrawable() {
		Drawable drawable = super.getDrawable();

		if (forces != null) {
			CombinedDrawable combination = new CombinedDrawable();
			combination.add(drawable);
			combination.addAll(getForceDrawables());
			return combination;
		}
		return drawable;
	}

	private Collection<Drawable> getForceDrawables() {
		Vector center = getBounds().getCenter();
		List<Drawable> drawables = new ArrayList<Drawable>();
		for (Vector force : forces) {
			if (force.getLength() > 0) {
				drawables.add(new RenderTransformation(new SimpleArrow(),
					center, new Vector(force.getLength() / mass, 1), force.getAngleToXAxis()));
			}
		}
		return drawables;
	}

	private void applyForces(float elapsedTime) {
		if (isGravityAffected)
			applyForce(new Vector(0, -Config.GRAVITY_ACCELERATION * mass));

		if (canClimb)
			applyYForceToSpeed(Config.CLIMB_ACCELERATION * getMass(), climbSpeed);
		climbSpeed = 0;

		momentum = momentum.add(collectedForce.multiply(elapsedTime));
		for (ForceToSpeed info : forceToSpeed) {
			Vector force = info.getForce();
			applyForce(force);
			momentum = momentum.add(force.multiply(getWorld().getFrameTime()));
		}
		forceToSpeed.clear();
		totalForce = collectedForce;
		collectedForce = Vector.getZero();
		actualShiftSpeed = shiftSpeed;
		shiftSpeed = Vector.getZero();
		if (collectedForces != null) {
			forces = collectedForces;
			collectedForces = new ArrayList<Vector>();
		}
	}

	private void move(float elapsedTime) {
		Vector lastPosition = getPosition();
		if (!momentum.isZero()) {
			Vector delta = getSpeed().multiply(elapsedTime);
			// Must handle x and y move separate, otherwise there are strange effects
			tryMoveTo(getPosition().add(delta.changeY(0)), Axis.HORIZONTAL);
			tryMoveTo(getPosition().add(delta.changeX(0)), Axis.VERTICAL);

		}
		actualSpeed = getPosition().subtract(lastPosition).divide(elapsedTime);

		walkSpeed = 0;
		hasWalkSpeed = false;
		standsOnGround = null;
	}

	private void checkClimbing() {
		Iterable<Body> overlaps = getWorld().getOverlappingBodies(getOuterBounds());
		for (Body body : overlaps) {
			if (body.isClimbable) {
				canClimb = true;
				return;
			}
		}
		canClimb = false;
	}

	public boolean canMoveTo(Vector targetPosition) {
		Rect source = bounds.add(getPosition());
		Rect target = bounds.add(targetPosition);
		Rect path = new Rect(Math.min(source.getLeft(), target.getLeft()),
			Math.min(source.getBottom(), target.getBottom()), Math.max(source.getRight(), target.getRight()), Math.max(
				source.getTop(), target.getTop()));
		for (Component component : getWorld().getComponents()) {
			if ((component != this) && !component.isToRemove() && (component instanceof Body) && ((Body) component).isSolid()) {
				Body body = (Body) component;
				Rect obstacle = body.getOuterBounds();
				// Ignore Bodies already colliding with this body (otherwise this body
				// would be stuck)
				if (!obstacle.overlaps(source)) {
					// test collision and update target if collides
					if (path.overlaps(obstacle))
						return false;
				} else
					System.out.println("stuck!");
			}
		}
		return true;
	}

	public Vector getImpactOnMove(Vector targetPosition) {
		return getImpactOnMove(targetPosition, false, Axis.NONE);
	}

	private Vector getImpactOnMove(Vector targetPosition, boolean calledOnMove, Axis axis) {
		Rect source = bounds.add(getPosition());
		Rect target = bounds.add(targetPosition);
		Rect path = new Rect(Math.min(source.getLeft(), target.getLeft()),
			Math.min(source.getBottom(), target.getBottom()), Math.max(source.getRight(), target.getRight()), Math.max(
				source.getTop(), target.getTop()));

		float x = target.getLeft();
		float y = target.getBottom();
		Vector delta = targetPosition.subtract(getPosition());
		Direction direction = delta.getDirection();

		List<Body> collidedBodies = null;

		for (Component component : getWorld().getComponents()) {
			if ((component instanceof Body) && !component.isToRemove() && (component != this)) {
				Body body = (Body) component;
				Rect obstacle = body.getOuterBounds();
				// Ignore Bodies already colliding with this body (otherwise this body
				// would be stuck)
				// disabled because allowed collision on rounding errors
				if (!obstacle.overlaps(source)) {
					// test collision and update target if collides
					float lastX = x;
					float lastY = y;
					boolean collided = false;
					if (path.overlaps(obstacle)) {
						if (body.isSolid) {
							switch (direction) {
							case RIGHT:
								// If moving to the right: left edge as right-most point
								x = Math.min(x, obstacle.getLeft() - bounds.getWidth());
								collided = x != lastX;
								break;

							case LEFT:
								// If moving to the left: right edge as left-most point
								x = Math.max(x, obstacle.getRight());
								collided = x != lastX;
								break;

							case UP:
								// If moving up: lower edge as top-most point
								y = Math.min(y, obstacle.getBottom() - bounds.getHeight());
								collided = y != lastY;
								break;

							case DOWN:
								// If moving down: upper edge as bottom-most point
								y = Math.max(y, obstacle.getTop());
								collided = y != lastY;
								break;
							}
						}

						if (calledOnMove && collided && isSolid && body.isSolid) {
							// Parallel force (when collided in y direction, apply y force)
							// (momentum-based, delayed)
							/*
							 * if (collidedBodies == null)
							 * collidedBodies = new ArrayList<Body>();
							 * collidedBodies.add(body);
							 */

							// parallel force (momentum-based, instantly applied)
							float totalMomentum = momentum.add(body.momentum).getComponent(axis);
							float totalMass = mass + body.mass;

							for (Body b : new Body[] { this, body }) {
								float minMomentum = totalMomentum * (b.mass / totalMass);
								float currentMomentum = b.momentum.getComponent(axis);
								// if this one is moving upward, reduce momentum of this, but
								// enlarge the one of the other.
								boolean enlarge = delta.getComponent(axis) < 0 || b != this;
								if (enlarge)
									currentMomentum = Math.max(currentMomentum, minMomentum);
								else
									currentMomentum = Math.min(currentMomentum, minMomentum);
								b.momentum = b.momentum.changeComponent(axis, currentMomentum);
							}

							shiftSpeed = shiftSpeed.changeComponent(axis, body.getActualSpeed().getComponent(axis));
							body.shiftSpeed = body.shiftSpeed.changeComponent(axis, getActualSpeed().getComponent(axis));

							// Orthogonal force (when pressed to ground, apply x force
							/* if (axis == Axis.VERTICAL) { */
							Vector force = totalForce.subtract(body.totalForce).multiply(Config.ADHESION);
							float speed = (body.momentum.getX() + momentum.getX()) / (mass + body.mass);
							applyGroundSpeed(force.getY(), speed);
							body.applyGroundSpeed(force.getY(), speed);
							// }
						}

						onCollision(body, direction, true);
						body.onCollision(this, direction, false);
					}
				}
			}
		}

		// workaround for having grip in the air
		if (hasWalkSpeed && axis == Axis.VERTICAL) {
			if (!walkSpeedApplied) {
				float adhesion = canClimb ? Config.ADHESION : Config.AIR_ADHESION;
				applyXForceToSpeed(mass * Config.GRAVITY_ACCELERATION * adhesion, walkSpeed);
			}
			walkSpeedApplied = false;
		}

		// Parallel force (when collided in y direction, apply y force)
		/*
		 * if (calledOnMove && collidedBodies != null) {
		 * collidedBodies.add(this);
		 * float totalMomentum = 0;
		 * float totalMass = 0;
		 * for (Body body : collidedBodies) {
		 * totalMass += body.mass;
		 * totalMomentum += body.momentum.getComponent(axis);
		 * }
		 * 
		 * if (totalMass > 0) {
		 * for (Body body : collidedBodies) {
		 * // braces to avoid +INF
		 * float minMomentum = totalMomentum * (body.mass / totalMass);
		 * 
		 * // If body
		 * //float currentMomentum = body.momentum.getComponent(axis);
		 * //if (minMomentum) currentMomentum = Math.min(minMomentum,
		 * // * currentMomentum); else currentMomentum = Math.max(minMomentum,
		 * // * currentMomentum);
		 * currentMomentum = minMomentum;
		 * body.momentum = body.momentum.changeComponent(axis, currentMomentum);
		 * }
		 * }
		 * }
		 */

		return new Vector(x, y).subtract(bounds.getMinVector());
	}

	private void applyGroundSpeed(float force, float speed) {
		if (hasWalkSpeed) {
			walkSpeedApplied = true;
			speed += walkSpeed;
		}
		applyXForceToSpeed(Math.abs(force), speed);
	}

	public void tryMoveTo(Vector targetPosition, Axis axis) {
		Vector impact = getImpactOnMove(targetPosition, true, axis);
		if (isSolid)
			setPosition(impact);
		else
			setPosition(targetPosition);
	}

	public boolean standsOnGround() {
		// if (standsOnGround == null)
		standsOnGround = !canMoveTo(getPosition().add(new Vector(0, -Config.ON_GROUND_EPSILON)));
		return standsOnGround;
	}

	protected void onCollision(Body other, Direction direction, boolean isCauser) {

	}
}

package de.yogularm.components.general;

import de.yogularm.Res;
import de.yogularm.components.Component;
import de.yogularm.components.Bot;
import de.yogularm.components.ComponentCollection;
import de.yogularm.drawing.AnimatedImage;
import de.yogularm.drawing.RenderTransformation;
import de.yogularm.geometry.Direction;
import de.yogularm.geometry.Rect;
import de.yogularm.geometry.Vector;

public class Chicken extends Bot {
	private AnimatedImage walkingAnimation;
	private AnimatedImage flutteringAnimation;
	private RenderTransformation transformation;
	private float explodeRemainingTime;
	
	public Chicken(ComponentCollection collection) {
		super(collection);
		walkingAnimation = Res.animations.chickenWalking.getInstance();
		flutteringAnimation = Res.animations.chickenFluttering.getInstance();
		transformation = new RenderTransformation(walkingAnimation);
		setDrawable(transformation);
		setBounds(new Rect(0.124296875f, 0.0464296875f, 0.8728359375f, 0.9109765625f));
		setMass(20);
	}
	
	public void update(float elapsedTime) {
		super.update(elapsedTime);
		
		if (isDead()) {
			explodeRemainingTime -= elapsedTime;
			if (explodeRemainingTime <= 0) {
				remove();
			}
		} else {
			if (getHeightOverGround() > 0.1f)
				transformation.setDrawable(flutteringAnimation);
			else
				transformation.setDrawable(walkingAnimation);
			
			if (getDirection() < 0)
				transformation.setIsVerticallyMirrored(false);
			else if(getDirection() > 0)
				transformation.setIsVerticallyMirrored(true);
		}
	}

	protected void onCollision(Component other, Direction direction, boolean isCauser) {
		super.onCollision(other, direction, isCauser);
		if (other instanceof Arrow)
			dropHeart();
	}
	
	protected void onDie() {
		// don't remove immediatly
		setAnimation(Res.animations.chickenExploding);
		if (explodeRemainingTime == 0)
			explodeRemainingTime = Res.animations.chickenExploding.getLength();
		setIsSolid(false);
		setIsShiftable(false);
		setIsGravityAffected(false);
		setMomentum(Vector.getZero());
	}
	
	private void dropHeart() {
		Heart heart = new Heart(getCollection());
		heart.setPosition(getPosition());
		getCollection().add(heart);
	}
}

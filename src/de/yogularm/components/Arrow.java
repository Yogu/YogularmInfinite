package de.yogularm.components;

import de.yogularm.Body;
import de.yogularm.ComponentCollection;
import de.yogularm.Direction;
import de.yogularm.Entity;
import de.yogularm.Item;
import de.yogularm.Rect;
import de.yogularm.Res;
import de.yogularm.Vector;
import de.yogularm.drawing.RenderTransformation;

public class Arrow extends Entity {
	private Body sender;
	private RenderTransformation transformation;
	
	public Arrow(ComponentCollection collection) {
		super(collection);
		transformation = new RenderTransformation(Res.images.arrow);
		transformation.setRotationCenter(new Vector(0.5f, 0.5f));
		setDrawable(transformation);
		setBounds(new Rect(0.3f, 0.3f, 0.7f, 0.7f));
		setIsGravityAffected(true);
		setIsSolid(false);
	}
	
	public Arrow(ComponentCollection collection, Body sender) {
		this(collection);
		this.sender = sender;
	}
	
	public void update(float elapsedTime) {
		super.update(elapsedTime);	
		float angle = getSpeed().getAngleToXAxis();
		transformation.setAngle(angle);
	}

	protected void onCollision(Body other, Direction direction, boolean isCauser) {
		super.onCollision(other, direction, isCauser);
		
		if ((other.isSolid() || other instanceof Item) && other != sender)
			remove();
	}
}

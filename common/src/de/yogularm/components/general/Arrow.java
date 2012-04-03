package de.yogularm.components.general;

import de.yogularm.Res;
import de.yogularm.components.Body;
import de.yogularm.components.ComponentCollection;
import de.yogularm.components.Entity;
import de.yogularm.components.Item;
import de.yogularm.drawing.RenderTransformation;
import de.yogularm.geometry.Direction;
import de.yogularm.geometry.Rect;
import de.yogularm.geometry.Vector;

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

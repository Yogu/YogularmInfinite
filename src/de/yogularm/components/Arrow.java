package de.yogularm.components;

import javax.media.opengl.GL2;

import de.yogularm.Body;
import de.yogularm.Direction;
import de.yogularm.Entity;
import de.yogularm.Image;
import de.yogularm.Rect;
import de.yogularm.Res;
import de.yogularm.World;

public class Arrow extends Entity {
	private Body sender;
	
	public Arrow(World world) {
		super(world);
		setImage(Res.images.arrow);
		setBounds(new Rect(0.3f, 0.3f, 0.7f, 0.7f));
		setIsGravityAffected(true);
		setIsSolid(false);
	}
	
	public Arrow(World world, Body sender) {
		this(world);
		this.sender = sender;
	}
	
	public void draw(GL2 gl) {
		float angle = getSpeed().getAngleToXAxis();
		getImage().setAngle(angle);
		super.draw(gl);
	}

	protected void onCollision(Body other, Direction direction, boolean isCauser) {
		super.onCollision(other, direction, isCauser);
		
		if (other.isSolid() && other != sender)
			remove();
	}
}

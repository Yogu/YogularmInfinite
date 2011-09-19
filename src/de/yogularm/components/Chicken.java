package de.yogularm.components;

import javax.media.opengl.GL2;

import de.yogularm.Body;
import de.yogularm.Bot;
import de.yogularm.Direction;
import de.yogularm.Image;
import de.yogularm.Rect;
import de.yogularm.Res;
import de.yogularm.World;

public class Chicken extends Bot {
	public Chicken(World world) {
		super(world);
		setImage(Res.images.chicken);
		setBounds(new Rect(0.109375f, 0.06075f, 0.890625f, 0.9392421875f));
		setMass(20);
	}
	
	public void draw(GL2 gl) {
		if (getDirection() < 0)
			getImage().setIsMirrored(false);
		else if(getDirection() > 0)
			getImage().setIsMirrored(true);
		super.draw(gl);
	}

	protected void onCollision(Body other, Direction direction, boolean isCauser) {
		super.onCollision(other, direction, isCauser);
		if (other instanceof Arrow)
			dropHeart();
	}
	
	protected void onDie() {
		super.onDie();
	}
	
	private void dropHeart() {
		Heart heart = new Heart(getWorld());
		heart.setPosition(getPosition());
		getWorld().addComponent(heart);
	}
}

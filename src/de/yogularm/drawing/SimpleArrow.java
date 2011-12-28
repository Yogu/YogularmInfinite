package de.yogularm.drawing;

import de.yogularm.geometry.Vector;

public class SimpleArrow implements Drawable {
	private static final float HEAD_SIZE = 0.2f;
	
	public float getLength(float length) {
		return length;
	}

	public void draw(RenderContext context) {
		context.drawLines(new Vector[] {
			new Vector(0, 0),
			new Vector(1, 0),
			new Vector(1 - HEAD_SIZE, -HEAD_SIZE),
			new Vector(1, 0),
			new Vector(1 - HEAD_SIZE, HEAD_SIZE)
		}, 2, true);
	}

	public void update(float elapsedTime) { }
	
	public static void render(RenderContext context, Vector offset, float length, float angle) {
		SimpleArrow arrow = new SimpleArrow();
		RenderTransformation transformation = new RenderTransformation(arrow);
		transformation.setOffset(offset);
		transformation.setAngle(angle);
		transformation.setScale(new Vector(length, 1));
		transformation.draw(context);
	}
}

package de.yogularm.drawing.debug;

import de.yogularm.drawing.Drawable;
import de.yogularm.drawing.RenderContext;
import de.yogularm.geometry.Parabola;
import de.yogularm.geometry.Vector;

public class ParabolaDrawable implements Drawable {
	private Parabola parabola;
	private float minX;
	private float maxX;
	private float precision;
	
	/**
	 * Creates a drawable that renders a parabola
	 * 
	 * @param parabola The parabola to render
 * @param minX The minimum x value
 * @param maxX The maximum x value
	 * @param precision The count of line segments per delta-x=1 step
	 */
	public ParabolaDrawable(Parabola parabola, float minX, float maxX, float precision) {
		this.parabola = parabola;
		this.minX = minX;
		this.maxX = maxX;
		this.precision = precision;
	}
	
	@Override
  public void draw(RenderContext context) {
		int count = (int)((maxX - minX) * precision) + 1;
	  Vector[] vertices = new Vector[count];
	  for (int i = 0; i < count; i++) {
	  	float x = (float)i / precision + minX;
	  	vertices[i] = new Vector(x, parabola.getY(x));
	  }

		context.drawLines(vertices, 2, true);
  }

	@Override
  public void update(float elapsedTime) {
	  // TODO Auto-generated method stub
	  
  }

}

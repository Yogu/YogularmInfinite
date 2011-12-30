package de.yogularm.components.debug;

import de.yogularm.components.Component;
import de.yogularm.components.ComponentCollection;
import de.yogularm.drawing.Renderable;
import de.yogularm.drawing.debug.ParabolaDrawable;
import de.yogularm.geometry.Parabola;

/**
 * Creates a component that shows a parabola
 * 
 * @param components
 * @param parabola The parabola to render
 * @param minX The minimum x value
 * @param maxX The maximum x value
 * @param precision The count of line segments per delta-x=1 step
 */
public class ParabolaComponent extends Component implements Renderable {
	public ParabolaComponent(ComponentCollection components, Parabola parabola, float minX,
		float maxX, float precision)
	{
	  super(components);
	  setDrawable(new ParabolaDrawable(parabola, minX, maxX, precision));
  }
}

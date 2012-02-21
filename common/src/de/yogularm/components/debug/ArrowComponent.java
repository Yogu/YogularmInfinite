package de.yogularm.components.debug;

import de.yogularm.components.Component;
import de.yogularm.components.ComponentCollection;
import de.yogularm.drawing.Renderable;
import de.yogularm.drawing.debug.SimpleArrow;
import de.yogularm.geometry.Vector;

public class ArrowComponent extends Component implements Renderable {
	/**
	 * Creates a component that shows a simple arrow
	 * 
	 * @param components
	 * @param length the arrow's length
	 * @param angle the angle against positive x axis
	 */
	public ArrowComponent(ComponentCollection components, float length, float angle)
	{
	  super(components);
	  setDrawable(SimpleArrow.getTransformed(Vector.ZERO, length, angle));
  }
}

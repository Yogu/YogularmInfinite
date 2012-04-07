package de.yogularm.geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Describes a rectangle following the graph of a numeric function
 * @author jan
 *
 */
public class RectTrace {
	private NumericFunction baseFunction;
	private Rect rectDimensions;
	private float startX;
	private float targetX;
	
	public RectTrace(NumericFunction baseFunction, Rect rectDimensions, float startX, float targetX) {
		this.baseFunction = baseFunction;
		this.rectDimensions = rectDimensions;
		this.startX = startX;
		this.targetX = targetX;
	}
	
	/**
	 * Gets all grid cells that collide with this trace
	 * @return a collection of grid cells
	 */
	public Collection<Point> getCollidingCells() {
		List<Point> list = new ArrayList<Point>();
		IntegerRange xRange = getColumnRange();
		for (int x = xRange.getMin(); x <= xRange.getMax(); x++) {
			IntegerRange yRange = getColumnYRange(x);
			for (int y = yRange.getMin(); y <= yRange.getMax(); y++)
				list.add(new Point(x, y));
		}
		return list;
	}
	
	/**
	 * Gets the range of grid positions this trace does overlap
	 * 
	 * @return the range of grid columns
	 */
	public IntegerRange getColumnRange() {
		int minX = (int)Math.floor(Math.min(startX, targetX) + rectDimensions.getLeft());
		int maxX = (int)Math.ceil(Math.max(startX, targetX) + rectDimensions.getRight()) - 1; // grid positions
		return new IntegerRange(minX, maxX);
	}
	
	/**
	 * Gets the range of grid positions (vertically) the trace overlaps in the given grid column  
	 * 
	 * @param gridX the grid column
	 * @return a range of grid rows
	 */
	public IntegerRange getColumnYRange(int gridX) {
		float totalLeftX = Math.min(startX, targetX) + rectDimensions.getLeft();
		float totalRightX = Math.max(startX, targetX) + rectDimensions.getRight() - 1; // grid positions
		
		int leftX = gridX;
		int rightX = gridX + 1;
		
		// left cell edge touches right rect edge
		float funcX1 = Math.max(totalLeftX, leftX - rectDimensions.getRight());
		float funcX2 = Math.min(totalRightX, rightX - rectDimensions.getLeft());
		float funcMinY = baseFunction.getMinY(funcX1, funcX2);
		float funcMaxY = baseFunction.getMaxY(funcX1, funcX2);
		int minY = (int)Math.floor(funcMinY + rectDimensions.getBottom());
		int maxY = (int)Math.ceil(funcMaxY + rectDimensions.getTop()) - 1; // grid positions
		return new IntegerRange(minY, maxY);
	}
}

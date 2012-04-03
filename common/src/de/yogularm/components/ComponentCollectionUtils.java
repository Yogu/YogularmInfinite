package de.yogularm.components;

import java.util.ArrayList;
import java.util.List;

import de.yogularm.geometry.Rect;
import de.yogularm.geometry.Vector;

public class ComponentCollectionUtils {
	/**
	 * Gets the components that are - if snapped into a 1x1 grid - at the given position
	 * 
	 * @param position The vector which is rounded to get the position
	 * @param components The component collection to use
	 * @return The components at the grid position
	 */
	public static Iterable<Component> getComponentsAt(ComponentCollection components, Vector position) {
		List<Component> list = new ArrayList<Component>();
		Vector rounded = position.round();
		for (Component component : components.getComponentsAround(position)) {
			if (component.getPosition().round().equals(rounded))
				list.add(component);
		}
		return list;
	}

	/**
	 * Gets the blocks that are - if snapped into a 1x1 grid - at the given position
	 * 
	 * @param position The vector which is rounded to get the position
	 * @param components The component collection to use
	 * @return The blocks at the grid position
	 */
	public static Component getBlockAt(ComponentCollection components, Vector position) {
		for (Component component : getComponentsAt(components, position))
			if (component instanceof Component)
				return (Component) component;
		return null;
	}

	/**
	 * Checks if there is a solid body at the specified grid position
	 * 
	 * @param position The position which is rounded to get the grid position
	 * @return true, if there is a solid body at the given position, false otherwise
	 */
	public static boolean hasSolidAt(ComponentCollection components, Vector position) {
		for (Component component : getComponentsAt(components, position))
			if (component instanceof Component && ((Component) component).isSolid())
				return true;
		return false;
	}

	/**
	 * Gets the bodies whose bounds overlap the given rectangle
	 * 
	 * @param range The rectangle
	 * @param components The component collection to use
	 * @return The bodies overlapping the rectangle
	 */
	public static Iterable<Component> getOverlappingBodies(ComponentCollection components, Rect range) {
		List<Component> list = new ArrayList<Component>();
		for (Component component : components.getComponentsAround(range)) {
			Component body = (Component)component;
			if (body.getOuterBounds().overlaps(range))
				list.add(body);
		}
		return list;
	}

	/**
	 * Gets the top-most block that is below the given grid position
	 * @param position The vector that is rounded to get the grid position
	 * @param components The component collection to use
	 * @return The block below, or null, if there is no block below
	 */
	public static Component getBlockBelow(ComponentCollection components, Vector position) {
		// This rectangle reaches from the position downward 
		Rect range = new Rect(position.changeY(Float.NEGATIVE_INFINITY), position);
		
		Vector rounded = position.round();
		for (Component component : components.getComponentsAround(range)) {
			Component block = (Component) component;
			if ((Math.round(component.getPosition().getX()) == rounded
					.getX())
					&& (block.getPosition().getY() <= position.getY()))
				return block;
		}
		return null;
	}
	
	/**
	 * Checks if there is a block below the given grid position
	 * @param position The vector that is rounded to get the grid position
	 * @param components The component collection to use
	 * @return true, if there is a block below, false otherwise
	 */
	public static boolean hasBlockBelow(ComponentCollection components, Vector position) {
		return getBlockBelow(components, position) != null;
	}
}

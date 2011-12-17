package de.yogularm;

import java.util.Collection;

public interface ComponentCollection {
	/**
	 * Gets all components in and nearly around the specified range.
	 * 
	 * The result collection can contain many components that are outside the range, but must include
	 * all the ones inside.
	 * 
	 * @param range The range
	 * @return an array of components
	 */
	public Collection<Component> getComponentsAround(Rect range);
	/**
	 * Gets all components around the specified position.
	 * 
	 * @param position The position
	 * @return an array of components
	 */
	public Collection<Component> getComponentsAround(Vector position);
	
	/**
	 * Checks whether the component is contained by this collection
	 * @param component The component to check
	 * @return true, if the component is contained by this collection, false otherwise
	 */
	public boolean contains(Component component);
	
	/**
	 * Adds the specified component, if it is not contained yet
	 * @param component The component to add
	 */
	public void add(Component component);
	
	/**
	 * Removes a component from the collection, if it is contained.
	 * @param component The component to remove
	 */
	public void remove(Component component);
	
	/**
	 * Gets the total count of all components
	 * @return
	 */
	public int getCount();
}

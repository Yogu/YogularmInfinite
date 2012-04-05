package de.yogularm.components;

import de.yogularm.geometry.Rect;

public abstract class AbstractWorld implements World {
	@Override
	public int update(float elapsedTime, Rect actionRange) {
		int updateCount = 0;
		ComponentCollection components = getComponents();
		
		for (Component component : components.getComponentsAround(actionRange)) {
			if (!component.isToRemove()) {
				component.update(elapsedTime);
				updateCount++;
			}
			if (component.isToRemove())
				components.remove(component);
		}
		
		return updateCount;
	}
}

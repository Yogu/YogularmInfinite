package de.yogularm.components;

import de.yogularm.geometry.Point;

public interface MultiPlayerWorldListener {
	void componentChanged(MultiPlayerWorld world, Component component, Point sector);
}

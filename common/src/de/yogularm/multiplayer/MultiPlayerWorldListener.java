package de.yogularm.multiplayer;

import de.yogularm.components.Component;
import de.yogularm.geometry.Point;

public interface MultiPlayerWorldListener {
	void componentChanged(MultiPlayerWorld world, Component component, Point sector);
	void quickChange(MultiPlayerWorld world, Component component, Point sector);
}

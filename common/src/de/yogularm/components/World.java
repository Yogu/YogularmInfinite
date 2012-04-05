package de.yogularm.components;

import de.yogularm.building.BuildingSite;
import de.yogularm.geometry.Rect;

public interface World {
	ComponentCollection getComponents();
	int update(float elapsedTime, Rect actionRange);
	Player getPlayer();
	BuildingSite getBuildingSite();
}

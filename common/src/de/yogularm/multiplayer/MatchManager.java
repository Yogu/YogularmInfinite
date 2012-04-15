package de.yogularm.multiplayer;

import java.util.Collection;

import de.yogularm.components.Component;
import de.yogularm.geometry.Point;
import de.yogularm.geometry.Vector;

public interface MatchManager {
	void setPlayerPosition(Player player, Vector newPosition, Vector newMomentum);
	Collection<Component> getComponentsOfSector(Point sector);
	de.yogularm.components.Player getPlayerComponent(Player player);

	void observeSectors(Collection<Point> sectors);
	void stopObservation(Collection<Point> sectors);
	
	void addListener(MatchListener listener);
	void removeListener(MatchListener listener);
}

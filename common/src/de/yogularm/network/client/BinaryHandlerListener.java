package de.yogularm.network.client;

import java.util.Collection;

import de.yogularm.components.Component;
import de.yogularm.geometry.Point;
import de.yogularm.geometry.Vector;

public interface BinaryHandlerListener {
	void worldInitialized(int sectorWidth, int sectorHeight);
	void playerComponentReceived(de.yogularm.components.Player player);
	void sectorReceived(Point sector, Collection<Component> components);
	void componentAdded(Component component);
	void componentRemoved(int componentID);
	void componentChanged(int componentID, Component component);
	void quickUpdate(int componentID, Vector position, Vector momentum);
}

package de.yogularm.network.client;

import java.io.IOException;

import de.yogularm.geometry.IntRect;
import de.yogularm.geometry.Vector;

public interface ActiveBinaryHandler {
	void observeSector(IntRect observationRange) throws IOException;
	void sendPlayerPosition(Vector position, Vector momentum) throws IOException;
}

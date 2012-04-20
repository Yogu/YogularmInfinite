package de.yogularm.network.client;

import java.io.IOException;

import de.yogularm.network.InputOutputStreams;

public interface ClientConnector {
	InputOutputStreams openConnection() throws IOException;
}

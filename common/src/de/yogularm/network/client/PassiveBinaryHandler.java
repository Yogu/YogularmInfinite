package de.yogularm.network.client;

import de.yogularm.network.BackgroundHandler;

public interface PassiveBinaryHandler extends BackgroundHandler {
	void addListener(BinaryHandlerListener listener);
	void removeListener(BinaryHandlerListener listener);
}

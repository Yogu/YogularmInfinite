package de.yogularm.network.client;

import de.yogularm.network.BackgroundHandler;

public interface PassiveHandler extends BackgroundHandler {
	void addListener(PassiveHandlerListener listener);
	void removeListener(PassiveHandlerListener listener);
}

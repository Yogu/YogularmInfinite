package de.yogularm.network.server.meta;

import de.yogularm.network.server.ClientData;

public interface CommandHandler {
	String handle(ClientData data, String parameter);
}

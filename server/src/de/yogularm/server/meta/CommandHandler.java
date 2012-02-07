package de.yogularm.server.meta;

import de.yogularm.server.ClientData;

public interface CommandHandler {
	String handle(ClientData data, String parameter);
}

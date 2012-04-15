package de.yogularm.network.server.meta;

import de.yogularm.network.server.ClientContext;

public interface CommandHandler {
	String handle(ClientContext data, String parameter);
}

package de.yogularm.server.network;

public interface CommandHandler {
	String handle(ClientData data, String parameter);
}

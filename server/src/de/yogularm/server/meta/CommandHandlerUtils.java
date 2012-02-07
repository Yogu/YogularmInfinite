package de.yogularm.server.meta;

import de.yogularm.network.CommunicationError;

public class CommandHandlerUtils {
	public String err(CommunicationError error) {
		return "ERR " + error;
	}
	
	public String err(CommunicationError error, String comment) {
		return "ERR " + error + " " + comment;
	}
	
	public String ok() {
		return "OK";
	}
	
	public String ok(String message) {
		return "OK " + message;
	}
}

package de.yogularm.server.network;

public abstract class AbstractCommandHandler implements CommandHandler {
	protected String err(String error) {
		return "ERR " + error;
	}
	
	protected String err(String error, String comment) {
		return "ERR " + error + " " + comment;
	}
	
	protected String ok() {
		return "OK";
	}
	
	protected String ok(String message) {
		return "OK " + message;
	}
}

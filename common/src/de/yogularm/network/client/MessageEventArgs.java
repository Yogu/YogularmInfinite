package de.yogularm.network.client;

public class MessageEventArgs {
	private String player;
	private String message;
	
	public MessageEventArgs(String player, String message) {
		this.player = player;
		this.message = message;
	}
	
	public String getPlayer() {
		return player;
	}
	
	public String getMessage() {
		return message;
	}
}

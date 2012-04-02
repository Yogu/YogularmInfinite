package de.yogularm.network;

public enum NetworkCommand {
	VERSION, HELLO, PASSIVE, RENEW, CREATE, JOIN, LEAVE, LIST_MATCHES, LIST_PLAYERS, START, PAUSE,
	RESUME, CANCEL, SAY, BINARY;
	
	public static NetworkCommand fromString(String str) {
		for (NetworkCommand command : values()) {
			if (command.name().equalsIgnoreCase(str))
				return command;
		}
		return null;
	}
}

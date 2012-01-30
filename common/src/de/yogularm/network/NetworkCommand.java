package de.yogularm.network;

public enum NetworkCommand {
	VERSION, HELLO, PASSIVE, CREATE, JOIN, LEAVE, LIST, START, PAUSE, RESUME, CANCEL, SAY;
	
	public static NetworkCommand fromString(String str) {
		for (NetworkCommand command : values()) {
			if (command.name().equalsIgnoreCase(str))
				return command;
		}
		return null;
	}
}

package de.yogularm.network;

public enum NetworkInformation {
	MESSAGE, MATCH_CREATED, MATCH_STARTED, MATCH_PAUSED, MATCH_RESUMED, MATCH_CANCELLED,
	PLAYER_JOINED_SERVER, PLAYER_JOINED_MATCH, PLAYER_LEFT_SERVER, PLAYER_LEFT_MATCH;
	
	public static NetworkInformation fromString(String str) {
		for (NetworkInformation command : values()) {
			if (command.name().equalsIgnoreCase(str))
				return command;
		}
		return null;
	}
}

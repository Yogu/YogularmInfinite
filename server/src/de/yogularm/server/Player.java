package de.yogularm.server;

public class Player {
	private String name;
	
	private static final String NAME_REGEX = "[a-zA-Z0-9_#+-]+";
	
	public Player(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return "Player " + name;
	}
	
	public static boolean isValidName(String name) {
		return name.matches(NAME_REGEX);
	}
}

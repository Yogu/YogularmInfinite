package de.yogularm.network;

public class Player {
	private String name;
	
	private static final String NAME_REGEX = "[a-zA-Z0-9_-]+";
	
	public Player(String name) {
		if (name == null)
			throw new NullPointerException("name is null");
		
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return "Player " + name;
	}
	
	public static boolean isValidName(String name) {
		return name != null && name.matches(NAME_REGEX);
	}
	
	public boolean equals(Player other) {
		return other != null && other.name != null && other.name.equals(name);
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Player)
			return equals(other);
		else
			return false;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
}

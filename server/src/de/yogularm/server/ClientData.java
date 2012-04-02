package de.yogularm.server;

import java.math.BigInteger;
import java.security.SecureRandom;

import de.yogularm.network.Match;
import de.yogularm.network.Player;

public class ClientData {
  private static final SecureRandom random = new SecureRandom();
  
	public ServerData serverData;
	public Player player;
	public final String key;
	
	public ClientData() {
		key = generateKey();
	}
	
	private String generateKey() {
    return new BigInteger(130, random).toString(32);
	}
}

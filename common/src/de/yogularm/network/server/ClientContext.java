package de.yogularm.network.server;

import java.math.BigInteger;
import java.security.SecureRandom;

import de.yogularm.multiplayer.Player;
import de.yogularm.multiplayer.ServerManager;

public class ClientContext {
  private static final SecureRandom random = new SecureRandom();
  
	private final ServerManager manager;
	private final String key;
	private Player player;
	
	public ClientContext(ServerManager manager) {
		this.manager = manager;
		key = generateKey();
	}

	public String getKey() {
		return key;
	}
	
	public ServerManager getManager() {
		return manager;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	private String generateKey() {
    return new BigInteger(130, random).toString(32);
	}
}

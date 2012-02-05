package de.yogularm.server.network;

import de.yogularm.Game;

public class VersionCommand implements CommandHandler {
	private static final String VERSION = "Yogularm Server Version " + Game.VERSION;
	
  public String handle(ClientData data, String parameter) {
	  return "OK " + VERSION;
  }
}

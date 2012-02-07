package de.yogularm.server.meta;

import de.yogularm.Game;
import de.yogularm.server.ClientData;

public class VersionCommand implements CommandHandler {
	private static final String VERSION = "Yogularm Server Version " + Game.VERSION;
	
  public String handle(ClientData data, String parameter) {
	  return "OK " + VERSION;
  }
}

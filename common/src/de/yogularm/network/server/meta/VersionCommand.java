package de.yogularm.network.server.meta;

import de.yogularm.Game;
import de.yogularm.network.server.ClientContext;

public class VersionCommand implements CommandHandler {
	private static final String VERSION = "Yogularm Server Version " + Game.VERSION;
	
  public String handle(ClientContext data, String parameter) {
	  return "OK " + VERSION;
  }
}

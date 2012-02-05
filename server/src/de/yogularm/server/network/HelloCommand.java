package de.yogularm.server.network;

import de.yogularm.server.Player;

public class HelloCommand extends AbstractCommandHandler {
  public String handle(ClientData data, String parameter) {
		if (parameter == "")
			return err("NO_NAME");
		
		if (!Player.isValidName(parameter))
			return err("INVALID_NAME");
		
		Player p = data.serverData.players.registerPlayer(parameter);
		if (p == null)
			return err("NAME_NOT_AVAILABLE");
		
		data.player = p;
		return ok();
  }
}

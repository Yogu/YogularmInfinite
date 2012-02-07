package de.yogularm.server.meta;

import de.yogularm.network.CommunicationError;
import de.yogularm.network.Player;
import de.yogularm.server.ClientData;

public class HelloCommand extends CommandHandlerUtils implements CommandHandler {
  public String handle(ClientData data, String parameter) {
		if (parameter == "")
			return err(CommunicationError.ILLEGAL_ARGUMENT, "Specify a name");
		
		if (!Player.isValidName(parameter))
			return err(CommunicationError.ILLEGAL_ARGUMENT, "Illegal format for name");
		
		Player p = data.serverData.players.registerPlayer(parameter);
		if (p == null)
			return err(CommunicationError.NAME_NOT_AVAILABLE);
		
		System.out.println("Player " + parameter + " says hello");
		
		data.player = p;
		return ok(data.key);
  }
}

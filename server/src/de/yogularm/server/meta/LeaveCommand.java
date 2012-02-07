package de.yogularm.server.meta;

import de.yogularm.network.CommunicationError;
import de.yogularm.server.ClientData;


public class LeaveCommand extends CommandHandlerUtils implements CommandHandler {
  public String handle(ClientData data, String parameter) {
		if (data.match == null)
			return err(CommunicationError.INVALID_STATE, "You have not joined any match.");
		
		data.match.removePlayer(data.player);
		data.match = null;
		return ok();
  }
}

package de.yogularm.server.meta;

import de.yogularm.network.CommunicationError;
import de.yogularm.network.Match;
import de.yogularm.server.ClientData;

public class CreateCommand extends CommandHandlerUtils implements CommandHandler {
  public String handle(ClientData data, String parameter) {
  	if (data.player == null)
  		return err(CommunicationError.INVALID_STATE, "Say hello");
		if (data.match != null)
			return err(CommunicationError.INVALID_STATE, "Leave the match before creating one");
		
		Match match = new Match(data.player);
		data.serverData.matches.put(match.getID(), match);
		data.match = match;
		match.addPlayer(data.player);
		return ok(match.getID() + "");
  }
}

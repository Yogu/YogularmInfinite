package de.yogularm.server.meta;

import de.yogularm.network.CommunicationError;
import de.yogularm.network.Match;
import de.yogularm.network.MatchState;
import de.yogularm.server.ClientData;

public class JoinCommand extends CommandHandlerUtils implements CommandHandler {
  public String handle(ClientData data, String parameter) {
		if (data.player == null)
			return err(CommunicationError.INVALID_STATE, "Say hello");
			
		int id = 0;
		try {
			id = Integer.parseInt(parameter);
		} catch (NumberFormatException e) { 
			return err(CommunicationError.ILLEGAL_ARGUMENT, "Must be an integer");
		};
		
		Match match = data.serverData.matches.get(id);
		if (match == null)
			return err(CommunicationError.MATCH_NOT_FOUND);
		if (match.getState() != MatchState.OPEN)
			return err(CommunicationError.MATCH_NOT_OPEN);
		
		data.match = match;
		match.addPlayer(data.player);
		return ok("Joined. Waiting for start");
  }
}

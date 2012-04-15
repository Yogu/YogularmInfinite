package de.yogularm.network.server.meta;

import de.yogularm.multiplayer.Match;
import de.yogularm.multiplayer.MatchState;
import de.yogularm.multiplayer.Player;
import de.yogularm.network.CommunicationError;
import de.yogularm.network.server.ClientContext;

public class JoinCommand extends CommandHandlerUtils implements CommandHandler {
  public String handle(ClientContext context, String parameter) {
  	Player player = context.getPlayer();
		if (player == null)
			return err(CommunicationError.INVALID_STATE, "Say hello");
			
		int id = 0;
		try {
			id = Integer.parseInt(parameter);
		} catch (NumberFormatException e) { 
			return err(CommunicationError.ILLEGAL_ARGUMENT, "Must be an integer");
		};
		
		Match match = context.getManager().getMatchByID(id);
		if (match == null)
			return err(CommunicationError.MATCH_NOT_FOUND);
		if (match.getState() != MatchState.OPEN)
			return err(CommunicationError.MATCH_NOT_OPEN);
		
		try {
			player.joinMatch(match);
		} catch (IllegalStateException e) {
			return err(CommunicationError.INVALID_STATE, e.getMessage());
		}
		
		return ok("Joined. Waiting for start");
  }
}

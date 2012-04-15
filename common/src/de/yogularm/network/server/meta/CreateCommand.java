package de.yogularm.network.server.meta;

import de.yogularm.multiplayer.Match;
import de.yogularm.multiplayer.Player;
import de.yogularm.network.CommunicationError;
import de.yogularm.network.server.ClientContext;

public class CreateCommand extends CommandHandlerUtils implements CommandHandler {
  public String handle(ClientContext context, String parameter) {
  	Player player = context.getPlayer();
  	if (player == null)
  		return err(CommunicationError.INVALID_STATE, "Say hello");
		if (player.getCurrentMatch() != null)
			return err(CommunicationError.INVALID_STATE, "Leave the match before creating one");
		
		Match match = context.getManager().startNewMatch(context.getPlayer(), parameter);
		
		return ok(match.getID() + "");
  }
}

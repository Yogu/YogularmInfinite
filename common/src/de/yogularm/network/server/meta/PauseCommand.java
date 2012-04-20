package de.yogularm.network.server.meta;

import de.yogularm.multiplayer.Match;
import de.yogularm.multiplayer.Player;
import de.yogularm.network.CommunicationError;
import de.yogularm.network.server.ClientContext;

public class PauseCommand extends CommandHandlerUtils implements CommandHandler {
  public String handle(ClientContext context, String parameter) {
  	Player player = context.getPlayer();
		if (player == null)
			return err(CommunicationError.INVALID_STATE, "Say hello first");
		Match match = player.getCurrentMatch();
		if (match == null)
			return err(CommunicationError.INVALID_STATE, "Join match before");
		if (match.getOwner() != player)
			return err(CommunicationError.INVALID_STATE, "You do not own the selected match");
		
		try {
			match.pause();
		} catch (IllegalStateException e) {
			return err(CommunicationError.INVALID_STATE, e.getMessage());
		}
		
		return ok();
  }
}
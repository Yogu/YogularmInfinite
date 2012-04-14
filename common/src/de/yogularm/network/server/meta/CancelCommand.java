package de.yogularm.network.server.meta;

import de.yogularm.network.CommunicationError;
import de.yogularm.network.Match;
import de.yogularm.network.MatchState;
import de.yogularm.network.NetworkInformation;
import de.yogularm.network.server.ClientData;

public class CancelCommand extends CommandHandlerUtils implements CommandHandler {
  public String handle(ClientData data, String parameter) {
		if (data.player == null)
			return err(CommunicationError.INVALID_STATE, "Say hello first");
		Match match = data.player.getCurrentMatch();
		if (match == null)
			return err(CommunicationError.INVALID_STATE, "Join match before");
		if (match.getOwner() != data.player)
			return err(CommunicationError.INVALID_STATE, "You do not own the selected match");
		if ((match.getState() == MatchState.CANCELLED) || (match.getState() == MatchState.FINISHED))
			return err(CommunicationError.INVALID_STATE, "Match not running");
		
		data.serverData.matches.remove(match.getID());
		match.cancel();
		
		data.serverData.notifyClients(NetworkInformation.MATCH_CANCELLED, match.getID() + "");
		
		return ok();
  }
}

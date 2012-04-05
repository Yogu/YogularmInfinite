package de.yogularm.server.meta;

import de.yogularm.ServerGame;
import de.yogularm.network.CommunicationError;
import de.yogularm.network.Match;
import de.yogularm.network.MatchState;
import de.yogularm.network.NetworkInformation;
import de.yogularm.server.ClientData;

public class StartCommand extends CommandHandlerUtils implements CommandHandler {
  public String handle(ClientData data, String parameter) {
		if (data.player == null)
			return err(CommunicationError.INVALID_STATE, "Say hello first");
		Match match = data.player.getCurrentMatch();
		if (match == null)
			return err(CommunicationError.INVALID_STATE, "Join match before");
		if (match.getOwner() != data.player)
			return err(CommunicationError.INVALID_STATE, "You do not own the selected match");
		if (match.getState() != MatchState.OPEN)
			return err(CommunicationError.INVALID_STATE, "Match already started");
		
		match.startOnServer();
		
		data.serverData.notifyClients(NetworkInformation.MATCH_STARTED, match.getID() + "");
		
		return ok();
  }
}

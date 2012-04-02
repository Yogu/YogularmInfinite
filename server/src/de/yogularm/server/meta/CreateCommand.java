package de.yogularm.server.meta;

import de.yogularm.network.CommunicationError;
import de.yogularm.network.Match;
import de.yogularm.network.NetworkInformation;
import de.yogularm.server.ClientData;
import de.yogularm.utils.GsonFactory;

public class CreateCommand extends CommandHandlerUtils implements CommandHandler {
  public String handle(ClientData data, String parameter) {
  	if (data.player == null)
  		return err(CommunicationError.INVALID_STATE, "Say hello");
		if (data.player.getCurrentMatch() != null)
			return err(CommunicationError.INVALID_STATE, "Leave the match before creating one");
		
		Match match = new Match(data.player);
		match.setComment(parameter);
		data.serverData.matches.add(match);
		data.player.joinMatch(match);
		
		String json = GsonFactory.createGson().toJson(match);
		data.serverData.notifyClients(NetworkInformation.MATCH_CREATED, json);
		
		return ok(match.getID() + "");
  }
}

package de.yogularm.server.meta;

import de.yogularm.network.CommunicationError;
import de.yogularm.network.Match;
import de.yogularm.network.NetworkInformation;
import de.yogularm.server.ClientData;


public class LeaveCommand extends CommandHandlerUtils implements CommandHandler {
  public String handle(ClientData data, String parameter) {
  	if (data.player == null)
  		return err(CommunicationError.INVALID_STATE, "Say hello first");
  	Match match = data.player.getCurrentMatch();
  	if (match == null)
			return err(CommunicationError.INVALID_STATE, "You have not joined any match.");
		
  	data.player.leaveMatch();
		
		data.serverData.notifyClients(NetworkInformation.PLAYER_LEFT_MATCH, 
			String.format("%s %s", data.player.getName(), match.getID()));
		
		// No more players left?
		if (match.getPlayers().size()== 0) {
			data.serverData.matches.remove(match.getID());
			match.cancel();
			
			data.serverData.notifyClients(NetworkInformation.MATCH_CANCELLED, match.getID() + "");
		}
		
		return ok();
  }
}

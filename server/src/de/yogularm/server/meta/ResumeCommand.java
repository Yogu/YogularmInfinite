package de.yogularm.server.meta;

import de.yogularm.network.CommunicationError;
import de.yogularm.network.MatchState;
import de.yogularm.server.ClientData;

public class ResumeCommand extends CommandHandlerUtils implements CommandHandler {
  public String handle(ClientData data, String parameter) {
		if (data.match == null)
			return err(CommunicationError.INVALID_STATE, "Join match before");
		
		if ((data.match.getState() != MatchState.RUNNING) && (data.match.getState() != MatchState.PAUSED))
			return err(CommunicationError.INVALID_STATE, "Match not paused");
		
		data.match.resume();
		return ok();
  }
}

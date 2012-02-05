package de.yogularm.server.network;

import de.yogularm.server.MatchState;

public class ResumeCommand extends AbstractCommandHandler {
  public String handle(ClientData data, String parameter) {
		if (data.match == null)
			return err("INVALID_STATE", "Join match before");
		
		if ((data.match.getState() != MatchState.RUNNING) && (data.match.getState() != MatchState.PAUSED))
			return err("INVALID_STATE", "Match not paused");
		
		data.match.resume();
		return ok();
  }
}

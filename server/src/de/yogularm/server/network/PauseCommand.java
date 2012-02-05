package de.yogularm.server.network;

import de.yogularm.server.MatchState;

public class PauseCommand extends AbstractCommandHandler {
  public String handle(ClientData data, String parameter) {
		if (data.match == null)
			return err("INVALID_STATE", "Join match before");
		
		if ((data.match.getState() != MatchState.RUNNING) && (data.match.getState() != MatchState.PAUSED))
			return err("INVALID_STATE", "Match not running");
		
		data.match.pause();
		return ok();
  }
}

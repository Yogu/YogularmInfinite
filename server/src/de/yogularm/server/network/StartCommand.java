package de.yogularm.server.network;

import de.yogularm.server.MatchState;

public class StartCommand extends AbstractCommandHandler {
  public String handle(ClientData data, String parameter) {
		if (data.match == null)
			return err("INVALID_STATE", "Join match before");
		
		if (data.match.getState() != MatchState.OPEN)
			return err("INVALID_STATE", "Match already started");
		
		data.match.start();
		return ok();
  }
}

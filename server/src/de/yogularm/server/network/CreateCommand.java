package de.yogularm.server.network;

import de.yogularm.server.Match;

public class CreateCommand extends AbstractCommandHandler {
  public String handle(ClientData data, String parameter) {
  	if (data.player == null)
  		return err("INVALID_STATE", "Say hello");
		if (data.match != null)
			return err("INVALID_STATE", "Leave the match before creating one");
		
		Match match = new Match(data.player);
		data.serverData.matches.put(match.getID(), match);
		data.match = match;
		match.addPlayer(data.player);
		return ok(match.getID() + "");
  }
}

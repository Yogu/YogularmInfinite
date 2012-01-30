package de.yogularm.server.network;

import de.yogularm.server.Match;

public class JoinCommand extends AbstractCommandHandler {
  public String handle(ClientData data, String parameter) {
		if (data.player == null)
			return err("INVALID_STATE", "Say hello");
			
		int id = 0;
		try {
			id = Integer.parseInt(parameter);
		} catch (NumberFormatException e) { 
			return err("ILLEGAL_ARGUMENT", "Must be an integer");
		};
		
		Match match = data.serverData.matches.get(id);
		if (match == null)
			return err("INVALID_ID");
		
		data.match = match;
		match.addPlayer(data.player);
		return ok("Joined. Waiting for start");
  }
}

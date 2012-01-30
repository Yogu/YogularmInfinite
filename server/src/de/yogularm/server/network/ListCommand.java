package de.yogularm.server.network;

import de.yogularm.server.Match;
import de.yogularm.server.MatchState;
import de.yogularm.server.Player;

public class ListCommand extends AbstractCommandHandler {
	public String handle(ClientData data, String parameter) {
		String response = "";
		for (Match match : data.serverData.matches.values()) {
			if (match.getState() == MatchState.OPEN) {
				if (response != "")
					response += ";";
				response += match.getID() + ":";
				for (Player player : match.getPlayers()) {
					response += player.getName() + ",";
				}
			}
		}
		return ok(response);
	}
}

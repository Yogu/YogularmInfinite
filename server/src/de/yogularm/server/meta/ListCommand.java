package de.yogularm.server.meta;

import de.yogularm.network.Match;
import de.yogularm.network.MatchState;
import de.yogularm.server.ClientData;

public class ListCommand extends CommandHandlerUtils implements CommandHandler {
	public String handle(ClientData data, String parameter) {
		String response = "";
		for (Match match : data.serverData.matches.values()) {
			if (match.getState() == MatchState.OPEN) {
				if (response != "")
					response += ";";
				response += match.serialize();
			}
		}
		return ok(response);
	}
}

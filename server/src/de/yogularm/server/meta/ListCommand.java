package de.yogularm.server.meta;

import de.yogularm.network.Match;
import de.yogularm.network.MatchState;
import de.yogularm.server.ClientData;

public class ListCommand extends CommandHandlerUtils implements CommandHandler {
	public String handle(ClientData data, String parameter) {
		String serialized = Match.serializeMatches(data.serverData.matches.values());
		return ok(serialized);
	}
}

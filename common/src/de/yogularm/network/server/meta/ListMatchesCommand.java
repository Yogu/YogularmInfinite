package de.yogularm.network.server.meta;

import java.util.Map;

import com.google.gson.Gson;

import de.yogularm.network.Match;
import de.yogularm.network.server.ClientData;
import de.yogularm.utils.GsonFactory;

public class ListMatchesCommand extends CommandHandlerUtils implements CommandHandler {
	public String handle(ClientData data, String parameter) {
		Gson gson = GsonFactory.createGson();
		Map<Integer, Match> original = data.serverData.matches.getMap();
		/*Map<Integer, Match> clone = new HashMap<Integer, Match>(original);
		for (Match match : original.values()) {
			if (match.getState() != MatchState.OPEN)
				clone.remove(match.getID());
		}*/
		return ok(gson.toJson(/*clone*/original));
	}
}

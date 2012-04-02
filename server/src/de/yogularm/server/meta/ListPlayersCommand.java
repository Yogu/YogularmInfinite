package de.yogularm.server.meta;

import com.google.gson.Gson;

import de.yogularm.server.ClientData;
import de.yogularm.utils.GsonFactory;

public class ListPlayersCommand extends CommandHandlerUtils implements CommandHandler {
	public String handle(ClientData data, String parameter) {
		Gson gson = GsonFactory.createGson();
		return ok(gson.toJson(data.serverData.players.getMap()));
	}
}

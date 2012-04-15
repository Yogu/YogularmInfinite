package de.yogularm.network.server.meta;

import com.google.gson.Gson;

import de.yogularm.network.server.ClientContext;
import de.yogularm.utils.GsonFactory;

public class ListPlayersCommand extends CommandHandlerUtils implements CommandHandler {
	public String handle(ClientContext context, String parameter) {
		Gson gson = GsonFactory.createGson();
		return ok(gson.toJson(context.getManager().getPlayers()));
	}
}

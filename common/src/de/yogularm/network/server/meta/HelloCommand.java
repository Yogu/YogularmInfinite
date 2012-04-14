package de.yogularm.network.server.meta;

import de.yogularm.network.CommunicationError;
import de.yogularm.network.NetworkInformation;
import de.yogularm.network.Player;
import de.yogularm.network.server.ClientData;
import de.yogularm.utils.GsonFactory;

public class HelloCommand extends CommandHandlerUtils implements CommandHandler {
  public String handle(ClientData data, String parameter) {
		if (parameter == "")
			return err(CommunicationError.ILLEGAL_ARGUMENT, "Specify a name");
		if (!Player.isValidName(parameter))
			return err(CommunicationError.ILLEGAL_ARGUMENT, "Illegal format for name");
		if (data.player != null)
			return err(CommunicationError.INVALID_STATE, "You are already logged in");
		
		Player player = data.serverData.players.registerPlayer(parameter);
		if (player == null)
			return err(CommunicationError.NAME_NOT_AVAILABLE);
		
		System.out.println("Player " + parameter + " says hello");

		String json = GsonFactory.createGson().toJson(player);
		data.serverData.notifyClients(NetworkInformation.PLAYER_JOINED_SERVER, json);
		
		data.player = player;
		return ok(data.key);
  }
}

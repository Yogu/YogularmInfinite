package de.yogularm.network.server.meta;

import de.yogularm.multiplayer.Player;
import de.yogularm.network.CommunicationError;
import de.yogularm.network.server.ClientContext;

public class HelloCommand extends CommandHandlerUtils implements CommandHandler {
  public String handle(ClientContext context, String parameter) {
		if (parameter == "")
			return err(CommunicationError.ILLEGAL_ARGUMENT, "Specify a name");
		if (!Player.isValidName(parameter))
			return err(CommunicationError.ILLEGAL_ARGUMENT, "Illegal format for name");
		if (context.getPlayer() != null)
			return err(CommunicationError.INVALID_STATE, "You are already logged in");
		
		Player player = context.getManager().registerPlayer(parameter);
		if (player == null)
			return err(CommunicationError.NAME_NOT_AVAILABLE);
		
		context.setPlayer(player);
		return ok(context.getKey());
  }
}

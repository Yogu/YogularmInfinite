package de.yogularm.network.server.meta;

import de.yogularm.multiplayer.Player;
import de.yogularm.network.CommunicationError;
import de.yogularm.network.server.ClientContext;

public class SayCommand extends CommandHandlerUtils implements CommandHandler {
	public String handle(ClientContext context, String parameter) {
		Player player = context.getPlayer();
		if (player == null)
			return err(CommunicationError.INVALID_STATE, "Say hello first");
		
		parameter = parameter.trim();
		if (!parameter.isEmpty())
			context.getManager().sendMessage(player, parameter);

		return ok();
	}
}

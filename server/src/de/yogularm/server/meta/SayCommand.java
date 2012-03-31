package de.yogularm.server.meta;

import de.yogularm.server.ClientData;

public class SayCommand extends CommandHandlerUtils implements CommandHandler {
  public String handle(ClientData data, String parameter) {
  	System.out.println(data.player.getName() + " says: " + parameter);
		return ok();
  }
}

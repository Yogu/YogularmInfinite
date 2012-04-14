package de.yogularm.network.server.meta;

import de.yogularm.network.NetworkInformation;
import de.yogularm.network.server.ClientData;

public class SayCommand extends CommandHandlerUtils implements CommandHandler {
  public String handle(ClientData data, String parameter) {
  	System.out.println(data.player.getName() + " says: " + parameter);
		
		data.serverData.notifyClients(NetworkInformation.MESSAGE, 
			String.format("%s %s", data.player.getName(), parameter));
		
		return ok();
  }
}

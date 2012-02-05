package de.yogularm.server.network;


public class LeaveCommand extends AbstractCommandHandler {
  public String handle(ClientData data, String parameter) {
		if (data.match == null)
			return err("INVALID_STATE", "You have not joined any match.");
		
		data.match.removePlayer(data.player);
		data.match = null;
		return ok();
  }
}

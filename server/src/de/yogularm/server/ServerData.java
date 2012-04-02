package de.yogularm.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.yogularm.network.Matches;
import de.yogularm.network.NetworkInformation;

public class ServerData {
	private List<NetworkInformationListener> networkInformationListeners = new ArrayList<NetworkInformationListener>();
	
	public final PlayerManager players = new PlayerManager();
	public final Matches matches = new Matches();
	public final Map<String, ClientData> clientData = new HashMap<String, ClientData>();
	
	public void addNetworkInformationListener(NetworkInformationListener listener) {
		networkInformationListeners.add(listener);
	}
	
	public void removeNetworkInformationListener(NetworkInformationListener listener) {
		networkInformationListeners.remove(listener);
	}
	
	public void notifyClients(NetworkInformation information, String parameter) {
		for (NetworkInformationListener listener : networkInformationListeners) {
			listener.send(information, parameter);
		}
		System.out.println(String.format("%s %s", information, parameter));
	}
}

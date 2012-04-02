package de.yogularm.server;

import de.yogularm.network.NetworkInformation;

public interface NetworkInformationListener {
	void send(NetworkInformation information, String parameter);
}

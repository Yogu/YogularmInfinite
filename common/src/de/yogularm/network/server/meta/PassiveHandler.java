package de.yogularm.network.server.meta;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Queue;

import de.yogularm.network.NetworkInformation;
import de.yogularm.network.server.ClientData;
import de.yogularm.network.server.NetworkInformationListener;
import de.yogularm.network.server.ServerData;

public class PassiveHandler {
	private PrintStream out;
	private ServerData server;
	@SuppressWarnings("unused")
	private ClientData client;
	private TheListener listener = new TheListener();
	private Queue<String> queue = new LinkedList<String>();
	
	private static final int IDLE_SLEEP_MILLISECS = 50;

	public PassiveHandler(ServerData server, ClientData client, PrintStream out) {
		this.out = out;
		this.server = server;
		this.client = client;
	}

	public void run() {
		server.addNetworkInformationListener(listener);
		
		try {
			while (true) {
				try {
					String line;
					synchronized(queue) {
						line = queue.poll();
					}
					if (line != null) {
						out.println(line);
					} else
						Thread.sleep(IDLE_SLEEP_MILLISECS);
				} catch (InterruptedException e) {
					break;
				}
				}
		} finally {
			server.removeNetworkInformationListener(listener);
		}
	}

	private void enqueue(NetworkInformation information, String parameter) {
		parameter = parameter.replace('\n', ' ');
		synchronized(queue) {
			queue.add(information.toString() + " " + parameter);
		}
	}
	
	private class TheListener implements NetworkInformationListener {
		public void send(NetworkInformation information, String parameter) {
			enqueue(information, parameter);
		}		
	}
}

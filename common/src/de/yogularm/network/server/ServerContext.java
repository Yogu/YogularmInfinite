package de.yogularm.network.server;

import java.util.HashMap;
import java.util.Map;

import de.yogularm.multiplayer.ServerManager;

public class ServerContext {
	private final ServerManager manager;
	private final Map<String, ClientContext> clientContexts = new HashMap<String, ClientContext>();
	
	public ServerContext(ServerManager manager) {
		if (manager == null)
			throw new NullPointerException("manager is null");
		
		this.manager = manager;
	}
	
	public ServerManager getManager() {
		return manager;
	}
	
	public ClientContext getClientContext(String key) {
		if (key == null)
			throw new NullPointerException("key is null");
		
		synchronized (clientContexts) {
			return clientContexts.get(key);
		}
	}
	
	public ClientContext createClientContext() {
		ClientContext data = new ClientContext(manager);
		synchronized (clientContexts) {
			clientContexts.put(data.getKey(), data);
		}
		return data;
	}
	
	public void removeClientContext(ClientContext context) {
		if (context == null)
			throw new NullPointerException("context is null");
		
		synchronized (clientContexts) {
			clientContexts.remove(context.getKey());
		}
	}
}

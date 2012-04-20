package de.yogularm.network.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.yogularm.components.Component;
import de.yogularm.components.Player;
import de.yogularm.geometry.IntRect;
import de.yogularm.geometry.Point;
import de.yogularm.geometry.Vector;
import de.yogularm.network.AbstractBackgroundHandler;
import de.yogularm.network.Components;
import de.yogularm.network.NetworkGlobals;
import de.yogularm.network.NetworkPacket;

public class DefaultBinaryHandler extends AbstractBackgroundHandler implements BinaryHandler {
	private final Collection<BinaryHandlerListener> listeners = new ArrayList<BinaryHandlerListener>();
	private final DataInputStream in;
	private final DataOutputStream out;
	private final String sessionKey;
	private boolean isInitialized;
	private final Object lock = new Object();

	public DefaultBinaryHandler(DataInputStream in, DataOutputStream out, String sessionKey) {
		this.in = in;
		this.out = out;
		this.sessionKey = sessionKey;
	}

	@Override
	public void observeSector(IntRect observationRange) throws IOException {
		initPacket(NetworkPacket.OBSERVE);
		out.writeInt(observationRange.getMin().getX());
		out.writeInt(observationRange.getMin().getY());
		out.writeInt(observationRange.getMax().getX());
		out.writeInt(observationRange.getMax().getY());
	}

	@Override
	public void sendPlayerPosition(Vector position, Vector momentum) throws IOException {
		initPacket(NetworkPacket.PLAYER_POSITION);
		out.writeFloat(position.getX());
		out.writeFloat(position.getY());
		out.writeFloat(momentum.getX());
		out.writeFloat(momentum.getY());
	}

	@Override
	public void addListener(BinaryHandlerListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	@Override
	public void removeListener(BinaryHandlerListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	@Override
	public void run() throws IOException {
		while (!isInterrupted()) {
			receivePacket();
		}
	}

	private void receivePacket() throws IOException {
		int id = in.readInt();
		// System.out.println("Receiving packet " + id);
		if (id < 0 || id >= NetworkPacket.values().length)
			throw new IOException("Invalid packet id: " + id);

		// Used several times
		Component component;
		int componentID;

		NetworkPacket packet = NetworkPacket.values()[id];
		switch (packet) {
		case INIT_WORLD:
			int sectorWidth = in.readInt();
			int sectorHeight = in.readInt();
			Player player = (Player) receiveComponent(in);
			synchronized (listeners) {
				for (BinaryHandlerListener listener : listeners) {
					listener.worldInitialized(sectorWidth, sectorHeight);
					listener.playerComponentReceived(player);
				}
			}
			break;

		case COMPLETE_SECTOR:
			Point sector = new Point(in.readInt(), in.readInt());
			int count = in.readInt();
			List<Component> list = new ArrayList<Component>(count);
			for (int i = 0; i < count; i++) {
				list.add(receiveComponent(in));
			}

			synchronized (listeners) {
				for (BinaryHandlerListener listener : listeners) {
					listener.sectorReceived(sector, list);
				}
			}
			break;

		case ADDED:
			component = receiveComponent(in);

			synchronized (listeners) {
				for (BinaryHandlerListener listener : listeners) {
					listener.componentAdded(component);
				}
			}
			break;

		case REMOVED:
			componentID = in.readInt();

			synchronized (listeners) {
				for (BinaryHandlerListener listener : listeners) {
					listener.componentRemoved(componentID);
				}
			}
			break;

		case CHANGED:
			componentID = in.readInt();
			Component newComponent = receiveComponent(in);

			synchronized (listeners) {
				for (BinaryHandlerListener listener : listeners) {
					listener.componentChanged(componentID, newComponent);
				}
			}
			break;
			
		case QUICK_CHANGE:
			componentID = in.readInt();
			Vector position = new Vector(in.readFloat(), in.readFloat());
			Vector momentum = new Vector(in.readFloat(), in.readFloat());

			synchronized (listeners) {
				for (BinaryHandlerListener listener : listeners) {
					listener.quickUpdate(componentID, position, momentum);
				}
			}
			break;
		default:
			throw new IOException("Invalid packet id: " + id);
		}

		// System.out.println("Received packet " + packet);
	}

	private Component receiveComponent(DataInputStream in) throws IOException {
		int typeID = in.readInt();
		Component component = Components.createComponent(typeID);
		if (component == null)
			throw new IOException("Invalid component class id: " + typeID);
		component.read(in);
		component.setNetworkComponent(true);
		return component;
	}

	private void initPacket(NetworkPacket packet) throws IOException {
		init();
		out.writeInt(packet.ordinal());
		// System.out.println("Sending packet " + packet);
	}

	private void init() throws IOException {
		synchronized (lock) {
			if (!isInitialized) {
				sendSessionKey();
				isInitialized = true;
			}
		}
	}

	private void sendSessionKey() throws IOException {
		out.writeInt(sessionKey.length());
		byte[] bytes = sessionKey.getBytes(Charset.forName(NetworkGlobals.CHARSET));
		out.write(bytes, 0, bytes.length);
		int response = in.readByte();
		if (response == NetworkGlobals.BINARY_INVALID_SESSION)
			throw new IOException("Server refused session key");
		else if (response != NetworkGlobals.BINARY_VALID_SESSION)
			throw new IOException("Invalid response from server on session key validation: " + response);
	}
}

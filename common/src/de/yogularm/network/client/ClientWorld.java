package de.yogularm.network.client;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import de.yogularm.building.BuildingSite;
import de.yogularm.components.AbstractWorld;
import de.yogularm.components.Body;
import de.yogularm.components.Component;
import de.yogularm.components.ComponentCollection;
import de.yogularm.components.ComponentTree;
import de.yogularm.components.ObservableComponentCollection;
import de.yogularm.components.Player;
import de.yogularm.event.Event;
import de.yogularm.geometry.IntRect;
import de.yogularm.geometry.Point;
import de.yogularm.geometry.Rect;
import de.yogularm.geometry.Vector;
import de.yogularm.network.Components;
import de.yogularm.network.NetworkPacket;

public class ClientWorld extends AbstractWorld {
	private ObservableComponentCollection components;
	private Player player;
	private boolean isStopped;
	private IntRect lastObservedRange = null;
	private IntRect observedRange = new IntRect(0, 0, 0, 0);
	private int sectorWidth;
	private int sectorHeight;

	public final Event<Void> onWorldInitialized = new Event<Void>(this);

	public ClientWorld() {
	}

	@Override
	public int update(float elapsedTime, Rect actionRange) {
		observedRange = new IntRect(getSector(actionRange.getMinVector()),
				getSector(actionRange.getMaxVector()));
		return super.update(elapsedTime, actionRange);
	}

	public void handleInput(InputStream in) throws IOException {
		DataInputStream dataIn = new DataInputStream(in);
		while (!isStopped) {
			if (dataIn.available() > 0)
				receivePacket(dataIn);
			else {
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	}

	public void handleOutput(OutputStream out) throws IOException {
		DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
		while (!isStopped) {
			sendObservedRange(dataOut);

			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				return;
			}
		}
	}

	public void stop() {
		isStopped = true;
	}

	@Override
	public ComponentCollection getComponents() {
		return components;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public BuildingSite getBuildingSite() {
		return null;
	}

	private void receivePacket(DataInputStream in) throws IOException {
		int id = in.readInt();
		if (id < 0 || id >= NetworkPacket.values().length)
			throw new IOException("Invalid packet id: " + id);

		// Used several times
		Component component;
		int componentID;

		NetworkPacket packet = NetworkPacket.values()[id];
		switch (packet) {
		case INIT_WORLD:
			sectorWidth = in.readInt();
			sectorHeight = in.readInt();
			components = new ComponentTree(sectorWidth, sectorHeight);
			player = new Player(components);
			components.add(player);
			onWorldInitialized.call(null);
			break;
		case COMPLETE_SECTOR:
			if (components != null) {
				Point sector = new Point(in.readInt(), in.readInt());
				int count = in.readInt();
				List<Component> list = new ArrayList<Component>(count);
				for (int i = 0; i < count; i++) {
					list.add(receiveComponent(in));
				}
				List<Component> oldComponents = components.getComponentsOfSector(sector);
				for (Component comp : oldComponents) {
					if (comp != player)
						components.remove(comp);
				}
				for (Component comp : list) {
					components.add(comp);
				}
				System.out.println(String.format("Replaced %d components by %d in sector %s",
						oldComponents.size(), list.size(), sector));
			}
			break;
		case ADDED:
			if (components != null) {
				component = receiveComponent(in);
				components.add(component);
			}
			break;
		case REMOVED:
			if (components != null) {
				componentID = in.readInt();
				component = components.getByID(componentID);
				if (component != null && component != player)
					components.remove(component);
			}
			break;
		case CHANGED:
			if (components != null) {
				componentID = in.readInt();
				component = components.getByID(componentID);
				if (component != null && component != player) {
					components.remove(component);
					Component newComponent = component = receiveComponent(in);
					components.add(newComponent);
				}
			}
			break;
		case QUICK_CHANGE:
			if (components != null) {
				componentID = in.readInt();
				component = components.getByID(componentID);
				if (component != null) {
					//System.out.println("Quick change: " + component.getID() + " (" + component.getClass().getSimpleName() + ")");
					if (component instanceof Body) {
						Vector position = new Vector(in.readFloat(), in.readFloat());
						Vector momentum = new Vector(in.readFloat(), in.readFloat());
						((Body)component).pushTo(position, momentum);
					} else {
						component.setPosition(new Vector(in.readFloat(), in.readFloat()));
						in.skip(2 * 4);
					}
				} else {
					in.skip(4 * 4);
				}
			}
			break;
		default:
			throw new IOException("Invalid packet id: " + id);
		}

		//System.out.println("Received packet " + packet);
	}

	private Component receiveComponent(DataInputStream in) throws IOException {
		int id = in.readInt();
		Component component = Components.createComponent(id, components);
		if (component == null)
			throw new IOException("Invalid component class id: " + id);
		component.read(in);
		component.setNetworkComponent(true);
		return component;
	}

	private Point getSector(Vector position) {
		return new Point((int) Math.floor(position.getX() / sectorWidth), (int) Math.floor(position
				.getY() / sectorHeight));
	}

	private void sendObservedRange(DataOutputStream out) throws IOException {
		if (lastObservedRange == null || !lastObservedRange.equals(observedRange)) {
			initPacket(out, NetworkPacket.OBSERVE);
			out.writeInt(observedRange.getMin().getX());
			out.writeInt(observedRange.getMin().getY());
			out.writeInt(observedRange.getMax().getX());
			out.writeInt(observedRange.getMax().getY());
			lastObservedRange = observedRange;
			out.flush();
		}
	}

	private void initPacket(DataOutputStream out, NetworkPacket packet) throws IOException {
		out.writeInt(packet.ordinal());
		//System.out.println("Sending packet " + packet);
	}
}

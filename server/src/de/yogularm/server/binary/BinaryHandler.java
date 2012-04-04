package de.yogularm.server.binary;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.yogularm.ServerGame;
import de.yogularm.components.Component;
import de.yogularm.components.ComponentCollectionListener;
import de.yogularm.components.MultiPlayerWorld;
import de.yogularm.components.MultiPlayerWorldListener;
import de.yogularm.components.ObservableComponentCollection;
import de.yogularm.geometry.Point;
import de.yogularm.network.Components;
import de.yogularm.network.Match;
import de.yogularm.network.NetworkPacket;
import de.yogularm.network.Player;

public class BinaryHandler {
	private DataInputStream in;
	private DataOutputStream out;
	private Player player;
	private Match match;
	private ServerGame game;
	private MultiPlayerWorld world;
	private Thread receiveThread;
	private IOException writeException;

	private Point firstObservedSector = Point.ZERO;
	private Point lastObservedSector = Point.ZERO;
	private Collection<Point> observedSectors = new ArrayList<Point>();

	public BinaryHandler(InputStream in, OutputStream out, Player player) {
		this.in = new DataInputStream(in);
		this.out = new DataOutputStream(out);
		this.player = player;
		match = player.getCurrentMatch();
		game = match.game;
		world = game.getWorld();

		world.getComponents().addListener(new TheComponentCollectionListener());
		world.addListener(new TheMultiPlayerWorldListener());
	}

	public void run() throws IOException {
		receiveThread = Thread.currentThread();
		sendInfo();
		while (!receiveThread.isInterrupted()) {
			if (in.available() > 0)
				receivePacket();
			else {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					break;
				}
			}
		}

		// Because other threads can write, i/o exceptions may have occurred on
		// their threads
		if (writeException != null)
			throw new IOException("Exception while writing", writeException);
	}

	private void receivePacket() throws IOException {
		int id = in.readInt();
		/* int length = in.readInt(); */
		if (id < 0 || id >= NetworkPacket.values().length) {
			/* in.skip(length); */
			throw new IOException("Invalid packet id: " + id);
		}

		// int read = 0;
		NetworkPacket packet = NetworkPacket.values()[id];
		switch (packet) {
		case OBSERVE:
			world.stopObservation(observedSectors);
			synchronized (firstObservedSector) {
				firstObservedSector = new Point(in.readInt(), in.readInt());
				lastObservedSector = new Point(in.readInt(), in.readInt());
			}
			recalculateObservedSectors();
			world.observeSectors(observedSectors);
			sendSectors();
			// read = 4 * 4;
			break;
		}

		// in.skip(length - read);
	}

	private void sendInfo() throws IOException {
		synchronized (out) {
			initPacket(NetworkPacket.WORLD_INFO/* , 8 */);
			out.write(MultiPlayerWorld.SECTOR_WIDTH);
			out.write(MultiPlayerWorld.SECTOR_HEIGHT);
		}
	}

	private void sendSectors() throws IOException {
		// observedSectors is immutable, so we don't need synchronization here
		for (Point sector : observedSectors) {
			sendSector(sector);
		}
	}

	private void sendAddedComponent(Component component) {
		synchronized (out) {
			try {
				initPacket(NetworkPacket.ADDED);
				sendComponent(component);
			} catch (IOException e) {
				delegateWriteException(e);
			}
		}
	}

	private void sendRemovedComponent(Component component) {
		synchronized (out) {
			try {
				initPacket(NetworkPacket.REMOVED);
				out.writeInt(component.getID());
			} catch (IOException e) {
				delegateWriteException(e);
			}
		}
	}

	private void sendChangedComponent(Component component) {
		synchronized (out) {
			try {
				initPacket(NetworkPacket.CHANGED);
				sendComponent(component);
			} catch (IOException e) {
				delegateWriteException(e);
			}
		}
	}

	private void sendSector(Point sector) throws IOException {
		synchronized (out) {
			List<Component> components = world.getComponents().getComponentsOfSector(sector);
			// int length = 4 + components.size() * 2 * 4;
			initPacket(NetworkPacket.COMPLETE_SECTOR/* , length */);
			out.write(sector.getX());
			out.write(sector.getY());
			for (Component component : components) {
				sendComponent(component);
			}
		}
	}

	private void sendComponent(Component component) throws IOException {
		Class<? extends Component> type = component.getClass();
		int id = Components.getID(type);
		out.write(id);
		component.write(out);
	}

	private void initPacket(NetworkPacket packet/* , int length */) throws IOException {
		out.write(packet.ordinal());
		// out.write(length);
	}

	private void recalculateObservedSectors() {
		Collection<Point> sectors = new ArrayList<Point>();
		for (int x = firstObservedSector.getX(); x <= lastObservedSector.getX(); x++) {
			for (int y = firstObservedSector.getY(); y <= lastObservedSector.getY(); y++) {
				sectors.add(new Point(x, y));
			}
		}
		observedSectors = sectors;
	}

	private class TheComponentCollectionListener implements ComponentCollectionListener {
		@Override
		public void componentAdded(ObservableComponentCollection collection, Component component,
				Point sector) {
			if (observedSectors.contains(sector)) {
				sendAddedComponent(component);
			}
		}

		@Override
		public void componentRemoved(ObservableComponentCollection collection, Component component,
				Point sector) {
			if (observedSectors.contains(sector)) {
				sendRemovedComponent(component);
			}
		}

		@Override
		public void componentMoved(ObservableComponentCollection collection, Component component,
				Point lastSector, Point newSector, boolean sectorHasChanged) {
			if (sectorHasChanged) {
				if (observedSectors.contains(lastSector) && !observedSectors.contains(newSector)) {
					sendRemovedComponent(component);
				} else if (observedSectors.contains(newSector) && !observedSectors.contains(lastSector)) {
					sendAddedComponent(component);
				}
			}
		}
	}
	
	private class TheMultiPlayerWorldListener implements MultiPlayerWorldListener {
		@Override
		public void componentChanged(MultiPlayerWorld world, Component component, Point sector) {
			if (observedSectors.contains(sector)) {
				sendChangedComponent(component);
			}
		}
	}

	private void delegateWriteException(IOException e) {
		writeException = e;
		receiveThread.interrupt();
	}
}

package de.yogularm.network.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import de.yogularm.components.Body;
import de.yogularm.components.Component;
import de.yogularm.geometry.Point;
import de.yogularm.geometry.Vector;
import de.yogularm.multiplayer.Match;
import de.yogularm.multiplayer.MatchListener;
import de.yogularm.multiplayer.MatchManager;
import de.yogularm.multiplayer.MatchState;
import de.yogularm.multiplayer.MultiPlayerWorld;
import de.yogularm.multiplayer.Player;
import de.yogularm.network.Components;
import de.yogularm.network.NetworkPacket;

public class BinaryHandler extends BasicServerHandler {
	private DataInputStream in;
	private DataOutputStream out;
	private ClientContext context;
	private Player player;
	private Match match;
	private MatchManager manager;
	private IOException writeException;
	private TheListener listener = new TheListener();

	private Point firstObservedSector = Point.ZERO;
	private Point lastObservedSector = Point.ZERO;
	private Collection<Point> observedSectors = new ArrayList<Point>();

	public BinaryHandler(DataInputStream in, DataOutputStream out, ClientContext clientContext,
			ServerHandlerFactory handlerFactory) {
		super(handlerFactory);
		this.in = in;
		this.out = out;
		context = clientContext;
		player = context.getPlayer();
		match = player.getCurrentMatch();
		manager = context.getManager().getMatchManager(match);
		manager.addListener(listener);
	}

	public void run() throws IOException {
		initWorld();
		int i = 0;
		while (!isInterrupted()) {
			out.write(i++);
			out.flush();
		}

		while (!isInterrupted()) {
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
		if (id < 0 || id >= NetworkPacket.values().length)
			throw new IOException("Invalid packet id: " + id);

		NetworkPacket packet = NetworkPacket.values()[id];
		switch (packet) {
		case OBSERVE:
			manager.stopObservation(observedSectors);
			synchronized (firstObservedSector) {
				firstObservedSector = new Point(in.readInt(), in.readInt());
				lastObservedSector = new Point(in.readInt(), in.readInt());
			}
			recalculateObservedSectors();
			manager.observeSectors(observedSectors);
			sendSectors();
			break;
		case PLAYER_POSITION:
			Vector newPosition = new Vector(in.readFloat(), in.readFloat());
			Vector newMomentum = new Vector(in.readFloat(), in.readFloat());
			manager.setPlayerPosition(player, newPosition, newMomentum);
			break;
		default:
			throw new IOException("Invalid packet id: " + id);
		}

		System.out.println("Received packet " + packet);
	}

	private void initWorld() throws IOException {
		synchronized (out) {
			initPacket(NetworkPacket.INIT_WORLD/* , 8 */);
			out.writeInt(MultiPlayerWorld.SECTOR_WIDTH);
			out.writeInt(MultiPlayerWorld.SECTOR_HEIGHT);
			sendComponent(manager.getPlayerComponent(player));
			out.flush();
		}
	}

	private void sendSectors() throws IOException {
		// observedSectors is immutable, so we don't need synchronization here
		for (Point sector : observedSectors) {
			sendSector(sector);
		}
	}

	private void sendAddedComponent(Component component) {
		assert component != null;
		
		synchronized (out) {
			try {
				initPacket(NetworkPacket.ADDED);
				sendComponent(component);
				out.flush();
			} catch (IOException e) {
				delegateWriteException(e);
			}
		}
	}

	private void sendRemovedComponent(Component component) {
		assert component != null;
		
		synchronized (out) {
			try {
				initPacket(NetworkPacket.REMOVED);
				out.writeInt(component.getID());
				out.flush();
			} catch (IOException e) {
				delegateWriteException(e);
			}
		}
	}

	private void sendChangedComponent(Component component) {
		assert component != null;
		
		synchronized (out) {
			try {
				initPacket(NetworkPacket.CHANGED);
				sendComponent(component);
				out.flush();
			} catch (IOException e) {
				delegateWriteException(e);
			}
		}
	}

	private void sendQuickChange(Component component) {
		assert component != null;
		
		synchronized (out) {
			try {
				initPacket(NetworkPacket.QUICK_CHANGE);
				out.writeInt(component.getID());
				out.writeFloat(component.getPosition().getX());
				out.writeFloat(component.getPosition().getY());
				if (component instanceof Body) {
					Vector momentum = ((Body) component).getMomentum();
					out.writeFloat(momentum.getX());
					out.writeFloat(momentum.getY());
				} else {
					out.writeFloat(0);
					out.writeFloat(0);
				}
				out.flush();
			} catch (IOException e) {
				delegateWriteException(e);
			}
		}
	}

	private void sendSector(Point sector) throws IOException {
		assert sector != null;
		
		synchronized (out) {
			Collection<Component> components = manager.getComponentsOfSector(sector);
			initPacket(NetworkPacket.COMPLETE_SECTOR);
			out.writeInt(sector.getX());
			out.writeInt(sector.getY());
			out.writeInt(components.size());
			for (Component component : components) {
				sendComponent(component);
			}
			out.flush();
		}
	}

	private void sendComponent(Component component) throws IOException {
		assert component != null;
		
		Class<? extends Component> type = component.getClass();
		int id = Components.getID(type);
		out.writeInt(id);
		component.write(out);
	}

	private void initPacket(NetworkPacket packet) throws IOException {
		out.writeInt(packet.ordinal());
		System.out.println("Sending packet " + packet);
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

	private class TheListener implements MatchListener {
		@Override
		public void componentAdded(Component component, Point sector) {
			if (observedSectors.contains(sector)) {
				sendAddedComponent(component);
			}
		}

		@Override
		public void componentRemoved(Component component, Point sector) {
			if (observedSectors.contains(sector)) {
				sendRemovedComponent(component);
			}
		}

		@Override
		public void componentChangedSector(Component component, Point lastSector, Point newSector) {
			if (observedSectors.contains(lastSector) && !observedSectors.contains(newSector)) {
				sendRemovedComponent(component);
			} else if (observedSectors.contains(newSector) && !observedSectors.contains(lastSector)) {
				sendAddedComponent(component);
			}
		}

		@Override
		public void componentChanged(Component component, Point sector) {
			if (observedSectors.contains(sector)) {
				sendChangedComponent(component);
			}
		}

		@Override
		public void quickChange(Component component, Point sector) {
			if (observedSectors.contains(sector)) {
				sendQuickChange(component);
			}
		}

		@Override
		public void stateChanged(MatchState oldState, MatchState newState) {
			
		}
	}

	private void delegateWriteException(IOException e) {
		writeException = e;
		interrupt();
	}
}

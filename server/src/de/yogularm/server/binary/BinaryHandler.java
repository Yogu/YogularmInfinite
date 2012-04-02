package de.yogularm.server.binary;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import de.yogularm.MultiPlayerWorld;
import de.yogularm.components.Component;
import de.yogularm.geometry.Point;
import de.yogularm.network.Match;
import de.yogularm.network.NetworkPacket;
import de.yogularm.network.Player;

public class BinaryHandler {
	private DataInputStream in;
	private DataOutputStream out;
	private Player player;
	private Match match;
	private MultiPlayerWorld world;
	
	private Point firstObservedSector = Point.ZERO;
	private Point lastObservedSector = Point.ZERO;
	
	public BinaryHandler(InputStream in, OutputStream out, Player player) {
		this.in = new DataInputStream(in);
		this.out = new DataOutputStream(out);
		this.player = player;
		match = player.getCurrentMatch();
		world = match.world;
	}
	
	public void run() throws IOException {
		sendInfo();
		while (true) {
			receivePacket();
		}
	}
	
	private void receivePacket() throws IOException {
		int id = in.readInt();
		int length = in.readInt();
		in.skip(length);
		if (id < 0 || id >= NetworkPacket.values().length)
			throw new IOException("Invalid packet id: " + id);

		int read = 0;
		NetworkPacket packet = NetworkPacket.values()[id];
		switch (packet) {
		case OBSERVE:
			synchronized(firstObservedSector) {
				firstObservedSector = new Point(in.readInt(), in.readInt());
				lastObservedSector = new Point(in.readInt(), in.readInt());
			}
			sendSectors();
			read = 4 * 4;
			break;
		}

		in.skip(length - read);
	}
	
	private void sendInfo() throws IOException {
		initPacket(NetworkPacket.WORLD_INFO, 8);
		out.write(MultiPlayerWorld.SECTOR_WIDTH);
		out.write(MultiPlayerWorld.SECTOR_HEIGHT);
	}
	
	private void sendSectors() throws IOException {
		synchronized(firstObservedSector) {
			for (int x = firstObservedSector.getX(); x <= lastObservedSector.getX(); x++) {
				for (int y = firstObservedSector.getY(); y <= lastObservedSector.getY(); y++) {
					sendSector(new Point(x, y));
				}
			}
		}
	}
	
	private void sendSector(Point sector) throws IOException {
		List<Component> components = world.getComponents().getComponentsOfSector(sector);
		int length = 4 + components.size() * 2 * 4;
		initPacket(NetworkPacket.COMPLETE_SECTOR, length);
		out.write(components.size());
		for (Component component : components) {
			out.writeFloat(component.getPosition().getX());
			out.writeFloat(component.getPosition().getY());
		}
	}
	
	private void initPacket(NetworkPacket packet, int length) throws IOException {
		out.write(packet.ordinal());
		out.write(length);
	}
}

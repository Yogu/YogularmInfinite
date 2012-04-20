package de.yogularm.network.server.meta;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;

import de.yogularm.multiplayer.Match;
import de.yogularm.multiplayer.MatchState;
import de.yogularm.multiplayer.Player;
import de.yogularm.multiplayer.ServerListener;
import de.yogularm.network.NetworkInformation;
import de.yogularm.network.server.BasicServerHandler;
import de.yogularm.network.server.ClientContext;
import de.yogularm.network.server.ServerHandlerFactory;
import de.yogularm.utils.GsonFactory;

public class PassiveHandler extends BasicServerHandler {
	private PrintWriter out;
	private ClientContext context;
	private TheListener listener = new TheListener();
	private Queue<String> queue = new LinkedList<String>();

	private static final int IDLE_SLEEP_MILLISECS = 50;

	public PassiveHandler(PrintWriter out, ClientContext clientContext,
			ServerHandlerFactory handlerFactory) {
		super(handlerFactory);
		this.out = out;
		this.context = clientContext;
	}

	public void run() {
		context.getManager().addListener(listener);

		try {
			while (!isInterrupted()) {
				try {
					String line;
					synchronized (queue) {
						line = queue.poll();
					}
					if (line != null) {
						out.println(line);
						out.flush();
					} else
						Thread.sleep(IDLE_SLEEP_MILLISECS);
				} catch (InterruptedException e) {
					break;
				}
			}
		} finally {
			context.getManager().removeListener(listener);
		}
	}

	private void enqueue(NetworkInformation information, String parameter) {
		parameter = parameter.replace('\n', ' ');
		synchronized (queue) {
			queue.add(information.toString() + " " + parameter);
		}
	}

	private class TheListener implements ServerListener {
		@Override
		public void playerAdded(Player player) {
			assert player != null;
			String json = GsonFactory.createGson().toJson(player);
			enqueue(NetworkInformation.PLAYER_JOINED_SERVER, json);
		}

		@Override
		public void playerRemoved(Player player) {
			assert player != null;
			enqueue(NetworkInformation.PLAYER_LEFT_SERVER, player.getName());
		}

		@Override
		public void matchCreated(Match match) {
			assert match != null;
			String json = GsonFactory.createGson().toJson(match);
			enqueue(NetworkInformation.MATCH_CREATED, json);
		}

		@Override
		public void matchChangedState(Match match, MatchState oldState, MatchState newState) {
			assert match != null;
			switch (newState) {
			case RUNNING:
				if (oldState == MatchState.RUNNING)
					enqueue(NetworkInformation.MATCH_RESUMED, match.getID() + "");
				else
					enqueue(NetworkInformation.MATCH_STARTED, match.getID() + "");
				break;
			case PAUSED:
				enqueue(NetworkInformation.MATCH_PAUSED, match.getID() + "");
				break;
			case CANCELLED:
				enqueue(NetworkInformation.MATCH_CANCELLED, match.getID() + "");
				break;
			}
		}

		@Override
		public void playerJoinedMatch(Player player, Match match) {
			enqueue(NetworkInformation.PLAYER_JOINED_MATCH,
					String.format("%s %s", player.getName(), match.getID()));
		}

		@Override
		public void playerLeftMatch(Player player, Match match) {
			enqueue(NetworkInformation.PLAYER_LEFT_MATCH,
					String.format("%s %s", player.getName(), match.getID()));
		}

		@Override
		public void messageReceived(Player sender, String message) {
			enqueue(NetworkInformation.MESSAGE, String.format("%s %s", sender.getName(), message));
		}
	}
}

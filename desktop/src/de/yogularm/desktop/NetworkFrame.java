package de.yogularm.desktop;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import de.yogularm.event.EventArgs;
import de.yogularm.event.EventListener;
import de.yogularm.event.ExceptionEventArgs;
import de.yogularm.network.Match;
import de.yogularm.network.MatchState;
import de.yogularm.network.Matches;
import de.yogularm.network.Player;
import de.yogularm.network.Players;
import de.yogularm.network.client.GameConnection;
import de.yogularm.network.client.MatchStartedEventArgs;
import de.yogularm.network.client.MessageEventArgs;
import javax.swing.border.TitledBorder;

public class NetworkFrame extends Page {
	private static final long serialVersionUID = 2671570492612526987L;

	private GameConnection connection;
	
	private CustomTextField chatInputField;
	private JButton sendChatMessageButton;
	private JTextArea chatField;
	private JLabel serverLabel;
	private JButton newMatchButton;
	private JButton joinMatchButton;
	private JButton leaveMatchButton;
	private JButton startMatchButton;
	private JButton cancelMatchButton;
	private JTable matchTable;

	/**
	 * Create the frame.
	 */
	public NetworkFrame(final SwingLauncher launcher, final GameConnection connection) {
		super(launcher);
		this.connection = connection;
		
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		add(splitPane, BorderLayout.CENTER);
		
		JPanel rightPanel = new JPanel();
		rightPanel.setBorder(null);
		splitPane.setRightComponent(rightPanel);
		rightPanel.setLayout(new BorderLayout(0, 0));
		
		JSplitPane rightSplitPane = new JSplitPane();
		rightSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		rightPanel.add(rightSplitPane, BorderLayout.CENTER);
		
		JPanel rightBottomPanel = new JPanel();
		rightBottomPanel.setBorder(new TitledBorder(null, "Chat", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		rightSplitPane.setRightComponent(rightBottomPanel);
		rightBottomPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel chatComposePanel = new JPanel();
		chatComposePanel.setBorder(null);
		rightBottomPanel.add(chatComposePanel, BorderLayout.SOUTH);
		chatComposePanel.setLayout(new BorderLayout(0, 0));
		
		chatInputField = new CustomTextField();
		chatInputField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == '\n')
					sendChatMessage();
			}
		});
		chatInputField.addInputMethodListener(new InputMethodListener() {
			public void caretPositionChanged(InputMethodEvent arg0) {
			}
			public void inputMethodTextChanged(InputMethodEvent arg0) {
				sendChatMessageButton.setEnabled(!chatInputField.getText().trim().equals(""));
			}
		});
		chatInputField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				chatInputField.selectAll();
			}
		});
		chatComposePanel.add(chatInputField, BorderLayout.CENTER);
		chatInputField.setPlaceholder("Type to compose...");
		chatInputField.setColumns(10);
		
		sendChatMessageButton = new JButton("Send");
		sendChatMessageButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendChatMessage();
			}
		});
		chatComposePanel.add(sendChatMessageButton, BorderLayout.EAST);
		
		JScrollPane chatScrollPane = new JScrollPane();
		rightBottomPanel.add(chatScrollPane, BorderLayout.CENTER);
		
		chatField = new JTextArea();
		chatField.setEditable(false);
		chatScrollPane.setViewportView(chatField);
		
		JPanel rightTopPanel = new JPanel();
		rightTopPanel.setBorder(new TitledBorder(null, "Other players on this server", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		rightSplitPane.setLeftComponent(rightTopPanel);
		rightTopPanel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane playersScrollPane = new JScrollPane();
		rightTopPanel.add(playersScrollPane, BorderLayout.CENTER);
		
		JList<String> playerList = new JList<String>(new PlayersModel(connection.getOtherPlayers()));
		playersScrollPane.setViewportView(playerList);
		
		JPanel leftPanel = new JPanel();
		splitPane.setLeftComponent(leftPanel);
		leftPanel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane matchesScrollPane = new JScrollPane();
		leftPanel.add(matchesScrollPane, BorderLayout.CENTER);
		
		matchTable = new JTable(new MatchesModel(connection.getOpenMatches()));
		matchTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		matchesScrollPane.setViewportView(matchTable);
		matchTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				joinMatchButton.setEnabled(connection.getPlayer().getCurrentMatch() == null && isOpenMatchSelected());
			}
		});
		
		JPanel leftTopPanel = new JPanel();
		leftTopPanel.setBorder(null);
		leftPanel.add(leftTopPanel, BorderLayout.NORTH);
		leftTopPanel.setLayout(new BorderLayout(0, 0));
		
		serverLabel = new JLabel("Connected to Server");
		leftTopPanel.add(serverLabel);
		updateServerLabel();
		
		JButton leaveServerButton = new JButton("Leave Server");
		leftTopPanel.add(leaveServerButton, BorderLayout.EAST);
		leaveServerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connection.close();
				launcher.back();
			}
		});
		
		JPanel leftBottomPanel = new JPanel();
		leftBottomPanel.setBorder(null);
		FlowLayout flowLayout = (FlowLayout) leftBottomPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		leftPanel.add(leftBottomPanel, BorderLayout.SOUTH);
		
		newMatchButton = new JButton("New Match");
		newMatchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String comment = JOptionPane.showInputDialog(NetworkFrame.this, "You may enter a comment on the new match:", "Create New Match", JOptionPane.QUESTION_MESSAGE);
				if (comment != null) {
					connection.createMatch(comment);
					newMatchButton.setEnabled(false);
					joinMatchButton.setEnabled(false);
					leaveMatchButton.setEnabled(true);
					startMatchButton.setEnabled(true);
					cancelMatchButton.setEnabled(true);
				}
			}
		});
		leftBottomPanel.add(newMatchButton);
		
		joinMatchButton = new JButton("Join Match");
		joinMatchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = matchTable.getSelectedRow();
				if (index >= 0 && index < connection.getOpenMatches().getSize()) {
					Match match = connection.getOpenMatches().getElementAt(index);
					connection.joinMatch(match);
					joinMatchButton.setEnabled(false);
					newMatchButton.setEnabled(false);
					leaveMatchButton.setEnabled(true);
					startMatchButton.setEnabled(match.getOwner().equals(connection.getPlayer()));
					cancelMatchButton.setEnabled(match.getOwner().equals(connection.getPlayer()));
				}
			}
		});
		joinMatchButton.setEnabled(false);
		leftBottomPanel.add(joinMatchButton);
		
		leaveMatchButton = new JButton("Leave Match");
		leaveMatchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (connection.getPlayer().getCurrentMatch() != null) {
					connection.leaveMatch();
					joinMatchButton.setEnabled(isOpenMatchSelected());
					leaveMatchButton.setEnabled(false);
					newMatchButton.setEnabled(true);
					startMatchButton.setEnabled(false);
					cancelMatchButton.setEnabled(false);
				}
			}
		});
		leaveMatchButton.setEnabled(false);
		leftBottomPanel.add(leaveMatchButton);
		
		startMatchButton = new JButton("Start Match");
		startMatchButton.setEnabled(false);
		startMatchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connection.startMatch();
				startMatchButton.setEnabled(false);
			}
		});
		leftBottomPanel.add(startMatchButton);
		
		cancelMatchButton = new JButton("Cancel Match");
		cancelMatchButton.setEnabled(false);
		cancelMatchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connection.cancelMatch();
				startMatchButton.setEnabled(false);
				cancelMatchButton.setEnabled(false);
				newMatchButton.setEnabled(true);
				joinMatchButton.setEnabled(isOpenMatchSelected());
				leaveMatchButton.setEnabled(false);
			}
		});
		leftBottomPanel.add(cancelMatchButton);
		
		connection.onStateChanged.addListener(new EventListener<EventArgs>() {
			@Override
			public void call(Object sender, EventArgs param) {
				updateServerLabel();
			}
		});
		connection.onNetworkError.addListener(new EventListener<ExceptionEventArgs>() {
			public void call(Object sender, ExceptionEventArgs param) {
				JOptionPane.showMessageDialog(NetworkFrame.this, param.getException().getMessage(), "Network Error", JOptionPane.ERROR_MESSAGE);
				setVisible(false);
			}
		});
		connection.onMessageReceived.addListener(new EventListener<MessageEventArgs>() {
			@Override
			public void call(Object sender, MessageEventArgs param) {
				logChatMessage(param.getPlayer(), param.getMessage());
			}
		});
		
		connection.onMatchStarted.addListener(new EventListener<MatchStartedEventArgs>() {
			@Override
			public void call(Object sender, MatchStartedEventArgs param) {
				getLauncher().setPage(new GameFrame(launcher, param.getWorld()));
			}
		});
		
		// Calls updateButtonsEnabled() if required
		Observer observer = new UpdateObserver();
		connection.getOpenMatches().addObserver(observer);
		connection.getOtherPlayers().addObserver(observer);
		connection.getPlayer().addObserver(observer);
	}
	
	private void sendChatMessage() {
		String text = chatInputField.getText().trim();
		if (!text.trim().equals("")) {
			connection.sendMessage(text);
			chatInputField.setText("");
			// message is logged when back-received via passive socket
		}
	}
		
	private void logChatMessage(String player, String message) {
		chatField.setText(String.format("%s%s: %s\n", chatField.getText(), player, message ));
	}
	
	private void updateServerLabel() {
		serverLabel.setText(getConnectionStatus());
	}
	
	private boolean isOpenMatchSelected() {
		if (matchTable.getSelectedRowCount() == 1) {
			int index = matchTable.getSelectedRow();
			if (index >= 0 && index < connection.getOpenMatches().getSize()) {
				Match match = connection.getOpenMatches().getElementAt(index);
				return match.getState() == MatchState.OPEN;
			}
		}
		return false;
	}
	
	private void updateButtonsEnabled() {
		Match currentMatch = connection.getPlayer().getCurrentMatch();
		newMatchButton.setEnabled(currentMatch == null);
		joinMatchButton.setEnabled(currentMatch == null && isOpenMatchSelected());
		leaveMatchButton.setEnabled(currentMatch != null);
		startMatchButton.setEnabled(
			currentMatch != null && currentMatch.getOwner().equals(connection.getPlayer())
			&& currentMatch.getState() == MatchState.OPEN);
		cancelMatchButton.setEnabled(currentMatch != null && currentMatch.getOwner().equals(connection.getPlayer()));
	}
	
	private class UpdateObserver implements Observer {
		public void update(Observable o, Object arg) {
			updateButtonsEnabled();
		}
	}
	
	private String getConnectionStatus() {
		switch (connection.getState()) {
		case CONNECTED:
			return String.format("At %s as %s", connection.getHost(), connection.getPlayer().getName());
		case CONNECTING:
			return "Connecting to " + connection.getHost() + "...";
		case CLOSING:
			return "Closing connection...";
		case NETWORK_ERROR:
			return "Network error!";
		default:
			return "Not connected";
		}
	}
	
	/**
	 * A list model that translates players given by a Players object to their names
	 * 
	 * @author Yogu
	 */
	private static class PlayersModel implements ListModel<String> {
		private Players players;
		
		public PlayersModel(Players players) {
			this.players = players;
		}

		@Override
		public void addListDataListener(ListDataListener l) {
			players.addListDataListener(l);
		}

		@Override
		public String getElementAt(int index) {
			if (index < players.getSize())
				return players.getElementAt(index).getName();
			else
				return "";
		}

		@Override
		public int getSize() {
			return players.getSize();
		}

		@Override
		public void removeListDataListener(ListDataListener l) {
			players.removeListDataListener(l);
		}
	}
	
	/**
	 * A table model that translates matches given by a Matches object to information interesting for
	 * the user
	 * 
	 * @author Yogu
	 */
	private static class MatchesModel extends AbstractTableModel implements ListDataListener {
		private static final long serialVersionUID = -1626476931065129495L;
		private static final String[] columns = new String[] { "ID", "Creator", "Comment", "Players", "State" };
		private Matches matches;
		
		public MatchesModel(Matches matches) {
			this.matches = matches;
			matches.addListDataListener(this);
		}
		
		@Override
		public int getColumnCount() {
			return columns.length;
		}
		
		@Override
		public String getColumnName(int column) {
			return columns[column];
		}

		@Override
		public int getRowCount() {
			return matches.getSize();
		}

		@Override
		public Object getValueAt(int row, int column) {
			if (row >= matches.getSize())
				return "";
			Match match = (Match) matches.getElementAt(row);
			if (match == null)
				return "";
			switch (column) {
			case 0:
				return match.getID();
			case 1:
				return match.getOwner().getName();
			case 2:
				return match.getComment();
			case 3:
				String text = "";
				for (Player player : match.getPlayers()) {
					if (text != "")
						text += ", ";
					text += player.getName();
				}
				return text;
			case 4:
				return match.getState().toString();
			default:
					return "";
			}
		}

		@Override
		public void contentsChanged(ListDataEvent e) {
			fireTableRowsUpdated(e.getIndex0(), e.getIndex1());
			
		}

		@Override
		public void intervalAdded(ListDataEvent e) {
			fireTableRowsInserted(e.getIndex0(), e.getIndex1());
		}

		@Override
		public void intervalRemoved(ListDataEvent e) {
			fireTableRowsDeleted(e.getIndex0(), e.getIndex1());
		}
	}
}

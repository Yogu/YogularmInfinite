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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
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
import de.yogularm.network.Matches;
import de.yogularm.network.Player;
import de.yogularm.network.Players;
import de.yogularm.network.client.GameConnection;
import de.yogularm.network.client.MessageEventArgs;

import javax.swing.ListSelectionModel;

public class NetworkFrame extends JFrame {
	private GameConnection connection;
	
	private JPanel contentPane;
	private JTextField chatInputField;
	private JButton sendChatMessageButton;

	private JTextArea chatField;

	private JLabel serverLabel;

	private JButton newMatchButton;

	private JButton joinMatchButton;

	private JButton leaveMatchButton;

	private JButton startMatchButton;

	private JButton cancelMatchButton;

	/**
	 * Create the frame.
	 */
	public NetworkFrame(final GameConnection connection) {
		this.connection = connection;
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

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowevent) {
				dispose();
				connection.close();
			}
		});
		
		setTitle("Yogularm Infinite - Multiplayer Mode");
		setBounds(100, 100, 767, 556);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		contentPane.add(splitPane, BorderLayout.CENTER);
		
		JPanel rightPanel = new JPanel();
		splitPane.setRightComponent(rightPanel);
		rightPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel rightBottomPanel = new JPanel();
		rightPanel.add(rightBottomPanel, BorderLayout.SOUTH);
		rightBottomPanel.setLayout(new BorderLayout(0, 0));
		
		chatInputField = new JTextField();
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
		rightBottomPanel.add(chatInputField, BorderLayout.CENTER);
		chatInputField.setText("Type to compose...");
		chatInputField.setColumns(10);
		
		sendChatMessageButton = new JButton("Send");
		sendChatMessageButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendChatMessage();
			}
		});
		rightBottomPanel.add(sendChatMessageButton, BorderLayout.EAST);
		
		JSplitPane rightSplitPane = new JSplitPane();
		rightSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		rightPanel.add(rightSplitPane, BorderLayout.CENTER);
		
		JScrollPane playersScrollPane = new JScrollPane();
		rightSplitPane.setLeftComponent(playersScrollPane);
		
		JList playerList = new JList(new PlayersModel(connection.getOtherPlayers()));
		playersScrollPane.setViewportView(playerList);
		
		JScrollPane chatScrollPane = new JScrollPane();
		rightSplitPane.setRightComponent(chatScrollPane);
		
		chatField = new JTextArea();
		chatField.setEditable(false);
		chatScrollPane.setViewportView(chatField);
		
		JLabel lblNewLabel = new JLabel("Other Players on this server:");
		rightPanel.add(lblNewLabel, BorderLayout.NORTH);
		
		JPanel leftPanel = new JPanel();
		splitPane.setLeftComponent(leftPanel);
		leftPanel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane matchesScrollPane = new JScrollPane();
		leftPanel.add(matchesScrollPane, BorderLayout.CENTER);
		
		final JTable matchTable = new JTable(new MatchesModel(connection.getOpenMatches()));
		matchTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		matchesScrollPane.setViewportView(matchTable);
		matchTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				boolean selected = matchTable.getSelectedRowCount() > 0;
				joinMatchButton.setEnabled(connection.getPlayer().getCurrentMatch() == null && selected);
			}
		});
		
		JPanel leftTopPanel = new JPanel();
		leftPanel.add(leftTopPanel, BorderLayout.NORTH);
		leftTopPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		serverLabel = new JLabel("Connected to Server");
		leftTopPanel.add(serverLabel);
		updateServerLabel();
		
		JButton leaveServerButton = new JButton("Leave Server");
		leftTopPanel.add(leaveServerButton);
		
		JPanel leftBottomPanel = new JPanel();
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
					joinMatchButton.setEnabled(matchTable.getSelectedRowCount() > 0);
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
				joinMatchButton.setEnabled(matchTable.getSelectedRowCount() > 0);
				leaveMatchButton.setEnabled(false);
			}
		});
		leftBottomPanel.add(cancelMatchButton);
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
		chatField.setText(chatField.getText() + "\n" + player + ": " + message);
	}
	
	private void updateServerLabel() {
		serverLabel.setText(getConnectionStatus());
	}
	
	private String getConnectionStatus() {
		switch (connection.getState()) {
		case CONNECTED:
			return "Connected to " + connection.getHost();
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
	private static class PlayersModel implements ListModel {
		private Players players;
		
		public PlayersModel(Players players) {
			this.players = players;
		}

		@Override
		public void addListDataListener(ListDataListener l) {
			players.addListDataListener(l);
		}

		@Override
		public Object getElementAt(int index) {
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

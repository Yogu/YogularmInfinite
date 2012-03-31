package de.yogularm.desktop;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import de.yogularm.network.Player;
import de.yogularm.network.client.GameConnection;

public class NetworkFrame extends JFrame {
	private GameConnection connection;
	
	private JPanel contentPane;
	private JTextField chatInputField;
	private JButton sendChatMessageButton;

	private JTextArea chatField;

	private JLabel serverLabel;

	/**
	 * Create the frame.
	 */
	public NetworkFrame(GameConnection connection) {
		setTitle("Yogularm Infinite - Multiplayer Mode");
		this.connection = connection;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 660, 539);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		contentPane.add(splitPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		splitPane.setRightComponent(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_3 = new JPanel();
		panel.add(panel_3, BorderLayout.SOUTH);
		panel_3.setLayout(new BorderLayout(0, 0));
		
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
		panel_3.add(chatInputField, BorderLayout.CENTER);
		chatInputField.setText("Type to compose...");
		chatInputField.setColumns(10);
		
		sendChatMessageButton = new JButton("Send");
		sendChatMessageButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendChatMessage();
			}
		});
		panel_3.add(sendChatMessageButton, BorderLayout.EAST);
		
		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		panel.add(splitPane_1, BorderLayout.CENTER);
		
		JScrollPane scrollPane = new JScrollPane();
		splitPane_1.setLeftComponent(scrollPane);
		
		JList playerList = new JList();
		scrollPane.setViewportView(playerList);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		splitPane_1.setRightComponent(scrollPane_1);
		
		chatField = new JTextArea();
		scrollPane_1.setViewportView(chatField);
		
		JLabel lblNewLabel = new JLabel("Other Players on this server:");
		panel.add(lblNewLabel, BorderLayout.NORTH);
		
		JPanel panel_1 = new JPanel();
		splitPane.setLeftComponent(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane_2 = new JScrollPane();
		panel_1.add(scrollPane_2, BorderLayout.CENTER);
		
		JList matchList = new JList();
		scrollPane_2.setViewportView(matchList);
		
		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2, BorderLayout.NORTH);
		panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		serverLabel = new JLabel("Connected to Server");
		panel_2.add(serverLabel);
		updateServerLabel();
				
		JButton newMatchButton = new JButton("New Match");
		panel_2.add(newMatchButton);
		
		JButton leaveServerButton = new JButton("Leave Server");
		panel_2.add(leaveServerButton);
		
		initGameConnection();
	}
	
	private void sendChatMessage() {
		String text = chatInputField.getText().trim();
		if (!text.trim().equals("")) {
			connection.sendMessage(text);
			chatInputField.setText("");
			logChatMessage(connection.getPlayer(), text);
		}
	}
		
	private void logChatMessage(Player player, String message) {
		chatField.setText(chatField.getText() + "\n" + player.getName() + ": " + message);
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
	
	private void initGameConnection() {
		
	}
}

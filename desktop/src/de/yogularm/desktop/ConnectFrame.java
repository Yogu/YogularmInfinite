package de.yogularm.desktop;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetSocketAddress;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;

import de.yogularm.event.EventArgs;
import de.yogularm.event.EventListener;
import de.yogularm.event.ExceptionEventArgs;
import de.yogularm.network.Player;
import de.yogularm.network.client.GameConnection;

public class ConnectFrame {
	private JFrame window;
	private JComponent panel;
	private JButton okButton;
	private JButton cancelButton;
	private JTextField hostField;
	private JTextField portField;
	private JTextField nameField;
	private GameConnection connection;

	private static Object lock = new Object();
	private static ConnectFrame instance;

	private static final int DEFAULT_PORT = 62602;

	public ConnectFrame() {
		instance = this;
		init();
	}

	public static void open() {
		synchronized (lock) {
			if (instance == null)
				instance = new ConnectFrame();
			else if (instance.window != null)
				instance.window.toFront();
		}
	}

	private void init() {
		window = new JFrame("Yogularm Infinite - Multiplayer Mode");
		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowevent) {
				close();
			}
		});
		window.setSize(400, 300);

		createLoginPanel();
		window.getContentPane().add(panel);

		window.setVisible(true);
	}

	private void createLoginPanel() {
		// ------ buttons ------
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		okButton = new JButton("OK");
		cancelButton = new JButton("Cancel");
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(okButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(cancelButton);

		// ------ input fields ------
		panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 1;
		c.weighty = 0;
		c.gridy = 0;
		c.insets = new Insets(10, 10, 10, 10);

		JTextArea l = new JTextArea("Enter hostname (ip-address) and port of the Yogularm Infinite "
				+ "server and chose your nickname.");
		makeMultilineLabel(l);
		c.gridwidth = 2;
		panel.add(l, c);
		c.gridwidth = 1;

		c.gridy++;
		JLabel label = new JLabel("Host / IP:");
		c.gridx = 0;
		c.weightx = 1;
		panel.add(label, c);
		hostField = new JTextField("localhost");
		c.gridx = 1;
		c.weightx = 2;
		panel.add(hostField, c);

		c.gridy++;
		label = new JLabel("Port:");
		c.gridx = 0;
		c.weightx = 1;
		panel.add(label, c);
		portField = new JTextField(DEFAULT_PORT + "");
		c.gridx = 1;
		c.weightx = 2;
		panel.add(portField, c);

		c.gridy++;
		label = new JLabel("Your Name:");
		c.gridx = 0;
		c.weightx = 1;
		panel.add(label, c);
		nameField = new JTextField("");
		c.gridx = 1;
		c.weightx = 2;
		panel.add(nameField, c);

		c.gridy++;
		c.gridx = 0;
		c.weighty = 1;
		c.gridwidth = 2;
		c.weightx = 1;
		panel.add(Box.createGlue(), c);

		c.gridy++;
		c.weighty = 0;
		panel.add(buttonPanel, c);

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				close();
			}
		});

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String host = hostField.getText().trim();
				String portStr = portField.getText().trim();
				String name = nameField.getText().trim();

				if (host.equals("")) {
					showError(hostField, "Enter the server's hostname or ip address.");
					return;
				}

				if (portStr.equals("")) {
					showError(portField, "Enter the server's port.");
					return;
				}

				int port;
				try {
					port = Integer.parseInt(portStr);
				} catch (NumberFormatException e) {
					showError(portField, "The port must be a valid number.");
					return;
				}

				if (port <= 0 || port > 65535) {
					showError(portField, "The port must be a positive number smaller than 65536.");
					return;
				}

				if (name.equals("")) {
					showError(nameField, "Please chose a name.");
					return;
				}

				if (!Player.isValidName(name)) {
					showError(portField,
							"The name must neither contain spaces nor any special characters except _ and -");
					return;
				}

				enableControls(false);

				connection = new GameConnection(host, port, name);
				connection.onStateChanged.addListener(new EventListener<EventArgs>() {
					public void call(Object sender, EventArgs param) {
						switch (connection.getState()) {
						case CONNECTED:
							close();
							new NetworkFrame(connection).setVisible(true);
							break;
						case NAME_NOT_AVAILABLE:
							JOptionPane.showMessageDialog(window, "This name is already taken by another user. Please choose a different one.");
							nameField.selectAll();
							enableControls(true);
							nameField.requestFocusInWindow();
							break;
						}
					}
				});
				EventListener<ExceptionEventArgs> listener = new EventListener<ExceptionEventArgs>() {
					public void call(Object sender, ExceptionEventArgs param) {
						enableControls(true);
						showError(hostField, "Could not reach server. Check hostname, port and internet connection.");
					}
				};
				connection.onNetworkError.addListener(listener);
				try {
					connection.start();
				} finally {
					connection.onNetworkError.removeListener(listener);
				}
			}
		});
	}

	private void close() {
		window.dispose();
		instance = null;
	}

	private void enableControls(boolean enable) {
		okButton.setEnabled(enable);
		hostField.setEnabled(enable);
		portField.setEnabled(enable);
		nameField.setEnabled(enable);
	}

	private void showError(JTextComponent component, String error) {
		JOptionPane.showMessageDialog(window, error, "Error", JOptionPane.ERROR_MESSAGE);
		if (component != null) {
			component.selectAll();
			component.requestFocusInWindow();
		}
	}

	public static void makeMultilineLabel(JTextComponent area) {
		area.setFont(UIManager.getFont("Label.font"));
		area.setEditable(false);
		area.setOpaque(false);
		area.setCursor(null);
		area.setFocusable(false);
		if (area instanceof JTextArea) {
			((JTextArea) area).setWrapStyleWord(true);
			((JTextArea) area).setLineWrap(true);
		}
	}
}

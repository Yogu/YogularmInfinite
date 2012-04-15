package de.yogularm.desktop;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.yogularm.event.EventArgs;
import de.yogularm.event.EventListener;
import de.yogularm.event.ExceptionEventArgs;
import de.yogularm.multiplayer.Player;
import de.yogularm.network.ServerGlobals;
import de.yogularm.network.client.GameConnection;

public class StartFrame extends Page {
	private static final long serialVersionUID = 1078377108661128420L;
	
	private JTextField hostField;
	private JTextField portField;
	private JTextField nameField;
	private JButton connectButton;

	private boolean isCancelled;

	private JButton cancelButton;

	/**
	 * Create the panel.
	 */
	public StartFrame(final SwingLauncher launcher) {
		super(launcher);
		
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 450 };
		gbl_panel.rowHeights = new int[] { 62, 149 };
		gbl_panel.columnWeights = new double[] { 1.0 };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0 };
		panel.setLayout(gbl_panel);

		JPanel multiPlayerPanel = new JPanel();
		multiPlayerPanel.setBorder(new TitledBorder(null, "Multi Player", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		GridBagConstraints gbc_multiPlayerPanel = new GridBagConstraints();
		gbc_multiPlayerPanel.weighty = 1.0;
		gbc_multiPlayerPanel.insets = new Insets(0, 0, 5, 0);
		gbc_multiPlayerPanel.fill = GridBagConstraints.BOTH;
		gbc_multiPlayerPanel.gridx = 0;
		gbc_multiPlayerPanel.gridy = 1;
		panel.add(multiPlayerPanel, gbc_multiPlayerPanel);
		multiPlayerPanel.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), }, new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("default:grow"), }));

		JLabel hostLabel = new JLabel("Host / IP:");
		multiPlayerPanel.add(hostLabel, "2, 2, right, default");

		hostField = new JTextField(ServerGlobals.DEFAULT_HOST);
		multiPlayerPanel.add(hostField, "4, 2, fill, default");
		hostField.setColumns(10);

		JLabel portLabel = new JLabel("Port:");
		multiPlayerPanel.add(portLabel, "2, 4, right, default");

		portField = new JTextField(ServerGlobals.DEFAULT_PORT + "");
		multiPlayerPanel.add(portField, "4, 4, fill, default");
		portField.setColumns(10);

		JLabel nameLabel = new JLabel("Your Name:");
		multiPlayerPanel.add(nameLabel, "2, 6, right, default");

		nameField = new JTextField(generateName());
		multiPlayerPanel.add(nameField, "4, 6, fill, default");
		nameField.setColumns(10);

		JPanel multiplayerButtonsPanel = new JPanel();
		FlowLayout fl_multiplayerButtonsPanel = (FlowLayout) multiplayerButtonsPanel.getLayout();
		fl_multiplayerButtonsPanel.setVgap(0);
		fl_multiplayerButtonsPanel.setHgap(0);
		fl_multiplayerButtonsPanel.setAlignment(FlowLayout.LEFT);
		multiPlayerPanel.add(multiplayerButtonsPanel, "4, 8, fill, fill");

		connectButton = new JButton("Connect");
		multiplayerButtonsPanel.add(connectButton);

		cancelButton = new JButton("Cancel");
		cancelButton.setVisible(false);
		multiplayerButtonsPanel.add(cancelButton);
		
		isCancelled = false;

		connectButton.addActionListener(new ActionListener() {
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

				isCancelled = false;
				final GameConnection connection = new GameConnection(host, port, name);
				final EventListener<ExceptionEventArgs> errorListener = new EventListener<ExceptionEventArgs>() {
					public void call(Object sender, ExceptionEventArgs param) {
						if (!isCancelled) {
							enableControls(true);
							String message;
							if (param.getException() instanceof UnknownHostException)
								message = "Unknown host. Please check the spelling and try again.";
							else
								message = "Could not reach server. Check hostname, port and internet connection.";
							showError(hostField, message);
						}
					}
				};
				connection.onStateChanged.addListener(new EventListener<EventArgs>() {
					public void call(Object sender, EventArgs param) {
						if (isCancelled)
							connection.close();
						else {
							switch (connection.getState()) {
							case CONNECTED:
								connection.onNetworkError.removeListener(errorListener);
								launcher.setPage(new NetworkFrame(launcher, connection));
								break;
							case NAME_NOT_AVAILABLE:
								JOptionPane.showMessageDialog(StartFrame.this,
										"This name is already taken by another user. Please choose a different one.");
								nameField.selectAll();
								enableControls(true);
								nameField.requestFocusInWindow();
								break;
							}
						}
					}
				});
				connection.onNetworkError.addListener(errorListener);
				connection.start();
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				isCancelled = true;
				enableControls(true);
			}
		});

		JPanel singlePlayerPanel = new JPanel();
		singlePlayerPanel.setBorder(new TitledBorder(null, "Single Player", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		GridBagConstraints gbc_singlePlayerPanel = new GridBagConstraints();
		gbc_singlePlayerPanel.fill = GridBagConstraints.BOTH;
		gbc_singlePlayerPanel.gridx = 0;
		gbc_singlePlayerPanel.gridy = 0;
		panel.add(singlePlayerPanel, gbc_singlePlayerPanel);

		JButton singlePlayerButton = new JButton("Start Game");
		singlePlayerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				launcher.setPage(new GameFrame(launcher));
			}
		});
		singlePlayerPanel.add(singlePlayerButton);
	}

	private void showError(JTextComponent component, String error) {
		JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
		if (component != null) {
			component.selectAll();
			component.requestFocusInWindow();
		}
	}

	private void enableControls(boolean enable) {
		connectButton.setVisible(enable);
		cancelButton.setVisible(!enable);
		hostField.setEnabled(enable);
		portField.setEnabled(enable);
		nameField.setEnabled(enable);
	}
	
	private String generateName() {
		Random random = new Random();
		return String.format("Player%03d", random.nextInt(1000));
	}
	
	public void onShown() {
		super.onShown();
		enableControls(true);
	}
}

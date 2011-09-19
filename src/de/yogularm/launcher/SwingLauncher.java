package de.yogularm.launcher;

import java.awt.BorderLayout;
import java.awt.SystemColor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.management.monitor.Monitor;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import org.omg.CORBA.Environment;

import de.yogularm.ExceptionHandler;
import de.yogularm.Game;

public class SwingLauncher {
	private final static int INIT_WIDTH = 800;
	private final static int INIT_HEIGHT = 450;
	
	private JFrame window;
	private GLCanvas canvas;
	private JLabel statusLabel;
	private Game game;

	public static void main(String[] args) {
		System.out.println("Yoglarm started");
		SwingLauncher launcher = new SwingLauncher();
		launcher.run();
	}

	public void run() {
		// If after createWindow(), the window must be resized to show the canvas
		
		createWindow();

		setStatus("Creating Rendering Context");
		createCanvas();
		
		setStatus("Initializing Game");
		game = new Game();
		game.setExceptionHandler(new ExceptionHandler() {
			public void handleException(Throwable e) {
				SwingLauncher.this.handleException(e);
			}
		});

		setStatus("Creating Render Canvas");
		GLProfile.initSingleton(true);
		initCanvas();

		setStatus("Starting Game");
		game.start(canvas);
		
		window.setExtendedState(window.getExtendedState() | JFrame.MAXIMIZED_BOTH);

		/*window.getContentPane().remove(statusLabel);
		window.getContentPane().repaint();*/
	}
	
	private void createWindow() {
		window = new JFrame("Yogularm Infinite loading...");
		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowevent) {
				window.dispose();
				System.exit(0);
			}
		});
		window.setSize(INIT_WIDTH, INIT_HEIGHT);
		window.setVisible(true);
		
		/*statusLabel = new JLabel("Loading Yogularm...");
		statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		statusLabel.setVerticalAlignment(SwingConstants.CENTER);*/
		//window.getContentPane().add(statusLabel, BorderLayout.CENTER);
	}

	private void createCanvas() {
		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities(glprofile);
		canvas = new GLCanvas(glcapabilities);
		window.getContentPane().add(canvas, BorderLayout.CENTER);
	}
	
	private void setStatus(String status) {
		/*statusLabel.setText(status + "...");
		window.repaint();*/
	}
	
	private void initCanvas() {
		canvas.addGLEventListener(game);
		canvas.addKeyListener(game.getKeyListener());
		window.addKeyListener(game.getKeyListener());
		window.setTitle("Yogularm Infinite (Version " + Game.VERSION + ")");
	}
	
	private void handleException(Throwable e) {
			StringWriter sw = new StringWriter();
			PrintWriter writer = new PrintWriter(sw);
			e.printStackTrace(writer);
			String stackTrace = sw.toString();
			String message = String.format("Sorry, an error occured: %s\n\nPlease send this error report to " + 
				"info@yogularm.de. Thanks!\n\n%s\n\nVersion: %s", e.getMessage(), stackTrace, Game.VERSION);
			
			JTextArea text = new JTextArea(message);
			text.setEditable(false);
			text.setBackground(SystemColor.control);
			JOptionPane.showMessageDialog(window, text, "Runtime Error", JOptionPane.ERROR_MESSAGE);
			
		if (window != null)
			window.dispose();
		System.exit(1);
	}
}

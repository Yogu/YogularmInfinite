package de.yogularm.launcher;

import java.awt.BorderLayout;
import java.awt.SystemColor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import de.yogularm.ExceptionHandler;
import de.yogularm.Game;

public class SwingLauncher {
	private final static int INIT_WIDTH = 800;
	private final static int INIT_HEIGHT = 450;
	
	private JFrame window;
	private GLCanvas canvas;

	public static void main(String[] args) {
		System.out.println("Yoglarm started");
		SwingLauncher launcher = new SwingLauncher();
		launcher.run();
	}

	public void run() {
		GLProfile.initSingleton(true);
		Game game = new Game();
		game.setExceptionHandler(new ExceptionHandler() {
			public void handleException(Throwable e) {
				SwingLauncher.this.handleException(e);
			}
		});
		canvas = createWindow(game);
		game.start(canvas);
	}

	public GLCanvas createWindow(final Game game) {
		window = new JFrame("Yogularm Infinite");
		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowevent) {
				window.dispose();
				System.exit(0);
			}
		});
		window.setSize(INIT_WIDTH, INIT_HEIGHT);
		window.setVisible(true);
		window.setExtendedState(window.getExtendedState() | JFrame.MAXIMIZED_BOTH);

		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities(glprofile);
		GLCanvas canvas = new GLCanvas(glcapabilities);
		canvas.addGLEventListener(game);
		window.getContentPane().add(canvas, BorderLayout.CENTER);
		canvas.addKeyListener(game.getKeyListener());
		window.addKeyListener(game.getKeyListener());

		return canvas;
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

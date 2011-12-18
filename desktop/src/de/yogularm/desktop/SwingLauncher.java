package de.yogularm.desktop;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.ImageIcon;
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
	private boolean exceptionShown;

	public static void main(String[] args) {
		System.out.println("Yoglarm started");
		SwingLauncher launcher = new SwingLauncher();
		launcher.run();
	}

	public void run() {
		GLProfile.initSingleton(true);
		
		Game game = new Game();
		canvas = createWindow();
		
		// Input
		InputImpl input = new InputImpl();
		game.setInput(input);
		canvas.addKeyListener(input.getKeyListener());
		window.addKeyListener(input.getKeyListener());
		
		// OpenGL
		GLEventListenerImpl eventListener = new GLEventListenerImpl(game);
		canvas.addGLEventListener(eventListener);

		eventListener.setExceptionHandler(new ExceptionHandler() {
			public void handleException(Throwable e) {
				SwingLauncher.this.handleException(e);
			}
		});
		eventListener.start(canvas);
	}

	public GLCanvas createWindow() {
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
		window.getContentPane().add(canvas, BorderLayout.CENTER);

		// Load icon
		window
			.setIconImages(getIconImages(new String[] { "res/icon-16.png", "res/icon-32.png", "res/icon-48.png" }));

		return canvas;
	}

	private void handleException(Throwable e) {
		// Down't show exceptions caused by other exceptions
		if (!exceptionShown) {
			StringWriter sw = new StringWriter();
			PrintWriter writer = new PrintWriter(sw);
			e.printStackTrace(writer);
			String stackTrace = sw.toString();
			String message = String.format(
					"Sorry, an error occured: %s\n\nPlease send this error report to "
							+ "info@yogularm.de. Thanks!\n\n%s\n\nVersion: %s", e.getMessage(),
					stackTrace, Game.VERSION);
	
			JTextArea text = new JTextArea(message);
			text.setEditable(false);
			text.setBackground(SystemColor.control);
			JOptionPane.showMessageDialog(window, text, "Runtime Error",
					JOptionPane.ERROR_MESSAGE);
			exceptionShown = true;
		}

		if (window != null)
			window.dispose();
		System.exit(1);
	}

	private List<Image> getIconImages(String[] fileNames) {
		List<Image> images = new ArrayList<Image>();
		for (String fileName : fileNames) {
			images.add(new ImageIcon(getClass().getResource(fileName)).getImage());
		}
		return images;
	}
}

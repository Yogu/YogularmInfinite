package de.yogularm.desktop;

import java.awt.Image;
import java.awt.SystemColor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GLProfile;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.yogularm.Game;
import de.yogularm.utils.ArrayDeque;
import de.yogularm.utils.Deque;

public class SwingLauncher {
	private final static int INIT_WIDTH = 800;
	private final static int INIT_HEIGHT = 450;

	private JFrame frame;
	private boolean exceptionShown;
	private Deque<Page> pageStack = new ArrayDeque<Page>();

	public static void main(String[] args) {
		System.out.println("Yoglarm started");
		SwingLauncher launcher = new SwingLauncher();
		launcher.run();
	}

	public void run() {
		try {
			System.setOut(new PrintStream("yogularm.log"));
		} catch (FileNotFoundException e) {
			handleException(e);
		}
		
		GLProfile.initSingleton(true);
		initUI();
		createWindow();
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				handleException(e);
			}
		});
		
		setPage(new StartFrame(this));

		frame.setVisible(true);
	}

	public void initUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			System.out.println("Failed to load system look and feel " + e.getMessage());
		} catch (InstantiationException e) {
			System.out.println("Failed to load system look and feel " + e.getMessage());
		} catch (IllegalAccessException e) {
			System.out.println("Failed to load system look and feel " + e.getMessage());
		} catch (UnsupportedLookAndFeelException e) {
			System.out.println("Failed to load system look and feel " + e.getMessage());
		}
	}

	public void createWindow() {
		frame = new JFrame("Yogularm Infinite");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowevent) {
				frame.dispose();
				System.exit(0);
			}
		});
		frame.setSize(INIT_WIDTH, INIT_HEIGHT);
		frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);

		// Load icon
		frame.setIconImages(getIconImages(new String[] { "res/icon-16.png", "res/icon-32.png",
				"res/icon-48.png" }));
	}

	public void handleException(Throwable e) {
		// Down't show exceptions caused by other exceptions
		if (!exceptionShown) {
			StringWriter sw = new StringWriter();
			PrintWriter writer = new PrintWriter(sw);
			e.printStackTrace(writer);
			String stackTrace = sw.toString();
			String message = String.format(
					"Sorry, an error occured: %s\n\nPlease send this error report to "
							+ "info@yogularm.de. Thanks!\n\n%s\n\nVersion: %s", e.getMessage(), stackTrace,
					Game.VERSION);

			JTextArea text = new JTextArea(message);
			text.setEditable(false);
			text.setBackground(SystemColor.control);
			JOptionPane.showMessageDialog(frame, text, "Runtime Error", JOptionPane.ERROR_MESSAGE);
			exceptionShown = true;
		}

		if (frame != null)
			frame.dispose();
		System.exit(1);
	}
	
	public JFrame getFrame() {
		return frame;
	}
	
	public void setPage(Page page) {
		if (!pageStack.isEmpty())
			pageStack.peek().onHidden();
		
		pageStack.push(page);
		replacePage(page);
		frame.repaint();
		page.onShown();
	}
	
	public void back() {
		Page current = pageStack.poll();
		Page last = pageStack.peek();
		if (current != null)
			current.onHidden();
		if (last == null)
			last = new StartFrame(this);
		replacePage(last);
		last.onShown();
	}
	
	private void replacePage(Page page) {
		frame.setContentPane(page);
		frame.validate();
		frame.requestFocus();
	}

	private List<Image> getIconImages(String[] fileNames) {
		List<Image> images = new ArrayList<Image>();
		for (String fileName : fileNames) {
			images.add(new ImageIcon(getClass().getResource(fileName)).getImage());
		}
		return images;
	}
}

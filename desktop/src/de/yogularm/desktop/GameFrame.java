package de.yogularm.desktop;

import java.awt.BorderLayout;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import de.yogularm.Game;
import de.yogularm.event.ExceptionHandler;

public class GameFrame extends Page {
	private static final long serialVersionUID = -7460785031013329714L;

	private GLCanvas canvas;

	private InputImpl input;

	private GLEventListenerImpl eventListener;

	public GameFrame(final SwingLauncher launcher) {
		super(launcher);

		setLayout(new BorderLayout());
		Game game = new Game();
		canvas = createCanvas();
		add(canvas, BorderLayout.CENTER);

		input = new InputImpl(this);
		game.setInput(input);
		addKeyListener(input.getKeyListener());
		canvas.addKeyListener(input.getKeyListener());
		launcher.getFrame().addKeyListener(input.getKeyListener());

		eventListener = new GLEventListenerImpl(game);
		canvas.addGLEventListener(eventListener);

		eventListener.setExceptionHandler(new ExceptionHandler() {
			public void handleException(Throwable e) {
				launcher.handleException(e);
			}
		});

		eventListener.start(canvas);
	}

	public GLCanvas createCanvas() {
		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities(glprofile);
		GLCanvas canvas = new GLCanvas(glcapabilities);
		return canvas;
	}

	public void exit() {
		getLauncher().getFrame().removeKeyListener(input.getKeyListener());
		getLauncher().back();
	}
}

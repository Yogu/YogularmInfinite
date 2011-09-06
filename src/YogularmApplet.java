import java.applet.Applet;
import java.awt.BorderLayout;

import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import de.yogularm.Game;

public class YogularmApplet extends Applet {
	private static final long serialVersionUID = 7826318129884002178L;
	
	private Game game;
	private GLCanvas canvas;

	public void init() {
		GLProfile.initSingleton(false);
		game = new Game();
		setLayout(new BorderLayout());

		canvas = new GLCanvas();
		canvas.addGLEventListener(game);
		canvas.setSize(getSize());
		add(canvas, BorderLayout.CENTER);
		canvas.addKeyListener(game.getKeyListener());
		addKeyListener(game.getKeyListener());
	}

	public void start() {
		game.start(canvas);
	}

	public void stop() {
		game.stop();
	}

	public void destroy() {
	}
}

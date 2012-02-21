package de.yogularm.desktop;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import de.yogularm.Config;
import de.yogularm.Game;
import de.yogularm.input.DigitalInput;

public class InputImpl extends DigitalInput {

	@Override
	public boolean isLeft() {
		return isLeft;
	}

	@Override
	public boolean isRight() {
		return isRight;
	}

	@Override
	public boolean isUp() {
		return isUp;
	}

	@Override
	public boolean isDown() {
		return isDown;
	}

	private boolean isLeft;
	private boolean isRight;
	private boolean isUp;
	private boolean isDown;
	private KeyListener keyListener;
	private Game game;

	public InputImpl(Game game) {
		this.game = game;
		keyListener = new KeyListener() {
			public void keyPressed(KeyEvent e) {
				updateKey(e.getKeyCode(), true);
			}

			public void keyReleased(KeyEvent e) {
				updateKey(e.getKeyCode(), false);
			}

			public void keyTyped(KeyEvent e) {
			}
		};
	}

	public KeyListener getKeyListener() {
		return keyListener;
	}

	private void updateKey(int keyCode, boolean isPressed) {
		switch (keyCode) {
		case KeyEvent.VK_LEFT:
			isLeft = isPressed;
			break;
		case KeyEvent.VK_RIGHT:
			isRight = isPressed;
			break;
		case KeyEvent.VK_UP:
			isUp = isPressed;
			break;
		case KeyEvent.VK_DOWN:
			isDown = isPressed;
			break;
		case KeyEvent.VK_F1:
			if (isPressed)
				Config.DEBUG_DISPLAY_RENDER_INFO = !Config.DEBUG_DISPLAY_RENDER_INFO;
			break;
		case KeyEvent.VK_F2:
			if (isPressed)
				Config.DEBUG_BUILDING = !Config.DEBUG_BUILDING;
			break;
		case KeyEvent.VK_R:
			if (isPressed && Config.DEBUG_DISPLAY_RENDER_INFO)
				game.restart();
			break;
		}
	}
}

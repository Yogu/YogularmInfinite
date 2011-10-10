package de.yogularm.desktop;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import de.yogularm.input.Input;

public class InputImpl implements Input {

	@Override
	public boolean isLeft() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRight() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUp() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDown() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isLeft;
	private boolean isRight;
	private boolean isUp;
	private boolean isDown;
	private KeyListener keyListener;

	public InputImpl() {
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
		}
	}
}

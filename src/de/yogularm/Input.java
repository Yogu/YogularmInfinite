package de.yogularm;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Input {
	private boolean isLeft;
	private boolean isRight;
	private boolean isUp;
	private KeyListener keyListener;
	
	public Input() {
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
		}
	}
	
	public void affect(World world, float elapsedTime) {
		int direction = (isLeft && !isRight) ? -1 : (isRight && !isLeft) ? 1 : 0;
		world.getPlayer().setDirection(direction);
		
		if (isUp && world.getPlayer().standsOnGround())
			world.getPlayer().setSpeed(world.getPlayer().getSpeed().changeY(Config.PLAYER_JUMP_SPEED));
	}
}

package de.yogularm.android;

import de.yogularm.Game;

public class Controller {
	private static Game game;
	private static InputImpl input;
	
	private Controller() {
		
	}
	
	public static Game getGame() {
		if (game == null) {
			game = new Game();
			game.setInput(getInput());
		}
		return game;
	}
	
	public static InputImpl getInput() {
		if (input == null)
			input = new InputImpl();
		return input;
	}
}

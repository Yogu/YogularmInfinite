package de.yogularm.desktop;

import de.yogularm.Game;
import de.yogularm.drawing.RenderContext;
import de.yogularm.input.Input;

public class DummyLauncher {
	public static void main(String[] args) {
		System.out.println("Creating game");
		Game game = new Game();
		Input input = new InputImpl();
		RenderContext renderContext = new DummyRenderContext();
		game.setInput(input);
		System.out.println("Game is running");
		while (true) {
			game.update();
			game.render(renderContext);
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}

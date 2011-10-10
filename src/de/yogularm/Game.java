package de.yogularm;

import java.util.Random;

import de.yogularm.drawing.Color;
import de.yogularm.drawing.Font;
import de.yogularm.drawing.RenderContext;
import de.yogularm.drawing.RenderTransformation;
import de.yogularm.input.Input;

public class Game {
	private World world;
	private RenderContext renderContext;
	private Input input;
	private long lastFrameTime;
	private long frameCount = 0;
	private float frameTime = 0;
	private Vector viewSize = Vector.getZero();
	private int width;
	private int height;
	private boolean isGameover = false;
	private float gameoverTime = 0;
	
	private static final Color CLEAR_COLOR = new Color(0.8f, 0.8f, 1, 1);
	public static final String VERSION = "0.2";
	
	public Game(RenderContext renderContext, Input input) {
		if (renderContext == null)
			throw new NullPointerException("renderContext is null");
		if (input == null)
			throw new NullPointerException("input is null");
		
		this.renderContext = renderContext;
		this.input = input;
		
		Res.init(renderContext);
		restart();
	}
	
	public void setResolution(int width, int height) {
		// limit to maximum block counts in each direction
		float resolution = Math.max((float) width / Config.MAX_VIEW_WIDTH, (float) height / Config.MAX_VIEW_HEIGHT);
		// resolution = Math.max(resolution, Config.MIN_RESOLUTION);
		float w = width / resolution;
		float h = height / resolution;
		
		renderContext.setProjection(w, h);
		viewSize = new Vector(w, h);
		world.getCamera().setBounds(world.getCamera().getBounds().changeSize(viewSize));
		this.width = width;
		this.height = height;
	}
	
	public void update() {
		captureFrameTime();

		if (!isGameover)
			doTick();
		else {
			gameoverTime -= frameTime;
			if (gameoverTime < 0)
				restart();
		}
		
		frameCount++;
	}
	
	public void render() {
		renderContext.clear(CLEAR_COLOR);
		world.render(renderContext);
		renderGUI();
	}

	private void doTick() {
		world.update(frameTime);
		applyInput();
		if (world.getPlayer().isDead()) {
			gameoverTime = Config.GAMEOVER_LENGTH;
			isGameover = true;
		}
	}

	private void renderGUI() {
		renderContext.setProjection(width, height);
		renderContext.setColor(Color.white);
		renderContext.resetTranformation();
		Font font = renderContext.loadFont();
		
		// Coins + Life (text)
		renderContext.setColor(new Color(0, 0, 0, 0.8f));
		renderContext.drawText(new Vector(80, height - 60), font, "" + world.getPlayer().getCollectedCoins());
		int life = Math.max(0, Math.round(world.getPlayer().getLife() - 1));
		renderContext.drawText(new Vector(80, height - 130), font,  "" + life);

		// Coins + life (icons)
		RenderTransformation.draw(renderContext, Res.images.coin, 20, height - 70, 50, 50);
		RenderTransformation.draw(renderContext, Res.images.heart, 20, height - 140, 50, 50);

		// Game Over screen
		if (isGameover) {
			renderContext.setColor(new Color(0, 0, 0, 0.5f));
			renderContext.unbindTexture();
			renderContext.drawRect(new Rect(0, 0, width, height));
			renderContext.setColor(Color.white);

			renderContext.drawText(new Vector(width / 2 - 130, height / 2 - 20), font, "GAME OVER");
		}
		
		// Title
		renderContext.setColor(Color.black);
		Font font2 = renderContext.loadFont();
		renderContext.drawText(new Vector(width - 165, height - 200), font2, "Yogularm Infinite " + VERSION);

		renderContext.endTransformation();
	}

	private void captureFrameTime() {
		long newTime = System.nanoTime();
		if (lastFrameTime != 0)
			frameTime = (newTime - lastFrameTime) / 1000000000.0f; // ns to s
		lastFrameTime = newTime;
		// System.out.println((1 / frameTime) + " FPS");
		frameTime = Math.min(frameTime, Config.MAX_FRAMETIME);
	}

	private void restart() {
		world = new World(new Random().nextInt());
		world.getCamera().setBounds(world.getCamera().getBounds().changeSize(viewSize));
		gameoverTime = 0;
		isGameover = false;
	}

	private void applyInput() {
		int direction = (input.isLeft() && !input.isRight()) ? -1 : (input.isRight() && !input.isLeft()) ? 1 : 0;
		world.getPlayer().setDirection(direction);

		if (input.isUp() && !world.getPlayer().isClimbing())
			world.getPlayer().jump();

		if (world.getPlayer().isClimbing()) {
			direction = (input.isDown() && !input.isUp()) ? -1 : (input.isUp() && !input.isDown()) ? 1 : 0;
			world.getPlayer().setClimbSpeed(direction * Config.PLAYER_CLIMB_SPEED);
		}
	}
}

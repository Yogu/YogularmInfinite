package de.yogularm;

import java.util.EnumSet;

import de.yogularm.drawing.Color;
import de.yogularm.drawing.Font;
import de.yogularm.drawing.FontStyle;
import de.yogularm.drawing.RenderContext;
import de.yogularm.drawing.RenderTransformation;
import de.yogularm.drawing.TextDrawable;
import de.yogularm.geometry.Rect;
import de.yogularm.geometry.Vector;
import de.yogularm.input.Input;

public class Game {
	private World world;
	private Input input;
	private Vector viewSize = new Vector(1, 1);
	private int width;
	private int height;
	private boolean isGameover = false;
	private float gameoverTime = 0;

	private long lastFrameTime;
	private long frameCount = 0;
	private float frameTime = 0;
	private long updateTime;
	private long renderTime;

	private static final Color CLEAR_COLOR = new Color(0.8f, 0.8f, 1, 1);
	public static final String VERSION = "0.4.0*";
	
	public Game() {
		// Init world
		restart();
	}

	public void setInput(Input input) {
		this.input = input;
	}

	public void setResolution(RenderContext context, int width, int height) {
		// limit to maximum block counts in each direction
		float resolution = Math.max((float) width / Config.MAX_VIEW_WIDTH, (float) height
				/ Config.MAX_VIEW_HEIGHT);
		// resolution = Math.max(resolution, Config.MIN_RESOLUTION);
		float w = width / resolution;
		float h = height / resolution;

		context.setProjection(w, h);
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

	public void render(RenderContext context) {
		long time = System.nanoTime();
		context.clear(CLEAR_COLOR);
		world.render(context);
		renderGUI(context);
		renderTime = System.nanoTime() - time;
	}

	private void doTick() {
		long time = System.nanoTime();

		world.update(frameTime);
		if (input != null)
			applyInput();
		if (world.getPlayer().isDead()) {
			gameoverTime = Config.GAMEOVER_LENGTH;
			isGameover = true;
		}

		updateTime = System.nanoTime() - time;
	}

	private void renderGUI(RenderContext context) {
		context.setProjection(width, height);
		context.setColor(Color.white);
		context.resetTranformation();
		Font font =  new Font(40, EnumSet.of(FontStyle.BOLD, FontStyle.ITALIC));

		// Coins
		RenderTransformation.draw(context, Res.images.coin, 20, height - 70, 50, 50);
		TextDrawable.draw(context, "" + world.getPlayer().getCollectedCoins(), 70, height - 70, 50);

		// Life
		RenderTransformation.draw(context, Res.images.heart, 20, height - 140, 50, 50);
		int life = Math.max(0, Math.round(world.getPlayer().getLife() - 1));
		TextDrawable.draw(context, "" + life, 70, height - 140, 50);

		// Game Over screen
		if (isGameover) {
			context.setColor(new Color(0, 0, 0, 0.5f));
			context.unbindTexture();
			context.drawRect(new Rect(0, 0, width, height));
			context.setColor(Color.white);

			context.drawText(new Vector(width / 2 - 130, height / 2 - 20), font, "GAME OVER");
		}

		// Title
		context.setColor(Color.black);

		Font font2 =  new Font(12, EnumSet.of(FontStyle.BOLD));
		context.drawText(new Vector(width - 165, height - 20), font2, "Yogularm Infinite " + VERSION);

		// Debug
		if (Config.DEBUG_DISPLAY_RENDER_INFO) {
			context.drawText(new Vector(10, 40), font2, String.format(
					"%.0f FPS;    Total Time: %.3f ms;    Update: %.3f ms;    Render: %.3f ms", 1 / frameTime,
					frameTime * 1000, updateTime / 1000000f, renderTime / 1000000f));
			context.drawText(new Vector(10, 25), font2, String.format(
					"Components: %d;     Rendered: %d;     Updated: %d;     Checked: %d", world.getComponents()
							.getCount(), world.renderCount, world.updateCount, world.inRangeCount));
			context.drawText(new Vector(10, 10), font2, "Player: " + world.getPlayer().getPosition());
		}

		context.setProjection(viewSize.getX(), viewSize.getY());
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
		world = new World();
		world.getCamera().setBounds(world.getCamera().getBounds().changeSize(viewSize));
		gameoverTime = 0;
		isGameover = false;
		System.gc();
	}

	private void applyInput() {
		float direction = input.getXControl();
		world.getPlayer().setDirection(direction);

		if (input.getYControl() > 0 && !world.getPlayer().isClimbing())
			world.getPlayer().jump();

		if (world.getPlayer().isClimbing()) {
			direction = input.getYControl();
			world.getPlayer().setClimbSpeed(direction * Config.PLAYER_CLIMB_SPEED);
		}
	}
}

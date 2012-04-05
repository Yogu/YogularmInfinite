package de.yogularm;

import java.util.EnumSet;

import de.yogularm.building.BuildingSite;
import de.yogularm.components.Component;
import de.yogularm.components.LocalWorld;
import de.yogularm.components.World;
import de.yogularm.drawing.Color;
import de.yogularm.drawing.Font;
import de.yogularm.drawing.FontStyle;
import de.yogularm.drawing.RenderContext;
import de.yogularm.drawing.RenderTransformation;
import de.yogularm.drawing.Renderable;
import de.yogularm.drawing.Renderer;
import de.yogularm.drawing.TextDrawable;
import de.yogularm.geometry.Point;
import de.yogularm.geometry.Rect;
import de.yogularm.geometry.Vector;
import de.yogularm.input.Input;
import de.yogularm.utils.ValueSmoothener;

public class Game {
	private World world;
	private Camera camera;
	private Input input;
	private Vector viewSize = new Vector(1, 1);
	private int width;
	private int height;
	private boolean isGameover = false;
	private float gameoverTime = 0;

	private long lastFrameTime;
	private float frameTime;
	@SuppressWarnings("unused")
  private long frameCount = 0;
	private ValueSmoothener smoothFrameTime = new ValueSmoothener(SMOOTH_TIME);
	private ValueSmoothener smoothUpdateTime = new ValueSmoothener(SMOOTH_TIME);
	private ValueSmoothener smoothRenderTime = new ValueSmoothener(SMOOTH_TIME);
	private int renderCount;
	private int updateCount;

	private static final Color CLEAR_COLOR = new Color(0.8f, 0.8f, 1, 1);
	private static final double SMOOTH_TIME = 0.33f; // [second]
	private static final int RENDER_RANGE_BUFFER = 4;
	
	public static final String VERSION = "0.4";
	
	public Game(World world) {
		startWorld(world);
	}
	
	public Game() {
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
		camera.setBounds(camera.getBounds().changeSize(viewSize));
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
		context.clear(Config.DEBUG_BUILDING ? Color.white : CLEAR_COLOR);
		renderWorld(context);
		renderGUI(context);
		smoothRenderTime.set((System.nanoTime() - time) / 1000000000.0); // ns to s
	}

	private void doTick() {
		long time = System.nanoTime();

		Rect actionRange = camera.getBounds().changeSize(camera.getBounds().getSize().multiply(2));
		world.update(frameTime, actionRange);
		camera.scroll(world.getPlayer().getOuterBounds().getCenter(), frameTime);
		
		if (input != null)
			applyInput();
		if (world.getPlayer().isDead()) {
			gameoverTime = Config.GAMEOVER_LENGTH;
			isGameover = true;
		}

		smoothUpdateTime.set((System.nanoTime() - time) / 1000000000.0); // ns to s
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
		context.drawText(new Vector(width - 153, height - 20), font2, "Yogularm Infinite " + VERSION);

		// Debug
		if (Config.DEBUG_DISPLAY_RENDER_INFO) {
			context.drawText(new Vector(10, 40), font2, String.format(
					"%.0f FPS;    Total Time: %.3f ms;    Update: %.3f ms;    Render: %.3f ms",
					1 / smoothFrameTime.getSmooth(), 
					smoothFrameTime.getSmooth() * 1000, 
					smoothUpdateTime.getSmooth() * 1000,
					smoothRenderTime.getSmooth() * 1000));
			context.drawText(new Vector(10, 25), font2, String.format(
					"Components: %d;     Rendered: %d;     Updated: %d", world.getComponents()
							.getCount(), renderCount, updateCount));
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
		smoothFrameTime.set(frameTime);
	}

	private void restart() {
		startWorld(new LocalWorld());
	}
	
	private void startWorld(World world) {
		this.world = world;
		camera = new Camera();
		camera.setBounds(camera.getBounds().changeSize(viewSize));
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

	public void renderWorld(RenderContext context) {
		camera.applyMatrix(context);
		
		Vector renderRangeBuffer = new Vector(RENDER_RANGE_BUFFER, RENDER_RANGE_BUFFER);
		Rect renderRange = camera.getBounds();
		renderRange = renderRange.changeSize(renderRange.getSize().add(renderRangeBuffer));

		renderCount = 0;
		for (Component component : world.getComponents().getComponentsAround(renderRange)) {
			if (component instanceof Renderable && component != world.getPlayer()) {
				if (renderRange.contains(component.getPosition())) {
					Renderer.render(context, (Renderable) component);
					renderCount++;
				}
			}
		}
		
		if (Config.DEBUG_BUILDING)
			renderFlagMap(context, renderRange);
		
		// To show it above all other components
		Renderer.render(context, world.getPlayer());
	}
	
	private void renderFlagMap(RenderContext context, Rect renderRange) {
		BuildingSite buildingSite = world.getBuildingSite();
		if (buildingSite == null)
			return;
		
		context.unbindTexture();
		int minX = (int)Math.floor(renderRange.getLeft());
		int maxX = (int)Math.ceil(renderRange.getRight());
		int minY = (int)Math.floor(renderRange.getBottom());
		int maxY = (int)Math.ceil(renderRange.getTop());
		Color keepFreeColor = new Color(0, 0, 0.5f, 0.12f);
		Color freeColor = new Color(0, 1, 0, 0.2f);
		Color temporarilyBlockedColor = new Color(1, 0.5f, 0, 0.5f);
		Color blockedColor = new Color(1, 0, 0, 0.5f);
		
		//Color takenColor = new Color(1, 0.5f, 0, 0.5f);
		Color safeColor = new Color(0, 0, 1, 0.2f);
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				Point p = new Point(x, y);
				Rect r = new Rect(x, y, x + 1, y + 1);
				if (buildingSite.isKeptFree(p))
					context.setColor(keepFreeColor);
				else if (buildingSite.isAlwaysFree(p))
					context.setColor(freeColor);
				else if (buildingSite.isFree(p))
					context.setColor(temporarilyBlockedColor);
				else
					context.setColor(blockedColor);
				context.drawRect(r);
				
				if (buildingSite.isSafe(p)) {
					r = new Rect(x, y, x + 1, y + 0.25f);
					context.setColor(safeColor);
					context.drawRect(r);
				}
				
				/*if (buildingSite.isFree(p) && !buildingSite.canPlace(p)) {
					r = new Rect(x + 0.75f, y + 0.75f, x + 1, y + 1);
					context.setColor(takenColor);
					context.drawRect(r);
				}*/
			}
		}
	}
}

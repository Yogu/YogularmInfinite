package de.yogularm;

import de.yogularm.building.Builder2;
import de.yogularm.building.BuildingSite;
import de.yogularm.building.test.TestBuilder;
import de.yogularm.components.Component;
import de.yogularm.components.ComponentCollection;
import de.yogularm.components.ComponentTree;
import de.yogularm.components.Player;
import de.yogularm.drawing.Color;
import de.yogularm.drawing.RenderContext;
import de.yogularm.drawing.Renderable;
import de.yogularm.drawing.Renderer;
import de.yogularm.geometry.Point;
import de.yogularm.geometry.Rect;
import de.yogularm.geometry.Vector;

public class World {
	// private static List<Builder> builders = new ArrayList<Builder>();

	public static final int MIN_BUFFER_LENGTH = 50;
	public static final int MAX_BUFFER_LENGTH = Integer.MAX_VALUE;//200;
	
	private static final int RENDER_RANGE_BUFFER = 4;
	private static final int SECTOR_WIDTH = (int)(Config.MAX_VIEW_WIDTH * 1.2);
	private static final int SECTOR_HEIGHT = (int)(Config.MAX_VIEW_HEIGHT * 1.2);

	static {
		/*
		 * builders.add(new Sky()); builders.add(new Sky2()); builders.add(new
		 * Farm());
		 */
	}

	private ComponentCollection components;
	private Camera camera;
	private Player player;
	private BuildingSite buildingSite;
	private Builder2 currentBuilder;
	
	// Debug
	public int updateCount;
	public int deleteCount;
	public int renderCount;
	public int inRangeCount;
	
	public World() {
		components = new ComponentTree(SECTOR_WIDTH, SECTOR_HEIGHT);
		buildingSite = new BuildingSite(components);
		camera = new Camera();
		player = new Player(components);
		components.add(player);
		
		//currentBuilder = new BuilderConverter(new Sky2());
		currentBuilder = new TestBuilder();
		//currentBuilder = new GroundBuilder();
		
		currentBuilder.init(buildingSite);

		/*// First place
		Stone stone = new Stone(components);
		stone.setPosition(new Vector(0, -1));
		components.add(stone);*/
	}

	public void render(RenderContext context) {
		camera.applyMatrix(context);
		
		Vector renderRangeBuffer = new Vector(RENDER_RANGE_BUFFER, RENDER_RANGE_BUFFER);
		Rect renderRange = camera.getBounds();
		renderRange = renderRange.changeSize(renderRange.getSize().add(renderRangeBuffer));

		renderCount = 0;
		for (Component component : components.getComponentsAround(renderRange)) {
			if (component instanceof Renderable && component != player) {
				if (renderRange.contains(component.getPosition())) {
					Renderer.render(context, (Renderable) component);
					renderCount++;
				}
			}
		}
		
		if (Config.DEBUG_BUILDING)
			renderFlagMap(context, renderRange);
		
		// To show it above all other components
		Renderer.render(context, player);
	}
	
	private void renderFlagMap(RenderContext context, Rect renderRange) {
		context.unbindTexture();
		int minX = (int)Math.floor(renderRange.getLeft());
		int maxX = (int)Math.ceil(renderRange.getRight());
		int minY = (int)Math.floor(renderRange.getBottom());
		int maxY = (int)Math.ceil(renderRange.getTop());
		Color freeColor = new Color(0, 1, 0, 0.2f);
		//Color takenColor = new Color(1, 0.5f, 0, 0.5f);
		Color blockedColor = new Color(1, 0, 0, 0.5f);
		Color keepFreeColor = new Color(0.5f, 0.5f, 1, 0.2f);
		Color safeColor = new Color(0, 0, 1, 0.2f);
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				Point p = new Point(x, y);
				Rect r = new Rect(x, y, x + 1, y + 1);
				if (buildingSite.isKeptFree(p))
					context.setColor(keepFreeColor);
				else if (buildingSite.canPlace(p))
					context.setColor(freeColor);
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

	public void update(float elapsedTime) {
		build();
		
		// inaccurate, for performance reasons: actionRange = 2 * camera.bounds
		Rect actionRange = camera.getBounds().changeSize(camera.getBounds().getSize().multiply(2));

		// accurate, for gameplay
		float actionDistance = Math.max(camera.getBounds().getWidth(), camera
				.getBounds().getHeight());

		updateCount = 0;
		deleteCount = 0;
		inRangeCount = 0;
		
		for (Component component : components.getComponentsAround(actionRange)) {
			inRangeCount++;
			
			float distance = Vector.getDistance(component.getPosition(), player
					.getPosition());
			if (!component.isToRemove() && distance <= actionDistance) {
				component.update(elapsedTime);
				updateCount++;
			}
			if (distance > MAX_BUFFER_LENGTH || component.isToRemove()) {
				components.remove(component);
				deleteCount++;
			}
		}
		camera.scroll(player.getOuterBounds().getCenter(), elapsedTime);
	}

	public Player getPlayer() {
		return player;
	}

	public Camera getCamera() {
		return camera;
	}

	public ComponentCollection getComponents() {
		return components;
	}

	public void build() {
		Rect rect =
			Rect.fromCenterAndSize(player.getPosition(), new Vector(MIN_BUFFER_LENGTH, MIN_BUFFER_LENGTH));
		currentBuilder.build(rect);
	}
}

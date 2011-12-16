package de.yogularm;

import de.yogularm.building.Sky2;
import de.yogularm.components.Stone;
import de.yogularm.drawing.RenderContext;
import de.yogularm.drawing.Renderable;
import de.yogularm.drawing.Renderer;

public class World {
	// private static List<Builder> builders = new ArrayList<Builder>();

	public static final int MIN_BUFFER_LENGTH = 50;
	public static final int MAX_BUFFER_LENGTH = Integer.MAX_VALUE;//200;
	
	private static final int SECTOR_WIDTH = (int)(Config.MAX_VIEW_WIDTH * 1.2);
	private static final int SECTOR_HEIGHT = (int)(Config.MAX_VIEW_HEIGHT * 1.2);

	static {
		/*
		 * builders.add(new Sky()); builders.add(new Sky2()); builders.add(new
		 * Farm());
		 */
	}

	private ComponentCollection components = new ComponentTree(SECTOR_WIDTH, SECTOR_HEIGHT);
	private Camera camera = new Camera();
	private Player player;
	private Builder currentBuilder;
	private float frameTime;
	
	// Debug
	public int updateCount;
	public int deleteCount;
	public int renderCount;
	public int inRangeCount;
	
	public World() {
		player = new Player(components);
		components.add(player);
		
		currentBuilder = new Sky2();
		//currentBuilder = new GroundBuilder();
		currentBuilder.init(components, new Vector(0, 0));

		// First place
		Stone stone = new Stone(components);
		stone.setPosition(new Vector(0, -1));
		components.add(stone);
	}

	public void render(RenderContext context) {
		camera.applyMatrix(context);

		renderCount = 0;
		for (Component component : components.getComponentsAround(camera.getBounds())) {
			if (component instanceof Renderable && component != player) {
				Renderer.render(context, (Renderable) component);
				renderCount++;
			}
		}
		// To show it above all other components
		Renderer.render(context, player);
	}

	public void update(float elapsedTime) {
		frameTime = elapsedTime;

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

	public float getFrameTime() {
		return frameTime;
	}

	public ComponentCollection getComponents() {
		return components;
	}

	public void build() {
		while (Vector.getDistance(player.getPosition(), currentBuilder.getBuildingPosition()) < MIN_BUFFER_LENGTH) {
			currentBuilder.build();
		}
	}
}

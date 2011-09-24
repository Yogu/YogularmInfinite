package de.yogularm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.media.opengl.GL2;

import de.yogularm.building.ClimbTest;
import de.yogularm.components.Stone;

public class World {
	// private static List<Builder> builders = new ArrayList<Builder>();

	public static final int MIN_BUFFER_LENGTH = 50;
	public static final int MAX_BUFFER_LENGTH = 200;

	static {
		/*
		 * builders.add(new Sky()); builders.add(new Sky2()); builders.add(new
		 * Farm());
		 */
	}

	private List<Component> components = new ArrayList<Component>();
	private List<Component> componentsToAdd = new ArrayList<Component>();
	private Camera camera = new Camera();
	private Player player;
	private Player player2;
	private int seed;
	private int structureCounter = 0;
	private Vector buildingPosition = Vector.getZero();
	private Builder currentBuilder;
	private float frameTime;
	private boolean hasSecondPlayer;

	public World(int seed) {
		this(seed, false);
	}
	
	public World(int seed, boolean hasSecondPlayer) {
		this.seed = seed;
		this.hasSecondPlayer = hasSecondPlayer;
		
		currentBuilder = new ClimbTest();

		player = new Player(this);
		components.add(player);
		
		if (hasSecondPlayer) {
			player2 = new Player(this);
			components.add(player2);
			player2.setPosition(new Vector(0, 1));
		}

		// First place
		Stone stone = new Stone(this);
		stone.setPosition(new Vector(0, -1));
		components.add(0, stone);
	}

	public void render(GL2 gl) {
		camera.applyMatrix(gl);

		for (Component component : components) {
			if (component instanceof Renderable) {
				Renderer.render(gl, (Renderable) component);
			}
		}
	}

	public void update(float elapsedTime) {
		frameTime = elapsedTime;

		build();

		components.addAll(0, componentsToAdd);
		componentsToAdd.clear();

		float actionDistance = Math.max(camera.getBounds().getWidth(), camera
				.getBounds().getHeight());

		Iterator<Component> iterator = components.iterator();
		while (iterator.hasNext()) {
			Component component = iterator.next();
			float distance = Vector.getDistance(component.getPosition(), player
					.getPosition());
			if (!component.isToRemove() && distance <= actionDistance)
				component.update(elapsedTime);
			if (distance > MAX_BUFFER_LENGTH || component.isToRemove())
				iterator.remove();
		}
		camera.scroll(player.getOuterBounds().getCenter(), elapsedTime);
	}

	public int getSeed() {
		return seed;
	}

	public int getStructureCounter() {
		return structureCounter;
	}

	public Vector getBuildingPosition() {
		return buildingPosition;
	}

	public Player getPlayer() {
		return player;
	}

	public Player getPlayer2() {
		return player2;
	}

	public Camera getCamera() {
		return camera;
	}

	public float getFrameTime() {
		return frameTime;
	}

	public List<Component> getComponents() {
		return components;
	}

	public void setBuildingPosition(Vector position) {
		if (position == null)
			throw new NullPointerException("position is null");
		buildingPosition = position;
	}

	public void addComponent(Component component) {
		componentsToAdd.add(component);
	}

	public void build() {
		while (Vector.getDistance(player.getPosition(), buildingPosition) < MIN_BUFFER_LENGTH)
			buildStructure();
	}

	public void buildStructure() {
		currentBuilder.build(this, structureCounter);
		structureCounter++;
	}

	public Iterable<Component> getComponentsAt(Vector position) {
		List<Component> list = new ArrayList<Component>();
		Vector rounded = position.round();
		for (Component component : components) {
			if (component.getPosition().round().equals(rounded))
				list.add(component);
		}
		return list;
	}

	public Iterable<Body> getOverlappingBodies(Rect range) {
		List<Body> list = new ArrayList<Body>();
		for (Component component : components) {
			if (component instanceof Body) {
				Body body = (Body)component;
				if (body.getOuterBounds().overlaps(range))
						list.add(body);
			}
		}
		return list;
	}

	public Block getBlockAt(Vector position) {
		for (Component component : getComponentsAt(position))
			if (component instanceof Block)
				return (Block) component;
		return null;
	}

	public boolean hasSolidAt(Vector position) {
		for (Component component : getComponentsAt(position))
			if (component instanceof Body && ((Body) component).isSolid())
				return true;
		return false;
	}

	public Body getBlockBelow(Vector position) {
		Vector rounded = position.round();
		for (Component component : components) {
			if (component instanceof Block) {
				Block block = (Block) component;
				if ((Math.round(component.getPosition().getX()) == rounded
						.getX())
						&& (block.getPosition().getY() <= position.getY()))
					return block;
			}
		}
		return null;
	}
	
	public boolean hasBlockBelow(Vector position) {
		return getBlockBelow(position) != null;
	}
}

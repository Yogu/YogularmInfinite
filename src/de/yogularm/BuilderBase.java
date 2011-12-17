package de.yogularm;

import java.util.Random;

public abstract class BuilderBase implements Builder {
	private ComponentCollection components;
	private int index;
	private int seed;
	private Vector buildingPosition;
	
	public abstract void doBuild();
	
	public void init(ComponentCollection components, Vector buildingPosition) {
		this.components = components;
		this.buildingPosition = buildingPosition;
		index = 0;
		seed = new Random().nextInt();
	}
	
	public void build() {
		index++;
		doBuild();
	}
	
	public ComponentCollection getComponents() {
		return components;
	}
	
	/**
	 * Places the given component relatively to the place below the player
	 * @param component The component to place
	 * @param x The translation along x axis
	 * @param y The translation along y axis
	 */
	protected void place(Component component, float x, float y) {
		// coordinates relatively to the place below the player
		Vector position = new Vector(x, y - 1).add(buildingPosition);
		
		component.setPosition(position);
		components.add(component);
	}

	/**
	 * Creates an instance of the component class and places it relatively to the place below the
	 * player
	 * 
	 * @param componentClass The class of the component to place
	 * @param x The translation along x axis
	 * @param y The translation along y axis
	 */
	protected void place(Class<? extends Component> componentClass, float x, float y) {
		try {
			Component component = componentClass.getConstructor(ComponentCollection.class).newInstance(components);

			place(component, x, y);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void place(Class<? extends Component> componentClass) {
		place(componentClass, 0, 0);
	}

	protected void place(Component component) {
		place(component, 0, 0);
	}
	
	protected int getSeed(int indexOffset) {
		return seed + (index + indexOffset) * 0x24613672;
	}
	
	protected int getSeed() {
		return getSeed(0);
	}
	
	public Vector getBuildingPosition() {
		return buildingPosition;
	}
	
	protected void setBuildingPosition(Vector value) {
		buildingPosition = value;
	}
	
	protected void moveBuildingPosition(Vector offset) {
		buildingPosition = buildingPosition.add(offset);
	}
	
	protected void moveBuildingPosition(float x, float y) {
		moveBuildingPosition(new Vector(x, y));
	}
	
	protected Random getRandom(int additionalSeed, int indexOffset) {
		return new Random(getSeed(indexOffset) + additionalSeed * 0x5685CAE7);
	}
	
	protected Random getRandom(int additionalSeed) {
		return getRandom(additionalSeed, 0);
	}
	
	protected Random getRandom() {
		return getRandom(0);
	}
	
	protected int getCurrentIndex() {
		return index;
	}
}

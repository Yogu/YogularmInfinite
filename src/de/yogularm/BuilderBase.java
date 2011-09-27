package de.yogularm;

import java.util.Random;

import de.yogularm.components.Coin;

public abstract class BuilderBase implements Builder {
	private World world;
	private int index;
	
	public abstract void doBuild();
	
	public void build(World world, int index) {
		this.world = world;
		this.index = index;
		doBuild();
	}
	
	public World getWorld() {
		return world;
	}
	
	protected void place(Component component, float x, float y) {
		// coordinates relatively to the place below the player
		Vector position = new Vector(x, y - 1).add(world.getBuildingPosition());
		
		component.setPosition(position);
		world.addComponent(component);
	}
	
	protected void place(Class<? extends Component> componentClass, float x, float y) {
		try {
			Component component = componentClass.getConstructor(World.class).newInstance(world);

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
		return world.getSeed() + (index + indexOffset) * 0x24613672;
	}
	
	protected int getSeed() {
		return getSeed(0);
	}
	
	protected void moveBuildingPosition(Vector offset) {
		world.setBuildingPosition(world.getBuildingPosition().add(offset));
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
}

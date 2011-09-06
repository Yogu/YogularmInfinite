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
	
	protected void place(Class<? extends Component> componentClass, float x, float y) {
		// coordinates relatively to the place below the player
		Vector position = new Vector(x, y - 1).add(world.getBuildingPosition());
		
		try {
			Component component = componentClass.getConstructor(World.class).newInstance(world);
			component.setPosition(position);
			world.addComponent(component);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void place(Class<? extends Component> componentClass) {
		place(componentClass, 0, 0);
	}
	
	protected int getSeed() {
		return world.getSeed() + index * 0x24613672;
	}
	
	protected void moveBuildingPosition(Vector offset) {
		world.setBuildingPosition(world.getBuildingPosition().add(offset));
		place(Coin.class, 0, 1);
	}
	
	protected void moveBuildingPosition(float x, float y) {
		moveBuildingPosition(new Vector(x, y));
	}
	
	protected Random getRandom(int additionalSeed) {
		return new Random(getSeed() + additionalSeed * 0x5685CAE7);
	}
	
	protected Random getRandom() {
		return getRandom(0);
	}
}

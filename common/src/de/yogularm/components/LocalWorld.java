package de.yogularm.components;

import de.yogularm.Config;
import de.yogularm.building.Builder2;
import de.yogularm.building.BuildingSite;
import de.yogularm.building.test.TestBuilder;
import de.yogularm.geometry.Rect;
import de.yogularm.geometry.Vector;

public class LocalWorld extends AbstractWorld {
	public static final int MIN_BUFFER_LENGTH = (int)(Config.MAX_VIEW_WIDTH * 2);
	public static final int MAX_BUFFER_LENGTH = Integer.MAX_VALUE;//200;
	
	private static final int SECTOR_WIDTH = (int)(Config.MAX_VIEW_WIDTH * 1.2);
	private static final int SECTOR_HEIGHT = (int)(Config.MAX_VIEW_HEIGHT * 1.2);

	private ComponentCollection components;
	private Player player;
	private BuildingSite buildingSite;
	private Builder2 currentBuilder;
	
	public LocalWorld() {
		components = new ComponentTree(SECTOR_WIDTH, SECTOR_HEIGHT);
		buildingSite = new BuildingSite(components);
		player = new Player(components);
		components.add(player);
		
		currentBuilder = new TestBuilder();
		
		currentBuilder.init(buildingSite);
	}

	@Override
	public int update(float elapsedTime, Rect actionRange) {
		build();
		return super.update(elapsedTime, actionRange);
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public ComponentCollection getComponents() {
		return components;
	}

	@Override
	public BuildingSite getBuildingSite() {
		return buildingSite;
	}

	private void build() {
		Rect rect =
			Rect.fromCenterAndSize(player.getPosition(), new Vector(MIN_BUFFER_LENGTH, MIN_BUFFER_LENGTH));
		currentBuilder.build(rect);
	}
}

package de.yogularm.building.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.yogularm.building.BuildingPath;
import de.yogularm.building.PathBuilder;
import de.yogularm.components.general.Coin;
import de.yogularm.components.general.Ladder;
import de.yogularm.components.general.Stone;
import de.yogularm.geometry.Point;
import de.yogularm.utils.WeightedCollection;

public class RandomPathBuilder extends PathBuilder {
	private boolean stuck = false;
	
	private static final float LADDER_LENGTH_FACTOR = 0.5f;
	private static final int MIN_LADDER_LENGTH = 3;
	private static final float COIN_DENSITY = 0.15f;

	public RandomPathBuilder(BuildingPath path) {
		super(path);
	}

	@Override
	public void build() {
		List<Point> reachablePositions = getPath().getReachablePositions();
		List<Point> goodPositions = calculateGoodPositions(reachablePositions);

		Random random = new Random();
		Point newWaypoint = null;
		while (reachablePositions.size() > 0) {
			if (goodPositions.size() == 0) {
				goodPositions = reachablePositions;
				System.out.println("  No right position available");
			}

			getPath().push();

			newWaypoint = goodPositions.get(random.nextInt(goodPositions.size()));
			System.out.printf("  Trying: %s -> %s\n", getPath().getCurrentWaypoint(), newWaypoint);
			if (tryBuildTo(newWaypoint)) {
				getPath().popAndApply();
				break;
			} else {
				reachablePositions.remove(newWaypoint);
				goodPositions.remove(newWaypoint);
				getPath().popAndDiscard();
			}
		}

		if (reachablePositions.size() == 0) {
			if (!stuck)
				System.out.println("Stuck!");
			stuck = true;
			return;
		}

		System.out.printf("OK: %s\n", newWaypoint);

		placeCoins();
	}

	private List<Point> calculateGoodPositions(List<Point> reachablePositions) {
		List<Point> goodPositions = new ArrayList<Point>();

		for (Point point : reachablePositions) {
			if ((Math.random() > 0.5) || (point.getX() > getPath().getCurrentWaypoint().getX()))
				if ((Math.random() > 0.8) || (point.getY() > getPath().getCurrentWaypoint().getY()))
					goodPositions.add(point);
		}

		return goodPositions;
	}

	private void placeCoins() {
		for (Point point : getPath().getLastTrace()) {
			if (/*Config.DEBUG_BUILDING || */Math.random() < COIN_DENSITY)
				getPath().place(new Coin(getComponents()), point);
		}
	}
	
	private boolean tryBuildTo(Point target) {
		WeightedCollection<StructureBuilder> builders = getStructureBuilders();
		while (builders.size() > 0) {
			StructureBuilder builder = builders.getRandom();
			if (builder.tryBuildTo(target))
				return true;
			else
				builders.remove(builder);
		}
		return false;
	}
	
	private WeightedCollection<StructureBuilder> getStructureBuilders() {
		WeightedCollection<StructureBuilder> structureBuilders =
			new WeightedCollection<StructureBuilder>();
		structureBuilders.add(new StoneBuilder(), 50);
		structureBuilders.add(new LadderBuilder(), 1);
		return structureBuilders;
	}

	private class StoneBuilder implements StructureBuilder {
		public boolean tryBuildTo(Point target) {
			getSite().place(new Stone(getComponents()), target.add(0, -1));
			return getPath().setWaypoint(target);
		}
	}

	private class LadderBuilder implements StructureBuilder {
		public boolean tryBuildTo(Point target) {
			int y = 0;
			while (y < MIN_LADDER_LENGTH || (Math.random() <= LADDER_LENGTH_FACTOR)) {
				Point p = target.add(0, y);
				if (!getPath().place(new Ladder(getComponents()), p)
					|| !getPath().setWaypoint(p))
					break;
				
				y++;
			}
			return y >= MIN_LADDER_LENGTH;
		}
	}
}

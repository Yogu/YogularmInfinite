package de.yogularm.building.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.yogularm.Config;
import de.yogularm.building.BuildingPath;
import de.yogularm.building.PathBuilder;
import de.yogularm.components.debug.ArrowComponent;
import de.yogularm.components.general.Checkpoint;
import de.yogularm.components.general.Chicken;
import de.yogularm.components.general.Coin;
import de.yogularm.components.general.Ladder;
import de.yogularm.components.general.Platform;
import de.yogularm.components.general.Shooter;
import de.yogularm.components.general.Stone;
import de.yogularm.geometry.Point;
import de.yogularm.geometry.Vector;
import de.yogularm.utils.WeightedCollection;

public class RandomPathBuilder extends PathBuilder {
	private boolean stuck = false;
	private boolean checkpointToPlace = false;
	private int structureCounter = 0;

	private static final float LADDER_LENGTH_FACTOR = 0.5f;
	private static final int MIN_LADDER_LENGTH = 3;
	private static final float COIN_DENSITY = 0.13f;
	private static final int CHECKPOINT_PERIOD = 20;
	private static final float BACKWARDS_PROBABILITY = 0.25f;
	private static final float CHICKEN_PROBABILITY = 0.05f;
	private static final float SHOOTER_PROBABILITY = 0.05f;

	public RandomPathBuilder(BuildingPath path) {
		super(path);
	}

	@Override
	public void build() {
		List<Point> reachablePositions = getPath().getReachablePositions();
		List<Point> goodPositions = calculateGoodPositions(reachablePositions);

		Random random = new Random();
		Point current = getPath().getCurrentWaypoint();
		Point target = null;
		while (reachablePositions.size() > 0) {
			if (goodPositions.size() == 0) {
				goodPositions = reachablePositions;
				System.out.println("  No right position available");
			}

			target = goodPositions.get(random.nextInt(goodPositions.size()));
			System.out.printf("  Trying: %s -> %s\n", current, target);
			if (tryBuildTo(target)) {
				break;
			} else {
				reachablePositions.remove(target);
				goodPositions.remove(target);
			}
		}

		if (reachablePositions.size() == 0) {
			if (!stuck)
				System.out.println("Stuck!");
			stuck = true;
			return;
		}
		
		target = getPath().getCurrentWaypoint();

		if (Config.DEBUG_BUILDING) {
			Vector c = current.toVector();
			Vector t = target.toVector();
			Vector d = t.subtract(c);
			ArrowComponent arrow = new ArrowComponent(getComponents(), 
					d.getLength(), d.getAngleToXAxis());
			arrow.setPosition(c.add(0.5f, 0.5f));
			getComponents().add(arrow);
		}

		placeCoins();
		placeCheckpoints();
		placeChickens();
		placeShooters();

		System.out.printf("OK: %s\n", target);
		structureCounter++;
	}

	private List<Point> calculateGoodPositions(List<Point> reachablePositions) {
		List<Point> goodPositions = new ArrayList<Point>();

		for (Point point : reachablePositions) {
			if ((Math.random() < BACKWARDS_PROBABILITY) || (point.getX() > getPath().getCurrentWaypoint().getX()))
				if ((Math.random() > 0.8) || (point.getY() > getPath().getCurrentWaypoint().getY()))
					goodPositions.add(point);
		}

		return goodPositions;
	}

	private void placeCoins() {
		for (Point point : getPath().getLastTrace()) {
			if (/* Config.DEBUG_BUILDING || */Math.random() < COIN_DENSITY)
				getPath().place(new Coin(getComponents()), point);
		}
	}
	
	private void placeCheckpoints() {
		checkpointToPlace |= (structureCounter % CHECKPOINT_PERIOD) == CHECKPOINT_PERIOD - 1;
		if (checkpointToPlace) {
			// If checkpoint can't be placed (e.g. a cell taken by ladder), keep heartToPlace true
			checkpointToPlace = !getPath().place(new Checkpoint(getComponents()), getPath().getCurrentWaypoint());
		}
	}
	
	private void placeChickens() {
		if (Math.random() < CHICKEN_PROBABILITY) {
			Chicken chicken = new Chicken(getComponents());
			getPath().place(chicken, getPath().getCurrentWaypoint());
		}
	}
	
	private void placeShooters() {
		if (Math.random() < SHOOTER_PROBABILITY) {
			Random random = new Random();
			Point p = getPath().getCurrentWaypoint().add(0, random.nextInt(4) + 3);
			getPath().place(new Shooter(getComponents()), p);
		}
	}

	private boolean tryBuildTo(Point target) {
		WeightedCollection<StructureBuilder> builders = getStructureBuilders();
		while (builders.size() > 0) {
			getPath().push();
			
			StructureBuilder builder = builders.getRandom();
			System.out.println("   Trying with " + builder.getClass().getSimpleName());
			if (builder.tryBuildTo(target)) {
				getPath().popAndApply();
				return true;
			}
			
			builders.remove(builder);
			getPath().popAndDiscard();
		}
		return false;
	}

	private WeightedCollection<StructureBuilder> getStructureBuilders() {
		WeightedCollection<StructureBuilder> structureBuilders =
		  new WeightedCollection<StructureBuilder>();
		structureBuilders.add(new StoneBuilder(), 50);
		structureBuilders.add(new LadderBuilder(), 0.5f);
		structureBuilders.add(new PlatformBuilder(), 1);
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
				if (!getPath().place(new Ladder(getComponents()), p) || !getPath().setWaypoint(p))
					break;

				y++;
			}
			return y >= MIN_LADDER_LENGTH;
		}
	}

	private class PlatformBuilder implements StructureBuilder {
		private int MAX_DELTA_TRIES = 5;
		
		public boolean tryBuildTo(Point target) {
			Random random = new Random();

			Platform platform = new Platform(getComponents());
			platform.setOrigin(target.add(0, -1).toVector());
			boolean platformPlaced = false;
			Point platformDelta = Point.ZERO;
			for (int i = 0; i < MAX_DELTA_TRIES; i++) {
				int xDiff = (2 + random.nextInt(3));// * (random.nextBoolean() ? 1 : -1);
				int yDiff = (2 + random.nextInt(3));// * (random.nextBoolean() ? 1 : -1);
				platformDelta = new Point(xDiff, yDiff);
				platform.setTargets(new Vector[] { Vector.ZERO, platformDelta.toVector() });
				if (getPath().place(platform, target.add(0, -1))) {
					platformPlaced = true;
					break;
				}
			}
			
			if (!platformPlaced) {
				System.out.println("   Unable to place platform");
				return false;
			}
			
			List<Point> reachable = getPath().calculateReachablePositions(target.add(platformDelta));
			while (reachable.size() > 0) {
				Point target2 = reachable.get(random.nextInt(reachable.size()));
				getPath().push();
				getSite().place(new Stone(getComponents()), target2.add(0, -1));
				if (getPath().setWaypointUsingPlatform(target2, platform)) {
					getPath().popAndApply();
					return true;
				}
				
				getPath().popAndDiscard();
				reachable.remove(target2);
			}
			return false;
		}
	}
}

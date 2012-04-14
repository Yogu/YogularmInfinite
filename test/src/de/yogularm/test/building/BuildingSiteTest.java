package de.yogularm.test.building;

import org.junit.Assert;
import org.junit.Test;

import de.yogularm.building.BuildingSite;
import de.yogularm.components.Component;
import de.yogularm.components.ComponentCollection;
import de.yogularm.components.general.Ladder;
import de.yogularm.components.general.Stone;
import de.yogularm.geometry.Point;
import de.yogularm.test.mock.MockComponentCollection;

public class BuildingSiteTest {
	@Test
	public void testInitState() {
		ComponentCollection components = new MockComponentCollection();
		BuildingSite site = new BuildingSite(components);
		Assert.assertFalse(site.canPop());
	}

	@Test
	public void testFlags() {
		Point[] points = new Point[] {
			new Point(0, 0),
			new Point(-1, -1),
			new Point(-19, 0),
			new Point(-20, 0),
			new Point(-21, 0),
			new Point(-20, 19),
			new Point(-20, 20),
			new Point(-20, 21),
			new Point(0, 14),
			new Point(0, 15),
			new Point(0, 16),
			new Point(0, -14),
			new Point(0, -15),
			new Point(0, 16)
		};
		for (Point p : points) {
			ComponentCollection components = new MockComponentCollection();
			BuildingSite site = new BuildingSite(components);
			placeSolid(components, site, p);
		}
	}

	@Test
	public void testPush() {
		ComponentCollection components = new MockComponentCollection();
		BuildingSite site = new BuildingSite(components);
		placeSolid(components, site, new Point(5, 5));
		site.push();
		Assert.assertFalse(site.isFree(new Point(5, 5)));
		placeSolid(components, site, Point.ZERO);
		placeSolid(components, site, new Point(-1, -1));
		Assert.assertTrue(site.canPop());
		site.popAndDiscard();
		Assert.assertFalse(site.canPop());
	}

	@Test
	public void testPopAndApply() {
		ComponentCollection components = new MockComponentCollection();
		BuildingSite site = new BuildingSite(components);
		site.push();
		Component component = new Stone(components);
		site.place(component, Point.ZERO);
		site.place(new Stone(components), new Point(5, 0));
		site.popAndApply();
		Assert.assertFalse(site.isFree(Point.ZERO));
		Assert.assertFalse(site.isFree(new Point(5, 0)));
		Assert.assertTrue(components.contains(component));
	}

	@Test
	public void testPopAndApplyWithUnpushed() {
		ComponentCollection components = new MockComponentCollection();
		BuildingSite site = new BuildingSite(components);
		site.place(new Stone(components), new Point(3, 0));
		site.push();
		site.place(new Stone(components), Point.ZERO);
		site.place(new Stone(components), new Point(5, 0));
		site.popAndApply();
		Assert.assertFalse(site.isFree(Point.ZERO));
		Assert.assertFalse(site.isFree(new Point(5, 0)));
	}

	@Test
	public void testPopAndDiscard() {
		ComponentCollection components = new MockComponentCollection();
		BuildingSite site = new BuildingSite(components);
		site.push();
		Component component = new Stone(components);
		site.place(component, Point.ZERO);
		site.place(new Stone(components), new Point(5, 0));
		site.popAndDiscard();
		Assert.assertTrue(site.isFree(Point.ZERO));
		Assert.assertTrue(site.isFree(new Point(5, 0)));
		Assert.assertFalse(components.contains(component));
	}

	@Test
	public void testPopAndDiscardWithUnpushed() {
		ComponentCollection components = new MockComponentCollection();
		BuildingSite site = new BuildingSite(components);
		site.place(new Stone(components), new Point(3, 0));
		site.push();
		site.place(new Stone(components), Point.ZERO);
		site.place(new Stone(components), new Point(5, 0));
		site.popAndDiscard();
		Assert.assertTrue(site.isFree(Point.ZERO));
		Assert.assertTrue(site.isFree(new Point(5, 0)));
	}

	@Test
	public void testDeepPop() {
		ComponentCollection components = new MockComponentCollection();
		BuildingSite site = new BuildingSite(components);
		site.place(new Stone(components), Point.ZERO);
		site.push();
			site.place(new Stone(components), new Point(1, 0));
			site.push();
				Assert.assertFalse(site.isFree(new Point(1, 0)));
				site.place(new Stone(components), new Point(2, 0));
				Assert.assertFalse(site.isFree(new Point(2, 0)));
			site.popAndDiscard();
			Assert.assertTrue(site.isFree(new Point(2, 0)));
		site.popAndDiscard();
		Assert.assertTrue(site.isFree(new Point(1, 0)));
		Assert.assertTrue(site.isFree(new Point(2, 0)));
	}
	
	@Test
	public void testLadders() {
		ComponentCollection components = new MockComponentCollection();
		BuildingSite site = new BuildingSite(components);
		site.place(new Ladder(components), new Point(0, 0));
		Assert.assertTrue(site.isSafe(new Point(0, 0)));
	}
	
	private void placeSolid(ComponentCollection components, BuildingSite site, Point position) {
		//System.out.printf("Placing solid to %s\n", position);
		Assert.assertTrue(site.canPlace(position));
		Assert.assertTrue(site.canPlaceSolid(position));
		Assert.assertTrue(site.place(new Stone(components), position));
		Assert.assertFalse(site.isFree(position));
		Assert.assertTrue(site.isSafe(position.add(0, 1)));
		Assert.assertFalse(site.canPlace(position));
		Assert.assertFalse(site.canPlaceSolid(position));
		//System.out.println("OK.");
	}
}
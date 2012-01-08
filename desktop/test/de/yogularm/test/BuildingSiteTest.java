package de.yogularm.test;

import org.junit.Assert;
import org.junit.Before;

import de.yogularm.building.BuildingSite;
import de.yogularm.components.ComponentCollection;
import de.yogularm.components.general.Stone;
import de.yogularm.geometry.Point;
import de.yogularm.test.mock.MockComponentCollection;

public class BuildingSiteTest {
	private ComponentCollection components;

  @Before
  public void setUp() {
		components = new MockComponentCollection();
  }

	@org.junit.Test
	public void testInitState() {
		BuildingSite site = new BuildingSite(components);
		Assert.assertFalse(site.canPop());
	}
	
	@org.junit.Test
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
			BuildingSite site = new BuildingSite(components);
			placeSolid(site, p);
		}
	}

	@org.junit.Test
	public void testPush() {
		BuildingSite site = new BuildingSite(components);
		site.push();
		placeSolid(site, Point.ZERO);
		placeSolid(site, new Point(-1, -1));
		Assert.assertTrue(site.canPop());
		site.popAndDiscard();
		Assert.assertFalse(site.canPop());
	}

	@org.junit.Test
	public void testPopAndApply() {
		BuildingSite site = new BuildingSite(components);
		site.push();
		site.place(new Stone(components), Point.ZERO);
		site.place(new Stone(components), new Point(5, 0));
		site.popAndApply();
		Assert.assertFalse(site.isFree(Point.ZERO));
		Assert.assertFalse(site.isFree(new Point(5, 0)));
	}
	
	public void testPopAndApplyWithUnpushed() {
		BuildingSite site = new BuildingSite(components);
		site.place(new Stone(components), new Point(3, 0));
		site.push();
		site.place(new Stone(components), Point.ZERO);
		site.place(new Stone(components), new Point(5, 0));
		site.popAndApply();
		Assert.assertFalse(site.isFree(Point.ZERO));
		Assert.assertFalse(site.isFree(new Point(5, 0)));
	}

	@org.junit.Test
	public void testPopAndDiscard() {
		BuildingSite site = new BuildingSite(components);
		site.push();
		site.place(new Stone(components), Point.ZERO);
		site.place(new Stone(components), new Point(5, 0));
		site.popAndDiscard();
		Assert.assertTrue(site.isFree(Point.ZERO));
		Assert.assertTrue(site.isFree(new Point(5, 0)));
	}

	@org.junit.Test
	public void testPopAndDiscardWithUnpushed() {
		BuildingSite site = new BuildingSite(components);
		site.place(new Stone(components), new Point(3, 0));
		site.push();
		site.place(new Stone(components), Point.ZERO);
		site.place(new Stone(components), new Point(5, 0));
		site.popAndDiscard();
		Assert.assertTrue(site.isFree(Point.ZERO));
		Assert.assertTrue(site.isFree(new Point(5, 0)));
	}
	
	private void placeSolid(BuildingSite site, Point position) {
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
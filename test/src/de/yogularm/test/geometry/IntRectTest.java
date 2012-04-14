package de.yogularm.test.geometry;

import org.junit.Assert;
import org.junit.Test;

import de.yogularm.geometry.IntRect;
import de.yogularm.geometry.Point;

public class IntRectTest {
	@Test
	public void testVectorConstructor() {
		IntRect r = new IntRect(new Point(1, 2), new Point(3, 4));
		Assert.assertEquals(new Point(1, 2), r.getMin());
		Assert.assertEquals(new Point(3, 4), r.getMax());

		r = new IntRect(new Point(3, 4), new Point(1, 2));
		Assert.assertEquals(new Point(1, 2), r.getMin());
		Assert.assertEquals(new Point(3, 4), r.getMax());

		r = new IntRect(new Point(1, 4), new Point(3, 2));
		Assert.assertEquals(new Point(1, 2), r.getMin());
		Assert.assertEquals(new Point(3, 4), r.getMax());

		r = new IntRect(new Point(3, 2), new Point(1, 4));
		Assert.assertEquals(new Point(1, 2), r.getMin());
		Assert.assertEquals(new Point(3, 4), r.getMax());
	}

	@Test
	public void testAtomicConstructor() {
		IntRect r = new IntRect(1, 2, 3, 4);
		Assert.assertEquals(new Point(1, 2), r.getMin());
		Assert.assertEquals(new Point(3, 4), r.getMax());
	}
	
	@Test
	public void testEdges() {
		IntRect r = new IntRect(1, 2, 3, 4);
		Assert.assertEquals(1, r.getLeft());
		Assert.assertEquals(2, r.getBottom());
		Assert.assertEquals(3, r.getRight());
		Assert.assertEquals(4, r.getTop());
	}
	
	@Test
	public void testCorners() {
		IntRect r = new IntRect(1, 2, 3, 4);
		Assert.assertEquals(new Point(1, 2), r.getBottomLeft());
		Assert.assertEquals(new Point(3, 2), r.getBottomRight());
		Assert.assertEquals(new Point(1, 4), r.getTopLeft());
		Assert.assertEquals(new Point(3, 4), r.getTopRight());
	}
	
	@Test
	public void testAdd() {
		IntRect r = new IntRect(1, 2, 3, 4);
		Point offset = new Point(2, 4);
		IntRect r2 = r.add(offset);
		Assert.assertEquals(r.getSize(), r2.getSize());
		Assert.assertEquals(r.getMin().add(offset), r2.getMin());
	}
	
	@Test
	public void testContains() {
		IntRect r = new IntRect(1, 2, 3, 4);
		Assert.assertTrue(r.contains(new Point(1, 2))); // the bottom-right corner
		Assert.assertTrue(r.contains(new Point(2, 3)));
		Assert.assertFalse(r.contains(new Point(0, 3))); // too far to the left
		Assert.assertFalse(r.contains(new Point(10, 3))); // too far to the right
		Assert.assertFalse(r.contains(new Point(2, 10))); // too far up
		Assert.assertFalse(r.contains(new Point(2, 0))); // too far down
	}
	
	@Test
	public void testGetSize() {
		IntRect r = new IntRect(1, 2, 4, 6);
		Assert.assertEquals(new Point(3, 4), r.getSize());
		Assert.assertEquals(3, r.getWidth());
		Assert.assertEquals(4, r.getHeight());
	}
	
	@Test
	public void testEquality() {
		IntRect p1 = new IntRect(1, 2, 3, 4);
		IntRect p2 = new IntRect(3, 4, 1, 2); // equals p1
		Assert.assertTrue(p1.equals(p2));
		Assert.assertFalse(p1.equals(new IntRect(2, 1, 3, 4)));
		Assert.assertFalse(p1.equals(new IntRect(1, 2, 4, 3)));
		
		// equals(IntRect) should return false if IntRect is null
		p2 = null;
		Assert.assertFalse(p1.equals(p2));
	}
	
	@Test
	public void testEqualsObject() {
		IntRect p1 = new IntRect(1, 2, 3, 4);
		Object p2 = new IntRect(3, 4, 1, 2); // equals p1
		Object p3 = new IntRect(5, 4, 1, 2); // does not equal p1
		Assert.assertTrue(p1.equals(p2));
		Assert.assertFalse(p1.equals(p3));
		Assert.assertFalse(p1.equals(new Object()));
		Object nullObj = null;
		Assert.assertFalse(p1.equals(nullObj));
	}
	
	@Test
	public void testToString() {
		Point v1 = new Point(1, 2);
		Point v2 = new Point(3, 4);
		IntRect r = new IntRect(v1, v2);
		String str = r.toString();
		Assert.assertTrue(str.contains(v1.toString()));
		Assert.assertTrue(str.contains(v2.toString()));
	}
	
	@Test
	public void testHashCode() {
		IntRect r1 = new IntRect(1, 2, 3, 4);
		IntRect r2 = new IntRect(4, 3, 2, 1);
		Assert.assertFalse(r1.hashCode() == 0);
		Assert.assertFalse(r2.hashCode() == 0);
		Assert.assertFalse(r1.hashCode() == r2.hashCode());
		Assert.assertEquals(r1.hashCode(), r1.hashCode());
	}
}

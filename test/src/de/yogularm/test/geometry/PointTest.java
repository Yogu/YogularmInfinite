package de.yogularm.test.geometry;

import junit.framework.Assert;

import org.junit.Test;

import de.yogularm.geometry.Point;

public class PointTest {
	@Test
	public void testConstructor() {
		Point point = new Point(1, 2);
		Assert.assertEquals(point.getX(), 1);
		Assert.assertEquals(point.getY(), 2);
	}
	
	@Test
	public void testZero() {
		Assert.assertEquals(Point.ZERO.getX(), 0);
		Assert.assertEquals(Point.ZERO.getX(), 0);
		Assert.assertEquals(Point.ZERO, Point.getZero());
	}
	
	@Test
	public void testEquality() {
		Point p1 = new Point(1, 1);
		Point p2 = new Point(1, 1);
		Object p2AsObj = p2;
		Assert.assertFalse(p1.equals(false));
		Assert.assertTrue(p1.equals(p2));
		Assert.assertTrue(p1.equals(p2AsObj));
		Assert.assertFalse(p1.equals(new Point(2, 2)));
		Assert.assertFalse(p1.equals(new Point(1, 2)));
		Assert.assertFalse(p1.equals(new Object()));
		
		// equals(Point) should return false if point is null
		p2 = null;
		Assert.assertFalse(p1.equals(p2));
	}
	
	@Test
	public void testAdd() {
		Point p = new Point(3, 5);
		Assert.assertEquals(p.add(0, 2), new Point(3, 7));
		Assert.assertEquals(p.add(p), new Point(6, 10));
	}
	
	@Test
	public void testToString() {
		Point p = new Point(123, 456);
		String str = p.toString();
		Assert.assertTrue(str.matches(".+123.+456.+"));
	}
	
	@Test
	public void testHashCode() {
		Point p1 = new Point(1, 3);
		Point p2 = new Point(1, 2);
		Assert.assertFalse(p1.hashCode() == 0);
		Assert.assertFalse(p2.hashCode() == 0);
		Assert.assertFalse(p1.hashCode() == p2.hashCode());
		Assert.assertEquals(p1.hashCode(), p1.hashCode());
	}
}

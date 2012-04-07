package de.yogularm.test;

import org.junit.Assert;
import org.junit.Test;

import de.yogularm.geometry.Point;
import de.yogularm.geometry.Rect;
import de.yogularm.geometry.Vector;

public class RectTest {
	private static final float EPSILON = 0.0000001f;
	
	@Test
	public void testVectorConstructor() {
		Rect r = new Rect(new Vector(1, 2), new Vector(3, 4));
		Assert.assertEquals(new Vector(1, 2), r.getMinVector());
		Assert.assertEquals(new Vector(3, 4), r.getMaxVector());

		r = new Rect(new Vector(3, 4), new Vector(1, 2));
		Assert.assertEquals(new Vector(1, 2), r.getMinVector());
		Assert.assertEquals(new Vector(3, 4), r.getMaxVector());

		r = new Rect(new Vector(1, 4), new Vector(3, 2));
		Assert.assertEquals(new Vector(1, 2), r.getMinVector());
		Assert.assertEquals(new Vector(3, 4), r.getMaxVector());

		r = new Rect(new Vector(3, 2), new Vector(1, 4));
		Assert.assertEquals(new Vector(1, 2), r.getMinVector());
		Assert.assertEquals(new Vector(3, 4), r.getMaxVector());
	}

	@Test
	public void testAtomicConstructor() {
		Rect r = new Rect(1, 2, 3, 4);
		Assert.assertEquals(new Vector(1, 2), r.getMinVector());
		Assert.assertEquals(new Vector(3, 4), r.getMaxVector());
	}
	
	@Test
	public void testCenterSizeConstructor() {
		Vector c = new Vector(0, 2);
		Vector s = new Vector(2, 4);
		Rect r = Rect.fromCenterAndSize(c, s);
		Assert.assertEquals(new Vector(-1, 0), r.getMinVector());
		Assert.assertEquals(new Vector(1, 4), r.getMaxVector());
		Assert.assertEquals(c, r.getCenter());
		Assert.assertEquals(s, r.getSize());
	}

	@Test
	public void testGetCenter() {
		Rect r = new Rect(-1, 0, 1, 4);
		Assert.assertEquals(new Vector(0, 2), r.getCenter());
	}

	@Test
	public void testGetSize() {
		Rect r = new Rect(-1, 0, 1, 4);
		Assert.assertEquals(new Vector(2, 4), r.getSize());
		Assert.assertEquals(2, r.getWidth(), EPSILON);
		Assert.assertEquals(4, r.getHeight(), EPSILON);
	}
	
	@Test
	public void testEdges() {
		Rect r = new Rect(1, 2, 3, 4);
		Assert.assertEquals(1, r.getLeft(), EPSILON);
		Assert.assertEquals(2, r.getBottom(), EPSILON);
		Assert.assertEquals(3, r.getRight(), EPSILON);
		Assert.assertEquals(4, r.getTop(), EPSILON);
	}
	
	@Test
	public void testCorners() {
		Rect r = new Rect(1, 2, 3, 4);
		Assert.assertEquals(new Vector(1, 2), r.getBottomLeft());
		Assert.assertEquals(new Vector(3, 2), r.getBottomRight());
		Assert.assertEquals(new Vector(1, 4), r.getTopLeft());
		Assert.assertEquals(new Vector(3, 4), r.getTopRight());
	}
	
	@Test
	public void testChangeSize() {
		Rect r = new Rect(1, 2, 3, 4);
		Vector newSize = new Vector(2, 4);
		Rect r2 = r.changeSize(newSize);
		Assert.assertEquals(r.getCenter(), r2.getCenter());
		Assert.assertEquals(newSize, r2.getSize());
	}
	
	@Test
	public void testChangeCenter() {
		Rect r = new Rect(1, 2, 3, 4);
		Vector newCenter = new Vector(2, 4);
		Rect r2 = r.changeCenter(newCenter);
		Assert.assertEquals(r.getSize(), r2.getSize());
		Assert.assertEquals(newCenter, r2.getCenter());
	}
	
	@Test
	public void testAdd() {
		Rect r = new Rect(1, 2, 3, 4);
		Vector offset = new Vector(2, 4);
		Rect r2 = r.add(offset);
		Assert.assertEquals(r.getSize(), r2.getSize());
		Assert.assertEquals(r.getCenter().add(offset), r2.getCenter());
	}
	
	@Test
	public void testOverlaps() {
		Rect r1 = new Rect(1, 2, 3, 4);
		Assert.assertTrue(r1.overlaps(new Rect(2, 3, 10, 10))); // r1.topRight with r2.bottomLeft
		Assert.assertTrue(r1.overlaps(new Rect(0, 0, 2, 3))); // r1.bottomRight with r2.topLeft
		Assert.assertFalse(r1.overlaps(new Rect(10, 3, 20, 10))); // too far to the right 
		Assert.assertFalse(r1.overlaps(new Rect(2, 10, 10, 20))); // too far up 
		Assert.assertFalse(r1.overlaps(new Rect(2, 0, 10, 1))); // too far down
		Assert.assertFalse(r1.overlaps(new Rect(0, 0, 1, 2))); // test epsilon
	}
	
	@Test
	public void testContains() {
		Rect r = new Rect(1, 2, 3, 4);
		Assert.assertTrue(r.contains(new Vector(3, 2))); // the bottom-right corner
		Assert.assertTrue(r.contains(new Vector(2, 3)));
		Assert.assertFalse(r.contains(new Vector(0, 3))); // too far to the left
		Assert.assertFalse(r.contains(new Vector(10, 3))); // too far to the right
		Assert.assertFalse(r.contains(new Vector(2, 10))); // too far up
		Assert.assertFalse(r.contains(new Vector(2, 0))); // too far down
	}
	
	@Test
	public void testEquality() {
		Rect p1 = new Rect(1, 2, 3, 4);
		Rect p2 = new Rect(3, 4, 1, 2); // equals p1
		Object p2AsObj = p2;
		Assert.assertFalse(p1.equals(false));
		Assert.assertTrue(p1.equals(p2));
		Assert.assertTrue(p1.equals(p2AsObj));
		Assert.assertFalse(p1.equals(new Rect(2, 1, 3, 4)));
		Assert.assertFalse(p1.equals(new Rect(1, 2, 4, 3)));
		Assert.assertFalse(p1.equals(new Object()));
		
		// equals(Rect) should return false if Rect is null
		p2 = null;
		Assert.assertFalse(p1.equals(p2));
	}
	
	@Test
	public void testToString() {
		Vector v1 = new Vector(1, 2);
		Vector v2 = new Vector(3, 4);
		Rect r = new Rect(v1, v2);
		String str = r.toString();
		Assert.assertTrue(str.contains(v1.toString()));
		Assert.assertTrue(str.contains(v2.toString()));
	}
	
	@Test
	public void testHashCode() {
		Rect r1 = new Rect(1, 2, 3, 4);
		Rect r2 = new Rect(4, 3, 2, 1);
		Assert.assertFalse(r1.hashCode() == 0);
		Assert.assertFalse(r2.hashCode() == 0);
		Assert.assertFalse(r1.hashCode() == r2.hashCode());
		Assert.assertEquals(r1.hashCode(), r1.hashCode());
	}
}

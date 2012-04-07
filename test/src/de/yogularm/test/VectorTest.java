package de.yogularm.test;

import junit.framework.Assert;

import org.junit.Test;

import de.yogularm.geometry.Axis;
import de.yogularm.geometry.Direction;
import de.yogularm.geometry.Point;
import de.yogularm.geometry.Vector;

public class VectorTest {
	@Test
	public void testConstructors() {
		Vector vector = new Vector(5, -7);
		Assert.assertEquals(vector.getX(), 5f);
		Assert.assertEquals(vector.getY(), -7f);
		Assert.assertEquals(new Vector(0, 0), Vector.ZERO);
		Assert.assertEquals(new Vector(0, 0), Vector.getZero());
	}

	@Test
	public void testEquality() {
		Vector v1 = new Vector(1, 1);
		Vector v2 = new Vector(1, 1);
		Vector v3 = new Vector(2, 2);
		Object v2AsObj = v2;
		Assert.assertTrue(v1.equals(v2));
		Assert.assertTrue(v1.equals(v2AsObj));
		Assert.assertFalse(v1.equals(v3));
		Assert.assertFalse(v1.equals(new Object()));
	}
	
	@Test
	public void testIsZero() {
		Assert.assertTrue(new Vector(0, 0).isZero());
		Assert.assertFalse(new Vector(0, 1).isZero());
		Assert.assertFalse(new Vector(1, 0).isZero());
	}
	
	@Test
	public void testEqualityWithEpsilon() {
		Vector v1 = new Vector(1, 1);
		Vector v2 = new Vector(1.00001f, 1);
		Vector v3 = new Vector(2, 2);
		Vector v4 = new Vector(1.000001f, 1.1f);
		Assert.assertTrue(v1.equals(v2, 0.01f));
		Assert.assertFalse(v1.equals(v3, 0.01f));
		Assert.assertFalse(v1.equals(v4, 0.01f));
	}
	
	@Test
	public void testGetComponent() {
		Assert.assertEquals(new Vector(2, 3).getComponent(Axis.HORIZONTAL), 2f);
		Assert.assertEquals(new Vector(2, 3).getComponent(Axis.VERTICAL), 3f);
		Assert.assertEquals(new Vector(2, 3).getComponent(Axis.NONE), 0f);
	}

	@Test
	public void testChangeMethods() {
		Vector vector = new Vector(1, 2);
		Assert.assertEquals(new Vector(5, 2), vector.changeX(5));
		Assert.assertEquals(new Vector(1, 3), vector.changeY(3));
		Assert.assertEquals(new Vector(1, 2), vector.changeComponent(Axis.NONE, 5));
		Assert.assertEquals(new Vector(5, 2), vector.changeComponent(Axis.HORIZONTAL, 5));
		Assert.assertEquals(new Vector(1, 3), vector.changeComponent(Axis.VERTICAL, 3));
	}

	@Test
	public void testBasicCalculation() {
		Assert.assertEquals(new Vector(5, 5), new Vector(1, 2).add(4, 3));
		Assert.assertEquals(new Vector(5, 5), new Vector(1, 2).add(new Vector(4, 3)));
		Assert.assertEquals(new Vector(2, 3), new Vector(4, 4).subtract(new Vector(2, 1)));

		Assert.assertEquals(new Vector(2, 6), new Vector(1, 2).multiply(2, 3));
		Assert.assertEquals(new Vector(2, 6), new Vector(1, 2).multiply(new Vector(2, 3)));
		Assert.assertEquals(new Vector(3, 6), new Vector(1, 2).multiply(3));
		Assert.assertEquals(new Vector(2, 2), new Vector(4, 4).divide(2));

		Assert.assertEquals(new Vector(-2, 3), new Vector(2, -3).negate());
	}

	@Test
	public void testDistances() {
		Assert.assertEquals((float) Math.sqrt(2), new Vector(1, 1).getLength());
		Assert.assertEquals(1f, new Vector(-1, 0).getLength());
		Assert.assertEquals((float) Math.sqrt(17),
		  Vector.getDistance(new Vector(1, 3), new Vector(-3, 2)));
	}

	@Test
	public void testAngles() {
		Assert.assertEquals(45f, Vector.getAngle(new Vector(0, 1), new Vector(1, 1)));
		Assert.assertEquals(45f, Vector.getAngle(new Vector(-1, 1), new Vector(-1, 0)));
		Assert.assertEquals(45f, new Vector(1, 1).getAngleToXAxis());
	}

	@Test
	public void testDirections() {
		Assert.assertEquals(Direction.UP, new Vector(0, 1).getDirection());
		Assert.assertEquals(Direction.DOWN, new Vector(0, -5).getDirection());
		Assert.assertEquals(Direction.RIGHT, new Vector(2, 0).getDirection());
		Assert.assertEquals(Direction.LEFT, new Vector(Float.NEGATIVE_INFINITY, 0).getDirection());
		Assert.assertEquals(Direction.NONE, new Vector(2, 3).getDirection());
		Assert.assertEquals(Direction.NONE, new Vector(0, 0).getDirection());
	}

	@Test
	public void testRounding() {
		Assert.assertEquals(new Vector(2, 3), new Vector(2.5f, 3.7f).floor());
		Assert.assertEquals(new Vector(3, 4), new Vector(2.4f, 3.7f).ceil());
		Assert.assertEquals(new Vector(3, 3), new Vector(2.5f, 3.2f).round());
	}
	
	@Test
	public void testToPoint() {
		Assert.assertEquals(new Vector(1, 2).toPoint(), new Point(1, 2));
		Assert.assertEquals(new Vector(1.2f, 1.7f).toPoint(), new Point(1, 2));
	}
	
	@Test
	public void testToString() {
		Vector v = new Vector(123, 456);
		String str = v.toString();
		Assert.assertTrue(str.matches(".+123.+456.+"));
	}
	
	@Test
	public void testHashCode() {
		Vector v1 = new Vector(1, 1);
		Vector v2 = new Vector(1, 2);
		Assert.assertFalse(v1.hashCode() == 0);
		Assert.assertFalse(v2.hashCode() == 0);
		Assert.assertFalse(v1.hashCode() == v2.hashCode());
		Assert.assertEquals(v1.hashCode(), v1.hashCode());
	}
}

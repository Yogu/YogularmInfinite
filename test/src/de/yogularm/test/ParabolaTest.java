package de.yogularm.test;

import org.junit.Assert;
import org.junit.Test;

import de.yogularm.geometry.Parabola;
import de.yogularm.geometry.Vector;

public class ParabolaTest {
	private static final float EPSILON = 0.0000001f;
	
	@Test
	public void testAtomicConstructor() {
		// y = -1 * (x - 2)^2 + 3
		Parabola p = new Parabola(-1, 2, 3);
		Assert.assertEquals(3, p.getY(2), EPSILON);
		Assert.assertEquals(2, p.getY(1), EPSILON);
		Assert.assertEquals(-1, p.getY(4), EPSILON);
	}
	
	@Test
	public void testConstructorWithApex() {
		// y = -1 * (x - 2)^2 + 3
		Parabola p = new Parabola(-1, new Vector(2, 3));
		Assert.assertEquals(3, p.getY(2), EPSILON);
		Assert.assertEquals(2, p.getY(1), EPSILON);
		Assert.assertEquals(-1, p.getY(4), EPSILON);
	}
	
	@Test
	public void testConstructorWithApexAndOtherPoint() {
		Parabola p = new Parabola(new Vector(2, 3), new Vector(10, -2));
		Assert.assertEquals(3, p.getY(2), EPSILON);
		Assert.assertEquals(-2, p.getY(10), EPSILON);
	}
	
	@Test
	public void testGetApex() {
		Vector apex = new Vector(2, 3);
		Parabola p = new Parabola(5, apex);
		Assert.assertEquals(apex, p.getApex());
	}
	
	@Test
	public void testChangeApex() {
		Vector apex = new Vector(2, 3);
		Parabola p = new Parabola(5, apex);
		Vector newApex = new Vector(4, 6);
		Parabola p2 = p.changeApex(newApex);
		Assert.assertEquals(newApex, p2.getApex());
		Assert.assertEquals(p.getY(3), p2.getY(5) - 3, EPSILON);
		Assert.assertEquals(p2, p.move(new Vector(2, 3)));
	}
	
	@Test
	public void testGetX() {
		// y = -1 * (x - 2)^2 + 3
		Parabola p = new Parabola(-1, new Vector(2, 3));
		Assert.assertEquals(1, p.getX1(2), EPSILON);
		Assert.assertEquals(3, p.getX2(2), EPSILON);
	}
	
	@Test
	public void testMin() {
		Parabola p = new Parabola(3, new Vector(4, 6));
		Assert.assertEquals(6, p.getMin(), EPSILON);
		
		p = new Parabola(-3, new Vector(4, 6));
		Assert.assertEquals(Float.NEGATIVE_INFINITY, p.getMin(), EPSILON);
	}
	
	@Test
	public void testMax() {
		Parabola p = new Parabola(3, new Vector(4, 6));
		Assert.assertEquals(Float.POSITIVE_INFINITY, p.getMax(), EPSILON);
		
		p = new Parabola(-3, new Vector(4, 6));
		Assert.assertEquals(6, p.getMax(), EPSILON);
	}
	
	@Test
	public void testMinInRange() {
		Parabola p = new Parabola(3, new Vector(4, 6));
		Assert.assertEquals(6, p.getMinY(3, 5), EPSILON);
		Assert.assertEquals(p.getY(3), p.getMinY(2, 3), EPSILON);
		Assert.assertEquals(p.getY(5), p.getMinY(5, 6), EPSILON);

		p = new Parabola(-3, new Vector(4, 6));
		Assert.assertEquals(p.getY(3), p.getMinY(3, 5), EPSILON);
		Assert.assertEquals(p.getY(2), p.getMinY(2, 3), EPSILON);
	}
	
	@Test
	public void testMaxInRange() {
		Parabola p = new Parabola(3, new Vector(4, 6));
		Assert.assertEquals(p.getY(6), p.getMaxY(3, 6), EPSILON);
		Assert.assertEquals(p.getY(2), p.getMaxY(2, 3), EPSILON);
		Assert.assertEquals(p.getY(6), p.getMaxY(5, 6), EPSILON);

		p = new Parabola(-3, new Vector(4, 6));
		Assert.assertEquals(6, p.getMaxY(3, 5), EPSILON);
		Assert.assertEquals(p.getY(3), p.getMaxY(2, 3), EPSILON);
	}
	
	@Test
	public void testEquality() {
		Parabola p1 = new Parabola(1, 2, 3);
		Parabola p2 = new Parabola(1, new Vector(2, 3)); // equals p1
		Assert.assertTrue(p1.equals(p2));
		Assert.assertFalse(p1.equals(new Parabola(2, 1, 3)));
		Assert.assertFalse(p1.equals(new Parabola(1, 2, 4)));
		Assert.assertFalse(p1.equals(new Parabola(1, 3, 3)));
		
		// equals(Parabola) should return false if Parabola is null
		p2 = null;
		Assert.assertFalse(p1.equals(p2));
	}
	
	@Test
	public void testEqualsObject() {
		Parabola p1 = new Parabola(1, 2, 3);
		Object p2 = new Parabola(1, new Vector(2, 3)); // equals p1
		Object p3 = new Parabola(5, 4, 1); // does not equal p1
		Assert.assertTrue(p1.equals(p2));
		Assert.assertFalse(p1.equals(p3));
		Assert.assertFalse(p1.equals(new Object()));
		Object nullObj = null;
		Assert.assertFalse(p1.equals(nullObj));
	}
	
	@Test
	public void testHashCode() {
		Parabola r1 = new Parabola(1, 2, 3);
		Parabola r2 = new Parabola(4, 3, 2);
		Assert.assertFalse(r1.hashCode() == 0);
		Assert.assertFalse(r2.hashCode() == 0);
		Assert.assertFalse(r1.hashCode() == r2.hashCode());
		Assert.assertEquals(r1.hashCode(), r1.hashCode());
	}
	
	@Test
	public void testToString() {
		Parabola p = new Parabola(12, 34, 56);
		String str = p.toString();
		Assert.assertTrue(str.matches(".+12.+34.+56.+"));
	}
}

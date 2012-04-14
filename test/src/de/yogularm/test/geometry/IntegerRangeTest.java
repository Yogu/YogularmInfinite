package de.yogularm.test.geometry;

import org.junit.Assert;
import org.junit.Test;

import de.yogularm.geometry.IntegerRange;

public class IntegerRangeTest {
	@Test
	public void test() {
		IntegerRange range = new IntegerRange(2, 6);
		Assert.assertEquals(2, range.getMin());
		Assert.assertEquals(6, range.getMax());

		range = new IntegerRange(6, 2);
		Assert.assertEquals(2, range.getMin());
		Assert.assertEquals(6, range.getMax());
	}
}

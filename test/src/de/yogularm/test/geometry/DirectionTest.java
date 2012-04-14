package de.yogularm.test.geometry;

import org.junit.Assert;
import org.junit.Test;

import de.yogularm.geometry.Axis;
import de.yogularm.geometry.Direction;

public class DirectionTest {
	@Test
	public void testGetAxis() {
		Assert.assertEquals(Direction.UP.getAxis(), Axis.VERTICAL);
		Assert.assertEquals(Direction.DOWN.getAxis(), Axis.VERTICAL);
		Assert.assertEquals(Direction.LEFT.getAxis(), Axis.HORIZONTAL);
		Assert.assertEquals(Direction.RIGHT.getAxis(), Axis.HORIZONTAL);
		Assert.assertEquals(Direction.NONE.getAxis(), Axis.NONE);
	}
}

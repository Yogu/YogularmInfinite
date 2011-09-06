package de.yogularm.building;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.yogularm.Builder;
import de.yogularm.World;

public class Sky implements Builder {
	private static final List<Class<? extends Builder>> structures
		= new ArrayList<Class<? extends Builder>>();
	
	static {
		structures.add(Flatland.class);
		structures.add(Stairs.class);
		structures.add(Gaps.class);
		structures.add(Gaps.class); // more probably
	}
	
	public void build(World world, int index) {
		Random random = new Random(world.getSeed() + index * 0x573C75B5);
		int i = random.nextInt(structures.size());
		Class<? extends Builder> structureClass = structures.get(i);
		Builder structure = null;
		try {
			structure = structureClass.newInstance();
		} catch (Exception e) { }
		
		structure.build(world, index);
	}
}

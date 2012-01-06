package de.yogularm.building;

import java.util.ArrayList;
import java.util.List;

import de.yogularm.geometry.Rect;

public abstract class GuidedBuilder implements Builder2 {
	private List<PathBuilder> pathBuilders;
	private BuildingSite buildingSite;
	
	private static final int MAX_BUILD_CALLS = 100;
	
	@Override
  public void init(BuildingSite buildingSite) {
	  this.buildingSite = buildingSite;
	  pathBuilders = new ArrayList<PathBuilder>();
	  PathBuilder builder = getFirstPathBuilder(buildingSite);
	  if (builder == null)
	  	throw new NullPointerException("FirstPathBuilder is null");
	  pathBuilders.add(builder);
  }

	@Override
  public void build(Rect bounds) {
	  for (PathBuilder builder : pathBuilders) {
	  	int buildCalls = 0;
	  	while (bounds.contains(builder.getPath().getCurrentWaypoint().toVector())) {
	  		builder.build();
	  		if (buildingSite.canPop())
	  			throw new RuntimeException("Path builder missed to pop");
	  		buildCalls++;
	  		if (buildCalls > MAX_BUILD_CALLS)
	  			throw new RuntimeException("Path builder seems to be stuck");
	  	}
	  }
  }
	
	protected abstract PathBuilder getFirstPathBuilder(BuildingSite site);
}

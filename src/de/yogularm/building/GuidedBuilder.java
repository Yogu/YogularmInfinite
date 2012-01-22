package de.yogularm.building;

import java.util.ArrayList;
import java.util.List;

import de.yogularm.components.ComponentCollection;
import de.yogularm.geometry.Point;
import de.yogularm.geometry.Rect;

public abstract class GuidedBuilder implements Builder2 {
	private List<PathBuilder> pathBuilders;
	private BuildingSite buildingSite;
	
	private static final int MAX_BUILD_CALLS = 5;
	private static final boolean REMOVE_WHEN_STUCK = false;
	
	@Override
  public void init(BuildingSite buildingSite) {
	  this.buildingSite = buildingSite;
	  
	  makeOriginSafe();
	  if (!buildingSite.isSafe(Point.ZERO))
	  	throw new RuntimeException("Guided buidler can't make origin safe");
	  
	  pathBuilders = new ArrayList<PathBuilder>();
	  PathBuilder builder = getFirstPathBuilder(buildingSite);
	  if (builder == null)
	  	throw new NullPointerException("FirstPathBuilder is null");
	  pathBuilders.add(builder);
  }

  @SuppressWarnings("unused")
  @Override
  public void build(Rect bounds) {
		List<PathBuilder> toRemove = null;
	  for (PathBuilder builder : pathBuilders) {
	  	int buildCalls = 0;
	  	while (bounds.contains(builder.getPath().getCurrentWaypoint().toVector())) {
	  		builder.build();
	  		if (buildingSite.canPop())
	  			throw new RuntimeException("Path builder missed to pop");
	  		buildCalls++;
	  		if (buildCalls > MAX_BUILD_CALLS) {
	  			//System.out.println("Path builder got stuck!");
	  			if (REMOVE_WHEN_STUCK) {
		  			if (toRemove == null)
		  				toRemove = new ArrayList<PathBuilder>();
		  			toRemove.add(builder);
	  			}
	  			break;//throw new RuntimeException("Path builder seems to be stuck");
	  		}
	  	}
	  }

	  if (toRemove != null)
		  for (PathBuilder builder : toRemove)
		  	pathBuilders.remove(builder);
  }
	
	public BuildingSite getBuildingSite() {
		return buildingSite;
	}
	
	protected ComponentCollection getComponents() {
		return buildingSite.getComponents();
	}
	
	protected abstract PathBuilder getFirstPathBuilder(BuildingSite site);
	
	protected abstract void makeOriginSafe();
}

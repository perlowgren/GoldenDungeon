package com.spirangle.minerva.path;

public interface PathMap {
	public int getMapWidth();
	public int getMapHeight();
	public int getMapStyle();
	public int getMapDir(Path p,PathPoint c1,PathPoint c2);
	public int getMapWeight(Path p,PathPoint fr,PathPoint to);
	public int getMapHeuristic(Path p,PathPoint c);

	public int movePathPoint(Path p,PathNode n,PathPoint c,int i);
	public void capturePathStep(Path p,PathNode n);
}


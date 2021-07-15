package com.seshat.path;

import com.seshat.path.PathFinder;
import com.seshat.path.PathFinder.PathNode;
import com.seshat.path.PathFinder.PathPoint;

public interface PathMap {
	public int getMapWidth();
	public int getMapHeight();
	public int getMapStyle();
	public int getMapDir(PathFinder p,PathPoint c1,PathPoint c2);
	public int getMapHeuristic(PathFinder p,PathPoint c);

	public PathNode getPathNode(int x,int y);

//	public int movePathPoint(PathFinder p,PathNode n,PathPoint c,int i);
}


package com.seshat.path;

import com.seshat.path.PathFinder;
import com.seshat.path.PathFinder.PathNode;
import com.seshat.path.PathFinder.PathPoint;
import com.seshat.path.PathMap;

public abstract class ObliqueMap implements PathMap {
	private static final short[] xcoords = {  0, 1, 0,-1 };
	private static final short[] ycoords = { -1, 0, 1, 0 };

	@Override
	public int getMapDir(PathFinder p,PathPoint c1,PathPoint c2) {
		PathPoint p1 = new PathPoint(c1),p2 = new PathPoint(c2);
		p.adjustDir(p1,p2);
		return p2.y==p1.y? (p2.x<p1.x? PathFinder.W : PathFinder.E) : (p2.y<p1.y? PathFinder.N : PathFinder.S);
	}

	@Override
	public int getMapHeuristic(PathFinder p,PathPoint c) {
		PathPoint p1 = new PathPoint(c),p2 = p.getDestination();
		p.adjustDir(p1,p2);
//		x = abs(x-dx),y = abs(y-dy);
//		return x>y? x+y/2 : y+x/2;
		return p1.manhattanDistance(p2);
	}
/*
	@Override
	public int movePathPoint(PathFinder p,PathNode n,PathPoint c,int i) {
		c.set(n.x+xcoords[i],n.y+ycoords[i]);
		p.adjustMove(c);
		return i<3? 1 : -1;
	}*/
}


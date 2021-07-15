package com.seshat.path;

import com.seshat.path.PathFinder;
import com.seshat.path.PathFinder.PathNode;
import com.seshat.path.PathFinder.PathPoint;
import com.seshat.path.PathMap;

public abstract class HexagonalMap implements PathMap {
	private static final short[][] xcoords = {{  0, 0, 0, 0,-1,-1 },{  0, 1, 1, 0, 0, 0 }};
	private static final short[] ycoords = { -2,-1, 1, 2, 1,-1 };

	@Override
	public int getMapDir(PathFinder p,PathPoint c1,PathPoint c2) {
		PathPoint p1 = new PathPoint(c1),p2 = new PathPoint(c2);
		p.adjustDir(p1,p2);
		p2.x -= p1.x+(p1.y&1);
		if(Math.abs(p2.y-p1.y)==2) return p2.y<p1.y? PathFinder.N : PathFinder.S;
		return p2.y<p1.y? (p2.x<0? PathFinder.NW : PathFinder.NE) : (p2.x<0? PathFinder.SW : PathFinder.SE);
	}

	@Override
	public int getMapHeuristic(PathFinder p,PathPoint c) {
		PathPoint p1 = new PathPoint(c),p2 = p.getDestination();
		p.adjustDir(p1,p2);
		p1.x = (short)(Math.abs((int)(p1.x-p2.x))*2);
		p1.y = (short)Math.abs((int)(p1.y-p2.y));
		return p1.x>p1.y? p1.x+p1.y/2 : p1.y+p1.x/2;
//		return p1.x+p1.y;
//		return (p1.x+p1.y)*2;
//		return (p1.x>p1.y? p1.x : p1.y);
	}
/*
	@Override
	public int movePathPoint(PathFinder p,PathNode n,PathPoint c,int i) {
		c.set(n.x+xcoords[n.y&1][i],n.y+ycoords[i]);
		p.adjustMove(c);
		return i<5? 1 : -1;
	}*/
}


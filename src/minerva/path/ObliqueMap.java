package com.spirangle.minerva.path;

public abstract class ObliqueMap implements PathMap {
	private static final short[] xcoords = {  0, 1, 0,-1 };
	private static final short[] ycoords = { -1, 0, 1, 0 };

	public int getMapDir(Path p,PathPoint c1,PathPoint c2) {
		PathPoint p1 = new PathPoint(c1),p2 = new PathPoint(c2);
		p.adjustDir(p1,p2);
		return p2.y==p1.y? (p2.x<p1.x? Path.W : Path.E) : (p2.y<p1.y? Path.N : Path.S);
	}

	public int getMapHeuristic(Path p,PathPoint c) {
		PathPoint p1 = new PathPoint(c),p2 = p.getDestination();
		p.adjustDir(p1,p2);
//		x = abs(x-dx),y = abs(y-dy);
//		return x>y? x+y/2 : y+x/2;
		return p1.manhattanDistance(p2);
	}

	public int movePathPoint(Path p,PathNode n,PathPoint c,int i) {
		c.set(n.x+xcoords[i],n.y+ycoords[i]);
		p.adjustMove(c);
		return i<3? 1 : -1;
	}
}


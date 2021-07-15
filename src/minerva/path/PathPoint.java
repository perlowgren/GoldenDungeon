package com.spirangle.minerva.path;

public class PathPoint {
	public short x;		//!< X-coordinate of point
	public short y;		//!< Y-coordinate of point

	public PathPoint(int x,int y) {
		this.x = (short)x;
		this.y = (short)y;
	}

	public PathPoint(PathPoint p) {
		x = p.x;
		y = p.y;
	}

	public void set(int x,int y) {
		this.x = (short)x;
		this.y = (short)y;
	}

	public int manhattanDistance(PathPoint c) {
		return Math.abs(x-c.x)+Math.abs(y-c.y);
	}

	public int diagonal_distance(PathPoint c) {
		return Math.max(Math.abs(x-c.x),Math.abs(y-c.y));
	}

	public int euclidianDistance(PathPoint c) {
		int x = this.x-c.x;
		int y = this.y-c.y;
		return (int)Math.sqrt(x*x+y*y);
		
	}

	public int isometricDistance(PathPoint c) {
		return Math.max(Math.abs(x-c.x)*2,Math.abs(y-c.y));
	}

	public int hexagonalDistance(PathPoint c) {
		int x = Math.abs(this.x-c.x)*2;
		int y = Math.abs(this.y-c.y);
		if((y&1)==0 && x!=0) ++x;
		if(y>1) y = y/2;
		return x>y? x : x+y;
	}
}



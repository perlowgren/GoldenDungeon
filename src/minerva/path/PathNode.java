package com.spirangle.minerva.path;

public class PathNode extends PathPoint {
	int key;					//!< Hash key. Key used in hashtable.
	short s;					//!< s value used in A* algorithm. Steps, the number of steps from starting point.
	short g;					//!< g value used in A* algorithm. Accumulated move-cost of all steps to and including this node.
	short h;					//!< h value used in A* algorithm. Heuristic value, approximate distance to destination.
	PathNode parent;		//!< Parent node in path. Previous step in the path. When a shorter path is found for any node; parent, s and g are adjusted accordingly.
	PathNode open;			//!< Open node. Linked list in the open stack.
	PathNode closed;		//!< Closed node. Linked list in the closed hashtable.

	/** Constructor
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param g g value used in the A* algorithm
	 * @param h h value used in the A* algorithm
	 * @param p Parent node, previous step in the path */
	PathNode(int x,int y,int g,int h,PathNode p) {
		super(x,y);
		this.g = (short)g;
		this.h = (short)h;
		parent = p;
		open = null;
		closed = null;
		key = (x<<16)|y;
		s = (short)(p!=null? p.s+1 : 0);
	}
}



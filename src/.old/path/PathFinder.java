package com.seshat.path;

import com.badlogic.gdx.Gdx;
import com.seshat.path.PathMap;
import com.seshat.path.Path;

public class PathFinder {
	public static final int VWRAP            = 0x0001;		//!< Wrap the map that is searched vertically, meaning top and bottom sides overlap.
	public static final int HWRAP            = 0x0002;		//!< Wrap the map that is searched horizontally, meaning left and right sides overlap.

	public static final int OBLIQUE          = 0x0004;		//!< The map is oblique (rectangular tiles).
	public static final int ISOMETRIC        = 0x0008;		//!< The map is hexagonal (hexagon shaped tiles).
	public static final int HEXAGONAL        = 0x0010;		//!< The map is isometric (diamond shaped tiles).

	public static final int CANNOT_MOVE      = -1;			//!< Will no include this coordinate in the search
	public static final int AVOID_MOVE       = -2;			//!< Is not included as a valid coordinate in the search, but accepted as a destination.
	public static final int DEFINITE_TARGET  = -3;			//!< Found the definite target in this step, end search.
	public static final int POSSIBLE_TARGET  = -4;			//!< Found a possible target in this step, continue searching.

	public static final int C              = 0;				//!< Centre
	public static final int N              = 1;				//!< North
	public static final int E              = 2;				//!< East
	public static final int S              = 3;				//!< South
	public static final int W              = 4;				//!< West
	public static final int NE             = 5;				//!< North-East
	public static final int SE             = 6;				//!< South-East
	public static final int SW             = 7;				//!< South-West
	public static final int NW             = 8;				//!< North-West
	public static final int NNE            = 9;				//!< North-North-East
	public static final int ENE            = 10;				//!< East-North-East
	public static final int ESE            = 11;				//!< East-South-East
	public static final int SSE            = 12;				//!< South-South-East
	public static final int SSW            = 13;				//!< South-South-West
	public static final int WSW            = 14;				//!< West-South-West
	public static final int WNW            = 15;				//!< West-North-West
	public static final int NNW            = 16;				//!< North-North-West
	public static final int UP             = 17;				//!< Up
	public static final int DN             = 18;				//!< Down
	public static final int R              = 19;				//!< Right
	public static final int L              = 20;				//!< Left
	public static final int IN             = 21;				//!< In
	public static final int OUT            = 22;				//!< Out

	public static class PathPoint {
		public short x;
		public short y;

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

		public void set(PathPoint p) {
			x = p.x;
			y = p.y;
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

	public static class PathNode extends PathPoint {
		/** Steps, the number of steps from starting point (s value used in A* algorithm). */
		public short s;
		/** Accumulated move-cost of all steps to and including this node (g value used in A* algorithm). */
		public short g;
		/** Heuristic value, approximate distance to destination (h value used in A* algorithm). */
		public short h;
		/** Parent node in path - previous step in the path; when a shorter path is found for any node: parent, s and g are adjusted accordingly. */
		public PathNode parent;
		/** Open node - linked list in the open stack. */
		public PathNode open;
		/** Closed node - linked list in the closed list. */
		public PathNode closed;
		/** Node exits - possible exit nodes from this point; usually adjacent. */
		public PathNode[] exits;
		/** Directory of movement from this step to next step. */
		public byte dir;

		/** Constructor
		 * @param x X-coordinate
		 * @param y Y-coordinate
		 * @param g g value used in the A* algorithm
		 * @param h h value used in the A* algorithm
		 * @param p Parent node, previous step in the path */
		public PathNode(int x,int y,int g,int h,PathNode p) {
			super(x,y);
			open = null;
			closed = null;
			exits = null;
//Gdx.app.log("PathNode","new(x: "+x+", y: "+y+", g: "+g+", h: "+h+")");
			updatePathNode(g,h,p);
			dir = C;
		}

		public void updatePathNode(int g,int h,PathNode p) {
			s = (short)(p!=null? p.s+1 : 0);
			this.g = (short)g;
			this.h = (short)h;
			parent = p;
		}
	}

	/** Map */
	protected PathMap map;
	/** Width of map. */
	protected int width;
	/** Height of map. */
	protected int height;
	/** Style of map, if edges are overlapping etc. */
	protected int style;
	/** Seekeer, object used in callbacks. */
	protected Seeker seeker;
	/**  */
	protected PathPoint start;
	/**  */
	protected PathPoint dest;

	/** Open nodes in a stack. */
	protected PathNode open = null;
	/** Closed nodes in a hashtable. */
	protected PathNode closed = null;
	/** Node closest to destination, or the found target. */
	protected PathNode target = null;

	/** Constructor
	 * @param m Map, callback functions & user data
	 * @param s Style */
	public PathFinder(PathMap m) {
		map = m;
		width = m.getMapWidth();
		height = m.getMapHeight();
		style = m.getMapStyle();
		seeker = null;
		clear();
	}

	public void adjustDir(PathPoint c1,PathPoint c2) {
		if((style&HWRAP)!=0) {
			if(c1.x+width-c2.x<c2.x-c1.x) c1.x += width;
			else if(c2.x+width-c1.x<c1.x-c2.x) c2.x += width;
		}
		if((style&VWRAP)!=0) {
			if(c1.y+height-c2.y<c2.y-c1.y) c1.y += height;
			else if(c2.y+height-c1.y<c1.y-c2.y) c2.y += height;
		}
	}

	public void adjustMove(PathPoint c) {
		if((style&HWRAP)!=0) {
			if(c.x<0) c.x += width;
			else if(c.x>=width) c.x -= width;
		} else {
			if(c.x<0) c.x = 0;
			else if(c.x>=width) c.x = (short)(width-1);
		}
		if((style&VWRAP)!=0) {
			if(c.y<0) c.y += height;
			else if(c.y>=height) c.y -= height;
		} else {
			if(c.y<0) c.y = 0;
			else if(c.y>=height) c.y = (short)(height-1);
		}
	}

	public PathMap getMap() { return map; }
	public Seeker getSeeker() { return seeker; }
	public PathPoint getStart() { return new PathPoint(start); }
	public PathPoint getDestination() { return new PathPoint(dest); }

	public boolean isWithinReach(int x,int y) { return map.getPathNode(x,y)!=null; }

	public boolean isOpen(int x,int y) {
		if(open!=null)
			for(PathNode n=map.getPathNode(x,y),p=open; p!=null; p=p.open)
				if(p==n) return true;
		return false;
	}

	public int getSteps(int x,int y) { PathNode n = map.getPathNode(x,y);return n!=null? n.s : -1; }
	public int getWeight(int x,int y) { PathNode n = map.getPathNode(x,y);return n!=null? n.g : -1; }

	public PathPoint getParent(int x,int y) {
		PathNode n = map.getPathNode(x,y);
		if(n!=null && n.parent!=null) return new PathPoint(n.parent.x,n.parent.y);
		return null;
	}

	public Path getPath(int x,int y) { return getPath(map.getPathNode(x,y)); }

	protected Path getPath(PathNode n) {
//Gdx.app.log("PathFinder","getPath()");
		Path t = null;
		if(n!=null) {
			int i;
			PathNode p1 = null,p2 = null;
//char mem[height][width];memset(mem,' ',width*height);
//for(y=0; y<height; ++y) for(x=0; x<width; ++x) if(get(x,y)) mem[y][x] = '+';
			if(n!=null && n.g!=0) {
//debug_output("PathFinder::search(p.x=%d,p.y=%d,p.g=%d)\n",p1->x,p1->y,p1->g);
				for(p1=n,i=0; p1!=null && p1.parent!=p1; ++i,p1=p1.parent);
//Gdx.app.log("PathFinder","getPath("+i+": x: "+p1.x+", y: "+p1.y+")");

//Gdx.app.log("PathFinder","getPath(trail.length: "+i+")");
				if(p1==null && i>1) {
					t = new Path(i);
					for(p1=n,p2=null,i=t.len-1; p1!=null; --i,p2=p1,p1=p1.parent) {
//debug_output("key=%04x\tx1=%d\ty1=%d\tx2=%d\ty2=%d\tdir=%d\n",
//p1->key,p1->x,p1->y,p2? p2->x : -1,p2? p2->y : -1,p2? dir(*this,p1->x,p1->y,p2->x,p2->y) : 5);
						p1.dir = (byte)(p2!=null? map.getMapDir(this,p1,p2) : C);
						t.setStep(i,p1);
//Gdx.app.log("PathFinder","getPath(i: "+i+", x: "+t.steps[i].x+", y: "+t.steps[i].y+", dir: "+t.steps[i].dir+")");
//mem[p1->y][p1->x] = (char)('a'+((t->len-1)%('z'-'a')));
					}
//debug_output("PathFinder::search()\n");
				}
			}
//for(y=0; y<height; ++y) {
//for(x=0; x<width; ++x) putc(mem[y][x],stderr);
//fputc('\n',stderr);fflush(stderr);}
		}
		return t;
	}

	/** Calculate the A* path search
	 * @param sk Seeker to perform search, user data
	 * @param x1 X-coordinate to start search
	 * @param y1 Y-coordinate to start search
	 * @param x2 X-coordinate to end search
	 * @param y2 Y-coordinate to end search
	 * @param l Length to search. No nodes at a distance from (x1,y1) greater than l are searched. If set to zero, all possible nodes are searched.
	 * @return A Path-object containing the found path, or to the closest point. If no path is found, null is returned. */
	public Path searchPath(Seeker sk,int x1,int y1,int x2,int y2,int l) {
		int i/*,d*/,c;
//		PathPoint p;
		PathNode[] e;
		PathNode p0 = null,p1 = null,p2 = null;
Gdx.app.log("PathFinder","searchPath(x1: "+x1+", y1: "+y1+", x2: "+x2+", y2: "+y2+")");
		clear();
		seeker = sk;
		if(x1==x2 && y1==y2) return null;
		start = new PathPoint(x1,y1);
		dest = new PathPoint(x2,y2);
//debug_output("PathFinder::searchPath(x1=%d,y1=%d,x2=%d,y2=%d)\n",x1,y1,x2,y2);
		p0 = map.getPathNode(start.x,start.y);
		p0.updatePathNode(0,map.getMapHeuristic(this,start),null);
		target = p0;
		push(p0);
//debug_output("PathFinder::searchPath(x1=%d,y1=%d,x2=%d,y2=%d)\n",x1,y1,x2,y2);
//		p = new PathPoint(x1,y1);
		while(open!=null) {
			p1 = pop();
//Gdx.app.log("PathFinder","searchPath(x1: "+p1.x+", y1: "+p1.y+
//", x2: "+(p1.parent!=null? p1.parent.x : -1)+", y2: "+(p1.parent!=null? p1.parent.y : -1)+", s: "+p1.s+", g: "+p1.g+", h: "+p1.h+")");
			e = p1.exits;
			if((l>0 && p1.s>=l) || e==null || e.length==0) continue;
			for(i=0; i<e.length; ++i) {
//				d = map.movePathPoint(this,p1,p,i);
				p2 = e[i];
				c = seeker.getPathStepCost(this,p1,p2);
				if(p2.x==dest.x && p2.y==dest.y) {
					if(c!=CANNOT_MOVE) {
//						p2 = map.getPathNode(p.x,p.y);
						if(p2.parent==null) {
							p2.closed = closed;
							closed = p2;
						}
						p2.updatePathNode(p1.g+1,0,p1);
						target = p2;
					}
					return getPath(target);
				} else if(c>=0) {
//					p2 = map.getPathNode(p.x,p.y);
					if(p2==p0) continue;
					if(p2.parent==null) {
						p2.updatePathNode(p1.g+c,map.getMapHeuristic(this,p2),p1);
						p2.closed = closed;
						closed = p2;
						if(p2.h<target.h || (p2.h==target.h && p2.g<target.g)) target = p2;
						push(p2);
					} else if(p1.g+c<p2.g) {
						remove(p2);
						p2.updatePathNode(p1.g+c,p2.h,p1);
						push(p2);
					}
				}
			}
		}
		return getPath(target);
	}

	/** Calculate the reach for a number of moves on a weighted map
	 * @param sk Seeker to perform search, user data
	 * @param x X-coordinate to start search
	 * @param y Y-coordinate to start search
	 * @param s Maximum number of steps, or zero for unlimited
	 * @param m Maximum amount of moves compared to weighted steps */
	public void searchReach(Seeker sk,int x,int y,int s,int m) {
		int i/*,d*/,c;
//		PathPoint p;
		PathNode[] e;
		PathNode p0 = null,p1 = null,p2 = null;
Gdx.app.log("PathFinder","searchReach(x: "+x+", y: "+y+", s: "+s+", m: "+m+")");
		clear();
		seeker = sk;
		if(x<0 || y<0 || x>=width || y>=height || (s==0 && m==0)) return;
		start = new PathPoint(x,y);
		dest = new PathPoint(-1,-1);
		p0 = map.getPathNode(x,y);
		p0.updatePathNode(0,0,null);
		push(p0);
//		p = new PathPoint(x,y);
		while(open!=null) {
			p1 = pop();
//debug_output("key=%08x\tx1=%d\ty1=%d\tx2=%d\ty2=%d\ts=%d\tg=%d\th=%d\n",p1->key,p1->x,p1->y,
//p1->parent? p1->parent->x : -1,p1->parent? p1->parent->y : -1,p1->s,p1->g,p1->h);
			e = p1.exits;
			if(e==null || e.length==0) continue;
			for(i=0; i<e.length; ++i) {
				p2 = e[i];
//				d = map.movePathPoint(this,p1,p,i);
				c = seeker.getReachStepCost(this,p1,p2);
				if(c<0 || (s>0 && p1.s+1>s) || (m>0 && p1.g+c>m) || p2==p0) continue;
//				p2 = map.getPathNode(p.x,p.y);
//				if(p2==p0) continue;
				if(p2.parent==null) {
					p2.updatePathNode(p1.g+c,0,p1);
					p2.closed = closed;
					closed = p2;
					push(p2);
				} else if(p1.g+c<p2.g) {
					remove(p2);
					p2.updatePathNode(p1.g+c,0,p1);
					push(p2);
				}
			}
		}
	}

	/** Calculate the reach for a number of moves on a weighted map
	 * @param sk Seeker to perform search, user data
	 * @param x X-coordinate to start search
	 * @param y Y-coordinate to start search
	 * @param s Maximum number of steps, or zero for unlimited
	 * @param m Maximum amount of moves compared to weighted steps */
	public Path searchTarget(Seeker sk,int x,int y,int s,int m) {
		int i/*,d*/,c;
//		PathPoint p;
		PathNode[] e;
		PathNode p0 = null,p1 = null,p2 = null,p3 = null;
		boolean pt;
		int t = seeker.countTargets(this);
Gdx.app.log("PathFinder","searchTarget(x: "+x+", y: "+y+", s: "+s+", m: "+m+")");
		clear();
		seeker = sk;
		if(x<0 || y<0 || x>=width || y>=height || (s==0 && m==0) || t==0) return null;
		start = new PathPoint(x,y);
		dest = new PathPoint(-1,-1);
		p0 = map.getPathNode(x,y);
		p0.updatePathNode(0,0,null);
		push(p0);
//		p = new PathPoint(x,y);
		while(open!=null) {
			p1 = pop();
			e = p1.exits;
			if(e==null || e.length==0) continue;
//debug_output("key=%08x\tx1=%d\ty1=%d\tx2=%d\ty2=%d\ts=%d\tg=%d\th=%d\n",p1->key,p1->x,p1->y,
//p1->parent? p1->parent->x : -1,p1->parent? p1->parent->y : -1,p1->s,p1->g,p1->h);
			for(i=0; i<e.length; ++i) {
				p2 = e[i];
//				d = map.movePathPoint(this,p1,p,i);
				c = seeker.getTargetStepCost(this,p1,p2);
				if((s>0 && p1.s+1>s) || (m>0 && p1.g+c>m) || p2==p0) continue;
				pt = false;
				if(c==DEFINITE_TARGET) {
//					p2 = map.getPathNode(p.x,p.y);
					if(p2.parent==null) {
						p2.closed = closed;
						closed = p2;
					}
					p2.updatePathNode(p1.g+1,0,p1);
					target = p2;
					return getPath(target);
				}
				if(c==POSSIBLE_TARGET) {
					pt = true;
					c = 0;
				}
				if(c<0) continue;
//				p2 = map.getPathNode(p.x,p.y);
//				if(p2==p0) continue;
				if(p2.parent==null) {
					p2.updatePathNode(p1.g+c,0,p1);
					p2.closed = closed;
					closed = p2;
					if(pt) {
						p3 = target;
						target = seeker.pickTarget(this,p2,p3);
						--t;
						if(t==0 || (/*End when previous target is picked: && */target==p3))
							return getPath(target);
					}
					push(p2);
				} else if(p1.g+c<p2.g) {
					remove(p2);
					p2.updatePathNode(p1.g+c,0,p1);
					push(p2);
				}
			}
		}
		return getPath(target);
	}

	protected void push(PathNode p) {
		PathNode p0 = null,p1 = open;
//		while(p1 && (p->g>p1->g || (p->g==p1->g && p->h>p1->h))) p0 = p1,p1 = p1->open;
//		while(p1 && (p->h>p1->h || (p->h==p1->h && p->g>p1->g))) p0 = p1,p1 = p1->open;
		while(p1!=null && p.g+p.h>p1.g+p1.h) {
			p0 = p1;
			p1 = p1.open;
		}
		if(p0==null) {
			p.open = open;
			open = p;
		} else {
			p0.open = p;
			p.open = p1;
		}
	}

	protected PathNode pop() {
		PathNode p = open;
		if(p!=null) {
			open = p.open;
			p.open = null;
		}
		return p;
	}

	protected void remove(PathNode p) {
		if(open==null) return;
		if(open!=p) {
			PathNode p1 = open;
			while(p1.open!=null && p1.open!=p) p1 = p1.open;
			if(p1.open==p) {
				p1.open = p1.open.open;
				p.open = null;
			}
		} else {
			open = open.open;
			p.open = null;
		}
	}

	protected void clear() {
		for(PathNode p=closed; p!=null; p=p.closed)
			p.parent = null;
		open = null;
		closed = null;
		target = null;
	}
};


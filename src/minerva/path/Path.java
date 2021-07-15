package com.spirangle.minerva.path;

public class Path {
	public static final int VWRAP				= 0x0001;		//!< Wrap the map that is searched vertically, meaning top and bottom sides overlap.
	public static final int HWRAP				= 0x0002;		//!< Wrap the map that is searched horizontally, meaning left and right sides overlap.

	public static final int OBLIQUE			= 0x0004;		//!< The map is oblique (rectangular tiles).
	public static final int ISOMETRIC		= 0x0008;		//!< The map is hexagonal (hexagon shaped tiles).
	public static final int HEXAGONAL		= 0x0010;		//!< The map is isometric (diamond shaped tiles).

	public static final int CANNOT_MOVE		= 0xffff;		//!< Will no include this coordinate in the search
	public static final int AVOID_MOVE		= 0xfffe;		//!< Is not included as a valid coordinate in the search, but accepted as a destination.

	public static final int C					= 0;				//!< Centre
	public static final int N					= 1;				//!< North
	public static final int E					= 2;				//!< East
	public static final int S					= 3;				//!< South
	public static final int W					= 4;				//!< West
	public static final int NE					= 5;				//!< North-East
	public static final int SE					= 6;				//!< South-East
	public static final int SW					= 7;				//!< South-West
	public static final int NW					= 8;				//!< North-West
	public static final int NNE				= 9;				//!< North-North-East
	public static final int ENE				= 10;				//!< East-North-East
	public static final int ESE				= 11;				//!< East-South-East
	public static final int SSE				= 12;				//!< South-South-East
	public static final int SSW				= 13;				//!< South-South-West
	public static final int WSW				= 14;				//!< West-South-West
	public static final int WNW				= 15;				//!< West-North-West
	public static final int NNW				= 16;				//!< North-North-West
	public static final int UP					= 17;				//!< Up
	public static final int DN					= 18;				//!< Down
	public static final int R					= 19;				//!< Right
	public static final int L					= 20;				//!< Left
	public static final int IN					= 21;				//!< In
	public static final int OUT				= 22;				//!< Out

	public static int created;
	public static int deleted;

	protected PathMap map;		//!< Map.
	protected int width;			//!< Width of map.
	protected int height;		//!< Height of map.
	protected int style;			//!< Style of map, if edges are overlapping etc.
	protected Object obj;		//!< Object used in callbacks.
	protected PathPoint start;
	protected PathPoint dest;

	protected PathNode open;			//!< Open nodes in a stack.
	protected PathNode[] closed;		//!< Closed nodes in a hashtable.
	protected PathNode closest;		//!< Node closest to target.
	protected int cap;					//!< Capacity of hashtable.
	protected int sz;						//!< Number of nodes in the hashtable.
	protected int full;					//!< Load of hashtable.

	/** Constructor
	 * @param m Map, callback functions & user data
	 * @param s Style */
	public Path(PathMap m) {
		map = m;
		width = m.getMapWidth();
		height = m.getMapHeight();
		style = m.getMapStyle();
		obj = null;
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
	public Object getObject() { return obj; }
	public PathPoint getStart() { return new PathPoint(start); }
	public PathPoint getDestination() { return new PathPoint(dest); }

	public boolean isWithinReach(int x,int y) { return get(x,y)!=null; }

	public boolean isOpen(int x,int y) {
		if(open!=null)
			for(PathNode n=get(x,y),p=open; p!=null; p=p.open)
				if(p==n) return true;
		return false;
	}

	public int getSteps(int x,int y) { PathNode n = get(x,y);return n!=null? n.s : -1; }
	public int getWeight(int x,int y) { PathNode n = get(x,y);return n!=null? n.g : -1; }

	public PathPoint getParent(int x,int y) {
		PathNode n = get(x,y);
		if(n!=null && n.parent!=null) return new PathPoint(n.parent.x,n.parent.y);
		return null;
	}

	public Trail getTrail(int x,int y) { return getTrail(get(x,y)); }

	protected Trail getTrail(PathNode n) {
		Trail t = null;
		if(n!=null) {
			int i;
			PathNode p1 = null,p2 = null;
//char mem[height][width];memset(mem,' ',width*height);
//for(y=0; y<height; ++y) for(x=0; x<width; ++x) if(get(x,y)) mem[y][x] = '+';
			if(n!=null && n.g!=0) {
//debug_output("Path::search(p.x=%d,p.y=%d,p.g=%d)\n",p1->x,p1->y,p1->g);
				for(p1=n,i=0; p1!=null && p1.parent!=p1; ++i,p1=p1.parent);
//debug_output("Path::search(trail.lenght=%d)\n",(int)t->len);
				if(p1==null && i>1) {
					t = new Trail(obj,i);
					for(p1=n,p2=null,i=t.len-1; p1!=null; --i,p2=p1,p1=p1.parent) {
//debug_output("key=%04x\tx1=%d\ty1=%d\tx2=%d\ty2=%d\tdir=%d\n",
//p1->key,p1->x,p1->y,p2? p2->x : -1,p2? p2->y : -1,p2? dir(*this,p1->x,p1->y,p2->x,p2->y) : 5);
						t.setStep(i,p1.x,p1.y,(p2!=null? map.getMapDir(this,p1,p2) : C));
//debug_output("Path::trail[%d]: x=%d, y=%d, dir=%d\n",i,(int)t->trail[i].x,(int)t->trail[i].y,(int)t->trail[i].dir);
//mem[p1->y][p1->x] = (char)('a'+((t->len-1)%('z'-'a')));
					}
//debug_output("Path::search()\n");
				}
			}
//for(y=0; y<height; ++y) {
//for(x=0; x<width; ++x) putc(mem[y][x],stderr);
//fputc('\n',stderr);fflush(stderr);}
		}
		return t;
	}

	/** Calculate the A* path search
	 * @param o Object to perform search, user data
	 * @param x1 X-coordinate to start search
	 * @param y1 Y-coordinate to start search
	 * @param x2 X-coordinate to end search
	 * @param y2 Y-coordinate to end search
	 * @param l Length to search. No nodes at a distance from (x1,y1) greater than l are searched. If set to zero, all possible nodes are searched.
	 * @return A Trail-object containing the found path, or to the closest point. If no path is found, NULL is returned. */

	public Trail searchPath(Object o,int x1,int y1,int x2,int y2,int l) {
		int i,d,c;
		PathPoint p;
		PathNode p1 = null,p2 = null;
//debug_output("Path::searchPath(x1=%d,y1=%d,x2=%d,y2=%d)\n",x1,y1,x2,y2);
		clear();
		obj = o;
		if(x1==x2 && y1==y2) return null;
		start = new PathPoint(x1,y1);
		dest = new PathPoint(x2,y2);
//debug_output("Path::searchPath(x1=%d,y1=%d,x2=%d,y2=%d)\n",x1,y1,x2,y2);
		cap = map.getMapHeuristic(this,start)*8+1;
		p1 = new PathNode(start.x,start.y,0,map.getMapHeuristic(this,start),null);
		closest = p1;
		put(p1);
		push(p1);
//debug_output("Path::searchPath(x1=%d,y1=%d,x2=%d,y2=%d)\n",x1,y1,x2,y2);
		p = new PathPoint(x1,y1);
		while(open!=null) {
			map.capturePathStep(this,open);
			p1 = pop();
//debug_output("key=%08x\tx1=%d\ty1=%d\tx2=%d\ty2=%d\ts=%d\tg=%d\th=%d\n",p1->key,p1->x,p1->y,
//p1->parent? p1->parent->x : -1,p1->parent? p1->parent->y : -1,p1->s,p1->g,p1->h);
			if(l<=0 || p1.s<l)
				for(i=0,d=0; d!=-1; ++i) {
					d = map.movePathPoint(this,p1,p,i);
					c = map.getMapWeight(this,p1,p);
					if(p.x==dest.x && p.y==dest.y) {
						if(c!=CANNOT_MOVE) {
							p1 = new PathNode(p.x,p.y,p1.g+1,0,p1);
							closest = p1;
							put(p1);
						}
						return getTrail(closest);
					} else if(c!=CANNOT_MOVE && c!=AVOID_MOVE) {
						if((p2=get(p))!=null) {
							if(p1.g+c<p2.g) {
								remove(p2);
								p2.parent = p1;
								p2.g = (short)(p1.g+c);
								p2.s = (short)(p1.s+1);
								push(p2);
							}
						} else {
							p2 = new PathNode(p.x,p.y,p1.g+c,map.getMapHeuristic(this,p),p1);
							if(p2.h<closest.h || (p2.h==closest.h && p2.g<closest.g)) closest = p2;
							put(p2);
							push(p2);
						}
					}
				}
		}
		return getTrail(closest);
	}

	/** Calculate the reach for a number of moves on a weighted map
	 * @param o Object to perform search, user data
	 * @param x1 X-coordinate to start search
	 * @param y1 Y-coordinate to start search
	 * @param x2 X-coordinate to end search
	 * @param y2 Y-coordinate to end search
	 * @param l Length to search. No nodes at a distance from (x1,y1) greater than l are searched. If set to zero, all possible nodes are searched.
	 * @return A Trail-object containing the found path, or to the closest point. If no path is found, NULL is returned. */
	public void searchReach(Object o,int x,int y,int s,int m) {
		int i,d,c;
		PathPoint p;
		PathNode p1 = null,p2 = null;
//debug_output("Path::searchReach(x=%d,y=%d)\n",x,y);
		clear();
		obj = o;
		if(x<0 || y<0 || x>=width || y>=height || (s==0 && m==0)) return;
		start = new PathPoint(x,y);
		dest = new PathPoint(-1,-1);
		cap = m*m*3;
		p1 = new PathNode(x,y,0,0,null);
		put(p1);
		push(p1);
		p = new PathPoint(x,y);;
		while(open!=null) {
			map.capturePathStep(this,open);
			p1 = pop();
//debug_output("key=%08x\tx1=%d\ty1=%d\tx2=%d\ty2=%d\ts=%d\tg=%d\th=%d\n",p1->key,p1->x,p1->y,
//p1->parent? p1->parent->x : -1,p1->parent? p1->parent->y : -1,p1->s,p1->g,p1->h);
			for(i=0,d=0; d!=-1; ++i) {
				d = map.movePathPoint(this,p1,p,i);
				c = map.getMapWeight(this,p1,p);
				if(c!=CANNOT_MOVE && c!=AVOID_MOVE) {
					if((p2=get(p))!=null) {
						if(p1.g+c<p2.g) {
							remove(p2);
							p2.parent = p1;
							p2.g = (short)(p1.g+c);
							p2.s = (short)(p1.s+1);
							push(p2);
						}
					} else if((s==0 || p1.s+1<=s) && (m==0 || p1.g+c<=m)) {
						p2 = new PathNode(x,y,p1.g+c,0,p1);
						put(p2);
						push(p2);
					}
				}
			}
		}
	}

	protected void put(PathNode n) {
		if(sz==full) rehash();
		int h = n.key%cap;
		n.closed = closed[h];
		closed[h] = n;
		++sz;
	}

	protected PathNode get(int x,int y) { return get((x<<16)|y); }
	protected PathNode get(PathPoint c) { return get((c.x<<16)|c.y); }

	protected PathNode get(int key) {
		if(sz==0) return null;
		PathNode n = closed[key%cap];
		while(n!=null && n.key!=key) n = n.closed;
		return n;
	}

	protected void rehash() {
		if(closed==null) closed = new PathNode[cap];
		else {
			int c = cap<<1,i,h;
			if((c&1)==0) ++c;
			PathNode[] t = new PathNode[c];
			PathNode n1,n2;
			for(i=0; i<cap; ++i)
				if((n1=closed[i])!=null)
					while(n1!=null) {
						n2 = n1.closed;
						h = n1.key%c;
						n1.closed = t[h];
						t[h] = n1;
						n1 = n2;
					}
			closed = t;
			cap = c;
		}
		full = cap>>1;
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
		open = null;
		closed = null;
		closest = null;
		cap = 11;
		sz = 0;
		full = 0;
	}
};


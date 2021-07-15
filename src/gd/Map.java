package com.seshat.gd;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Random;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Json;
import com.seshat.path.ObliqueMap;
import com.seshat.path.PathFinder;
import com.seshat.path.PathFinder.PathNode;
import com.seshat.path.PathFinder.PathPoint;
import com.seshat.path.Path;

/**
 * This class is used for reading and writing to and from JSON, using the Json class.
 */
class MapJson {
	public long id;
	public long seed;
	public int status;
	public int width;
	public int height;
	public int[] floors;
	public int[] walls;
};

/**
 * The Map class creates and contains the game map and it's tiles and objects.
 * It does not handle user interaction directly, but records all events which can
 * be extracted and stored, to repeat the same scenario.
 */
public class Map extends ObliqueMap implements Data {
	private static final String className = "Map";

	private Main main;

	/** Random sequence for map. Seeded once before a match, the entire match should be
	 * duplicated with the same seed and identically timed user input. */
	private Random rand;

	/** Status of map. */
	private long id = 0;

	/** Status of map. */
	private long seed = 0;

	/** Status of map. */
	private int status = 0;

	/** Width of map in tiles. */
	private int width = 0;

	/** Height of map in tiles. */
	private int height = 0;

	/** Number of tiles all together, width*height. */
	private int size = 0;

	/** Width of map in isometric points (width+height*(tile-width/2)). */
	private int widthPts = 0;

	/** Height of map in isometric points (width+height*(tile-height/2)). */
	private int heightPts = 0;

	/** Tiles in the map. */
	private Tile[] tiles = null;

	private int[] tileIndex = null;

	public PathFinder pathFinder = null;

	public int time = 0;

	public int[] die20 = null;

	/** X-coordinate in map pointed at by user. */
	public float focusX = -1.0f;

	/** Y-coordinate in map pointed at by user. */
	public float focusY = -1.0f;

	/** Tile with user focus. */
	public Tile focusTile = null;

	/** Unit with user focus. */
	public Unit focus = null;

	/** X-coordinate in map for focus Unit when dragging. */
	public float focusDragX = -1.0f;

	/** Y-coordinate in map for focus Unit when dragging. */
	public float focusDragY = -1.0f;

	/** Linked list of units. */
	public Unit units = null;

	/** The defending Wizard of the Map. */
	public Wizard defender = null;

	/** The attacking Wizard. */
	public Wizard attacker = null;

	public Map(Main m) {
		main = m;
	}

	public String toJson() {
		int i,n,x,y;
		String json = "{\n"+
		        "  \"id\":     "+id+",\n"+
		        "  \"seed\":   "+seed+",\n"+
		        "  \"status\": "+status+",\n"+
		        "  \"width\":  "+width+",\n"+
		        "  \"height\": "+height+",\n"+
		        "  \"floors\":  [";
		for(i=0; i<size; ++i) {
			if((i%width)==0) json += "\n    ";
			n = (tiles[i]!=null? tiles[i].floor : 0);
			if(n<10) json += ' ';
			json += n;
			if(i<size-1) json += ',';
		}
		json += "\n  ],\n"+
		        "  \"walls\":  [";
		for(i=0; i<size; ++i) {
			if((i%width)==0) json += "\n    ";
			n = (tiles[i]!=null && tiles[i].wall!=null? tiles[i].wall.type.id : 0);
			if(n<10) json += ' ';
			json += n;
			if(i<size-1) json += ',';
		}
		json += "\n  ]\n}";
		return json;
	}

	public Main getMain() { return main; }

	public void moveUnits() {
		Unit u;
		++time;
		for(u=units; u!=null; u=u.list) u.move();
		evaluateStatus();
	}

	public void evaluateStatus() {
		if(!isGameEnded()) {
			Unit u;
			int i;
			int d = 0;
			int a = 0;
			for(i=0; i<defender.team.length; ++i) {
				u = defender.team[i];
				if(u!=null && !u.isDestroyed()) ++d;
			}
			for(i=0; i<attacker.team.length; ++i) {
				u = attacker.team[i];
				if(u!=null && !u.isDestroyed()) ++a;
			}
			/*for(u=units; u!=null; u=u.list) {
				if(u.wizard==defender) ++d;
				else if(u.wizard==attacker) ++a;
			}*/
			if(d==0 || a==0)
				status |= (a==0? GAME_WINNER_DEF : GAME_WINNER_ATT);
		}
	}

	public void setStatus(int n) { status = n; }
	public void setStatus(int n,boolean s) { status = s? (status|n) : (status&~n); }
	public boolean isWinnerDefender() { return (status&GAME_WINNER_DEF)!=0; }
	public boolean isWinnerAttacker() { return (status&GAME_WINNER_ATT)!=0; }
	public boolean isGameEnded() { return (status&GAME_ENDED)!=0; }
	public boolean isHWrap() { return (status&GAME_MAP_HWRAP)!=0; }
	public boolean isVWrap() { return (status&GAME_MAP_VWRAP)!=0; }
	public boolean isOnMap(int x,int y) { return x>=0 && y>=0 && x<width && y<height; }

	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public int getWidthPoints() { return widthPts; }
	public int getHeightPoints() { return heightPts; }
	public Tile getTile(int x,int y) { return x>=0 && y>=0 && x<width && y<height? tiles[x+y*width] : null; }
	public Tile getTile(Unit u) { return tiles[(int)u.mapX+(int)u.mapY*width]; }

	@Override
	public PathNode getPathNode(int x,int y) {
		if(x>=0 && y>=0 && x<width && y<height) return (PathNode)tiles[x+y*width];
		return null;
	}

	public void setDefender(Wizard w) {
		defender = w;
		addWizard(defender);
	}

	public void setAttacker(Wizard w) {
		attacker = w;
		addWizard(attacker);
	}

	public void addWizard(Wizard w) {
		if(w.team!=null) {
			int i;
			Unit u;
			for(i=0; i<w.team.length; ++i) {
				u = w.team[i];
				if(u!=null && u.map!=this) {
					if(u.mapX>=0.0f && u.mapX<(float)(width+1) && u.mapY>=0.0f && u.mapY<(float)(height+1))
						u.setPosition(this,u.mapX,u.mapY);
					else
						u.remove();
				}
			}
		}
	}

	public void create(String data) {
		Gdx.app.log(APP_NAME,className+":create(data: "+data+")");
		Json json = new Json();
		MapJson m = json.fromJson(MapJson.class,data);
		Gdx.app.log(APP_NAME,className+":create(id: "+m.id+", seed: "+m.seed+", status: "+m.status+", width: "+m.width+", height: "+m.height+")");
		create(m.id,m.seed,m.status,m.width,m.height,m.floors,m.walls);
	}

	public void create(long i,long sd,int st,int w,int h,int[] fl,int[] wl) {
		int x,y,n,r;
		id = i;
		seed = sd;
		rand = new Random(seed);
		setStatus(st);
		clear();
		width = w;
		height = h;
		size = w*h;
		widthPts = (w+h)*TILE_WIDTH_HALF-2;
		heightPts = (w+h)*TILE_HEIGHT_HALF;
		time = 0;
		tileIndex = new int[size];
		for(n=0; n<size; ++n) {
			r = rand.nextInt();
			if(r<0) r = -r;
			tileIndex[n] = r;
		}
		tiles = new Tile[size];
		for(x=0; x<w; ++x)
			for(y=0; y<h; ++y) {
				n = x+y*w;
				tiles[n] = new Tile(this,tileIndex[n],x,y,fl[n],wl[n]);
			}
		for(n=0; n<size; ++n)
			tiles[n].makeExits();

		pathFinder = new PathFinder(this);

		die20 = createDie(20);
	}

	public int[] createDie(int d) {
		int i,n;
		int[] die = new int[d];
Gdx.app.log(APP_NAME,className+":die("+d+")");
		for(i=0; i<d; ++i) die[i] = 0;
		for(i=1; i<=d; ++i) {
			n = rand.nextInt()&0xffff;
Gdx.app.log(APP_NAME,className+":die(n: "+n+")");
			if(n<0) n = -n;
			n %= d;
			while(die[n]!=0) {
Gdx.app.log(APP_NAME,className+":die(die["+n+"]=="+die[n]+")");
				if(n<19) ++n;
				else n = 0;
			}
Gdx.app.log(APP_NAME,className+":die["+n+"]: "+i);
			die[n] = i;
		}
		return die;
	}

	public void close() {
	}

	public void clear() {
		width = 0;
		height = 0;
		size = 0;
		widthPts = 0;
		heightPts = 0;
		tiles = null;
		units = null;
		if(focus!=null) focus.setPassive(false);
		focus = null;
		focusTile = null;
		focusX = -1.0f;
		focusY = -1.0f;
	}

	public int getMapWidth() { return width; }
	public int getMapHeight() { return height; }
	public int getMapStyle() { return (isHWrap()? PathFinder.HWRAP : 0)|(isVWrap()? PathFinder.VWRAP : 0)|PathFinder.OBLIQUE; }
/*
	public int getMapWeight(PathFinder p,PathPoint fr,PathPoint to) {
		Tile t = tiles[to.x+to.y*width];
		if(t==null) return PathFinder.CANNOT_MOVE;
//		if(t.wall!=null) return PathFinder.AVOID_MOVE;
		if(t.wall!=null) return 10;
		else return 1;
	}
*/
//	public void capturePathStep(PathFinder p,PathNode n) {}

	public void setFocusPoint(float xs,float ys,float xm,float ym) {
		if(tiles==null) return;
		if(focus!=null) focus.setPassive(false);
		focus = null;
		focusTile = null;
		focusX = xm;
		focusY = ym;
		if(true) {
			Unit u;
			int x = (int)xs;
			int y = (int)ys;
Gdx.app.log(APP_NAME,className+":setFocusPoint(x: "+x+", y: "+y+")");
			for(u=units; u!=null; u=u.list) {
Gdx.app.log(APP_NAME,className+":setFocusPoint(unit: "+u.screenX+", "+u.screenY+")");
				if(u.isTouched(x,y)) focus = u;
			}
			if(focus!=null) {
				focus.setPassive(true);
				focusDragX = focus.mapX;
				focusDragY = focus.mapY;
Gdx.app.log(APP_NAME,className+":setFocusPoint(focus: "+focus.mapX+", "+focus.mapY+")");
			}
		}
		if(focus==null) {
			focusTile = getTile((int)xm,(int)ym);
			if(focusTile!=null) {
Gdx.app.log(APP_NAME,className+":setFocusPoint(focusTile: "+focusTile.mapX+", "+focusTile.mapY+")");
			}
		}
	}

	public void moveFocusPoint(float xs,float ys,float xm,float ym) {
		if(tiles==null) return;
		if(focus!=null) {
			float xd = (float)Math.floor(focusDragX+(xm-focusX))+0.5f;
			float yd = (float)Math.floor(focusDragY+(ym-focusY))+0.5f;
			focus.setPosition(this,xd,yd);
		}
	}

	public void activateFocusPoint() {
/*		if(focus!=null)
			focus.setDestination(focusX,focusY);*/
	}
}






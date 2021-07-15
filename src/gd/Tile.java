package com.seshat.gd;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.seshat.AnimationSet;
import com.seshat.path.PathFinder.PathNode;

public class Tile extends PathNode implements Data {
	public Map map;
	public int index;
	public int mapX;
	public int mapY;
	public int screenX;
	public int screenY;
	public int floor;
	public Wall wall = null;
	public Unit units = null;

	public Tile(Map m,int i,int x,int y,int fl,int wt) {
		super(x,y,0,0,null);
		map      = m;
		index    = i;
		mapX     = x;
		mapY     = y;
		screenX  = (x-y+map.getHeight())*TILE_WIDTH_HALF;
		screenY  = (x+y)*TILE_HEIGHT_HALF;
		floor    = fl;
		if(wt>0)
			wall = new Wall(this,wt);
	}

	public void makeExits() {
		int i = 0,n = 4,w = map.getWidth(),h = map.getHeight();
		if((x==0 && y==0) || (x==w-1 && y==h-1)) n = 2;
		else if(x==0 || y==0 || x==w-1 || y==h-1) n = 3;
		exits = new PathNode[n];
		if(x>0  ) exits[i++] = map.getPathNode(x-1,y  );
		if(y>0  ) exits[i++] = map.getPathNode(x,  y-1);
		if(x<w-1) exits[i++] = map.getPathNode(x+1,y  );
		if(y<w-1) exits[i++] = map.getPathNode(x,  y+1);
	}

	public void drawWall(IsometricScreen screen,SpriteBatch batch) {
		if(wall!=null)
			wall.draw(screen,batch,screenX+screen.focus.x,screenY+screen.focus.y);
	}

	public void drawUnits(IsometricScreen screen,SpriteBatch batch) {
		Unit u = units;
		for(; u!=null; u=u.tile)
			u.draw(screen,batch);
	}
};




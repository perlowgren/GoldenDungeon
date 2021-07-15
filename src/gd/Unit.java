package com.seshat.gd;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.seshat.Rect;
import com.seshat.AnimationSet;
import com.seshat.Size;
import com.seshat.Sprite;

/** The Unit class is the base class for all units in
 * the game, which are any objects that can be placed
 * in the map and are not floor or wall. Some units
 * are static, others can move.
 */
public class Unit implements Data, Target, EffectListener {
	public static final int DELETED           = 1;
	public static final int HIDDEN            = 1<<1;
	public static final int PASSIVE           = 1<<2;

	public static final int MELEE_TARGET      = 1<<8;
	public static final int RANGED_TARGET     = 1<<9;
	public static final int SPELL_TARGET      = 1<<10;
	public static final int TRAP_TARGET       = 1<<11;
	public static final int TARGET            = MELEE_TARGET|RANGED_TARGET|SPELL_TARGET|TRAP_TARGET;

	/** The Unit type is meant to be a static object
	 * containing members with data specific for the
	 * unit, e.g. name, hp etc. It works as a template
	 * for such values, which may change during game,
	 * e.g. hp lowering from damage.
	 */
	public static class UnitType {
		public int id;
		public int level;
		public String name;
		public Sprite sprite;
		public float radius;
		public float HP;

		public UnitType(int i,int lvl,String nm,float rd,float hp) {
			id      = i;
			level   = lvl;
			name    = nm;
			sprite  = null;
			radius  = rd;
			HP      = hp;
		}
	};

	public static final UnitType unitEffect = new UnitType(
		UNIT_EFFECT,1,
		"Effect",
		0.0f,
		0.0f
	);

	public static final UnitType unitStructure = new UnitType(
		UNIT_STRUCTURE,1,
		"Structure",
		0.0f,
		0.0f
	);

	public static final UnitType unitChampion = new UnitType(
		UNIT_CHAMPION,1,
		"Champion",
		0.3f,
		0.0f
	);

	public static final UnitType[] units = {null,
		unitEffect,
		unitStructure,
		unitChampion,
	null};

	public Unit list = null;
	public Unit tile = null;
	public Map map = null;
	public Wizard wizard;
	public int id;
	public int state = 0;
	public float mapX = -1.0f;
	public float mapY = -1.0f;
	public float screenX;
	public float screenY;
	public UnitType type;
	public Sprite sprite;
	public int timer = 0;
	public float maxHP;
	public float HP;

	public Unit(Wizard wz,int i,int t) {
		wizard     = wz;
		id         = i;
		type       = units[t];
		maxHP      = type.HP;
		HP         = maxHP;
		if(type.sprite!=null)
			sprite  = new AnimationSet((AnimationSet)type.sprite,0,1.0f);
	}

	public Unit(Wizard wz,int i,int t,Map m,float x,float y) {
		this(wz,i,t);
		setPosition(m,x,y);
	}

	public final boolean isDeleted() { return (state&DELETED)!=0; }

	public final void setHidden(boolean b) { if(b) state |= HIDDEN;else if(!b) state &= ~HIDDEN; }
	public final boolean isHidden() { return (state&HIDDEN)!=0; }

	public final void setPassive(boolean b) { if(b) state |= PASSIVE;else if(!b) state &= ~PASSIVE; }
	public final boolean isPassive() { return (state&PASSIVE)!=0; }

	public void setPosition(Map m,float x,float y) {
		if(map!=null) remove();
		map        = m;
		list       = map.units;
		map.units  = this;
		mapX       = -1.0f;
		mapY       = -1.0f;
		setPosition(x,y);
	}

	public void setPosition(float x,float y) {
		Tile t = map.getTile((int)mapX,(int)mapY);
		if(t!=null && t.units!=null) {
			if(t.units==this) t.units = tile;
			else { 
				Unit u = t.units;
				while(u.tile!=null && u.tile!=this) u = u.tile;
				if(u.tile==this) u.tile = tile;
			}
		}
		tile = null;

		mapX = x;
		mapY = y;
		screenX = (x-y+(float)map.getHeight())*TWH;
		screenY = (x+y)*THH;

		t = map.getTile((int)mapX,(int)mapY);
		if(t!=null) {
			if(t.units==null || screenY<=t.units.screenY) {
				tile = t.units;
				t.units = this;
			} else {
				Unit u = t.units;
				while(u.tile!=null && u.tile.screenY<screenY) u = u.tile;
				tile = u.tile;
				u.tile = this;
			}
		}
	}

	public void remove() {
		if(map!=null) {
			Unit u;
			Tile t;
			if(map.units==this) map.units = list;
			else {
				for(u=map.units; u.list!=null && u.list!=this; u=u.list);
				if(u.list==this) u.list = list;
			}
			t = map.getTile((int)mapX,(int)mapY);
			if(t!=null && t.units!=null) {
				if(t.units==this) t.units = tile;
				else { 
					u = t.units;
					while(u.tile!=null && u.tile!=this) u = u.tile;
					if(u.tile==this) u.tile = tile;
				}
			}
			map = null;
		}
		list = null;
		tile = null;
	}

	public void delete() {
		state |= DELETED;
		remove();
	}

	public float distance(float x,float y) {
		x -= mapX;
		y -= mapY;
		return (float)Math.sqrt((double)(x*x+y*y));
	}
	public float distance(Unit u) {
		if(u==null) return 0.0f;
		return distance(u.mapX,u.mapY);
	}
	public float distance(Wall w) {
		if(w==null) return 0.0f;
		return distance(w.tile);
	}
	public float distance(Tile t) {
		if(t==null) return 0.0f;
		return distance(t.mapX+0.5f,t.mapY+0.5f);
	}

	public void move() {}

	public void draw(IsometricScreen screen,SpriteBatch batch) {
		if(sprite!=null && (state&HIDDEN)==0) {
			int x = (int)Math.round(screenX)+screen.focus.x;
			int y = (int)Math.round(screenY)+screen.focus.y;
			sprite.draw(screen.view,batch,x,y);
		}
	}

	@Override
	public void setAsTarget(Unit u) {}

	@Override
	public void damage(float hp) {
		if(hp<=0.0f) return;
		HP -= hp;
		if(HP<=0.0f)
			delete();
	}

	@Override
	public boolean isDestroyed() { return HP<=0.0f; }

	@Override
	public void effectActivated(Effect e) {
		if(e instanceof FadeEffect) {
			FadeEffect fade = (FadeEffect)e;
			if(fade.getAlpha()==0.0) delete();
			else {
			}
		}
	}

	public boolean isTouched(int x,int y) {
		return sprite.isTouched((int)screenX,(int)screenY,x,y);
	}
}


package com.seshat.gd;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.seshat.AnimationSet;

/** The Wall is placed in the Tile, it covers the floor.
 * All walls has hit points and can be destroyed by units
 * during game. When destroyed, there is a small chance a
 * Wall may release some resource, depending on the type.
 */
public class Wall implements Data, Target {
	/** The WallType is a static object containing members
	 * with data specific for the Wall.
	 */
	public static class WallType {
		public int id;
		public String name;
		public AnimationSet sprite;
		public float HP;
		public float moveCost;

		public WallType(int i,String n,float hp,float mc) {
			id        = i;
			name      = n;
			sprite    = null;
			HP        = hp;
			moveCost  = mc;
		}
	};

	public static final WallType wallMud = new WallType(
		WALL_MUD,
		"Mud Wall",
		100.0f,   // HP
		1.0f      // Move cost
	);

	public static final WallType wallStone = new WallType(
		WALL_STONE,
		"Stone Wall",
		125.0f,   // HP
		1.25f     // Move cost
	);

	public static final WallType wallRustedIron = new WallType(
		WALL_RUSTED_IRON,
		"Rusted Iron Wall",
		150.0f,   // HP
		1.5f      // Move cost
	);

	public static final WallType wallSteel = new WallType(
		WALL_STEEL,
		"Steel Wall",
		175.0f,   // HP
		1.75f     // Move cost
	);

	public static final WallType wallAmethyst = new WallType(
		WALL_AMETHYST,
		"Amethyst Wall",
		200.0f,   // HP
		2.0f      // Move cost
	);

	public static final WallType[] walls = {null,
		wallMud,
		wallStone,
		wallRustedIron,
		wallSteel,
		wallAmethyst,
	null};

	public WallType type;
	public Tile tile;
	public float maxHP;
	public float HP;

	public Wall(Tile t,int wt) {
		type   = walls[wt];
		tile   = t;
		maxHP  = type.HP;
		HP     = maxHP;
	}

	public void draw(IsometricScreen screen,SpriteBatch batch,int x,int y) {
		if(type.sprite.draw(screen.view,batch,x,y,tile.index)) {
			if(HP<maxHP)
				screen.drawBar(x,y,CLIP_BAR_HP,HP/maxHP);
		}
	}

	@Override
	public void setAsTarget(Unit u) {}

	@Override
	public void damage(float hp) {
		if(hp<=0.0f) return;
		HP -= hp;
		if(HP<=0.0f) {
			tile.wall = null;
		}
	}

	@Override
	public boolean isDestroyed() { return HP<=0.0f; }
};




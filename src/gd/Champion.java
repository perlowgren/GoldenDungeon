package com.seshat.gd;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.seshat.Point;
import com.seshat.Rect;
import com.seshat.AnimationSet;
import com.seshat.Clip;
import com.seshat.path.PathFinder;
import com.seshat.path.PathFinder.PathNode;
import com.seshat.path.PathFinder.PathPoint;
import com.seshat.path.Seeker;
import com.seshat.path.Path;
import com.seshat.gd.Unit.UnitType;


/** The Champion class is the base class for all Champion
 * units. The Champions are the Units which attack or
 * defend a Wizard dungeon, and are all creatures of
 * some kind.
 */
public class Champion extends Unit implements Seeker {
	private static final String className = "Champion";

	public static final int ACTION_IDLE        = 0;
	public static final int ACTION_MOVE        = 1;
	public static final int ACTION_BATTLE      = 2;

	/** The Champion type is the same as the Unit type,
	 * but for Champions. It contains additional data
	 * specific for Champions.
	 */
	public static class ChampionType extends UnitType {
		public Clip shadow;
		public float speed;
		public float range;
		public float vision;
		public float att;
		public float def;
		public float dmg;
		public float arm;
		public float idleHeal;
		public float moveHeal;
		public int floorMoveCost;
		public float wallMoveMod;
		public float wallAttMod;
		public int idlePause;

		public ChampionType(int i,int lvl,String nm,float sp,float rd,float rn,float vs,
		                    float hp,float a,float d,float dm,float ar,float ih,float mh,
		                    int fmc,float wmm,float wam,int ip) {
			super(i,lvl,nm,rd,hp);
			shadow           = null;
			speed            = sp;
			range            = rn;
			vision           = vs;
			att              = a;
			def              = d;
			dmg              = dm;
			arm              = ar;
			idleHeal         = ih;
			moveHeal         = mh;
			floorMoveCost    = fmc;
			wallMoveMod      = wmm;
			wallAttMod       = wam;
			idlePause        = ip;
		}
	};

	public static final ChampionType championDwarf1 = new ChampionType(
		CHAMPION_DWARF_1,1,
		"Dwarf Warrior",
		0.03f,   // Speed
		0.3f,    // Radius
		0.6f,    // Range
		5.0f,    // Vision
		100.0f,  // HP
		10.0f,   // Att
		5.0f,    // Def
		10.0f,   // Damage
		10.0f,   // Armor
		1.0f,    // Idle Heal
		0.2f,    // Move Heal
		1,       // Floor move cost
		7.0f,    // Wall move modifier
		1.1f,    // Wall attack modifier
		FPS/4    // Idle pause, nr. frames
	);

	public static final ChampionType[] champions = {null,
		championDwarf1,
	null};

	/** Static data specific for the type of the Champion. */
	public ChampionType champion;
	/** The present action. */
	public int action;
	/** Movement speed. */
	public float speed;
	/** Direction the unit is facing. */
	public int dir;
	/** X-coordinate for the start position. */
	public float startX;
	/** Y-coordinate for the start position. */
	public float startY;
	/** X-coordinate for next step (tile). */
	public float stepX;
	/** Y-coordinate for next step (tile). */
	public float stepY;
	/** X-coordinate for the destination. */
	public float destX;
	/** Y-coordinate for the destination. */
	public float destY;
	/** Path of steps the unit is planning to move. */
	public Path path = null;
	/** Target unit is aiming to move to and interact with. */
	public Target target = null;
	/** Focus can be the target or an obstacle to destroy to get to the target. */
	public Target focus = null;
	/** Attack value. */
	public float att;
	/** Defence value. */
	public float def;
	/** Damage value. */
	public float dmg;
	/** Armor value. */
	public float arm;

	public Champion(Wizard wz,int i,int t) {
		super(wz,i,UNIT_CHAMPION);
		state    |= TARGET;
		champion  = champions[t];
		type      = champion;
		speed     = champion.speed;
		dir       = DIR_EAST;

		maxHP     = type.HP;
		HP        = maxHP;
		att       = champion.att;
		def       = champion.def;
		dmg       = champion.dmg;
		arm       = champion.arm;

		sprite = new AnimationSet((AnimationSet)type.sprite,0,0.12f/speed);
		setActionIdle(champion.idlePause);
	}

	public Champion(Wizard wz,int i,int t,Map m,float x,float y) {
		this(wz,i,t);
		setPosition(m,x,y);
	}

	@Override
	public void setPosition(Map m,float x,float y) {
		super.setPosition(m,x,y);
		startX    = mapX;
		startY    = mapY;
		destX     = mapX;
		destY     = mapY;
	}

	@Override
	public void delete() {
		FadeEffect fade = new FadeEffect(wizard,0,((AnimationSet)sprite).frame,FPS,1.0f,0.0f,map,mapX,mapY);
		fade.setEffectListener(this);
		super.delete();
	}

	@Override
	public void move() {
		if(isPassive()) return;

		--timer;

		if(action!=ACTION_IDLE && target!=null && target.isDestroyed())
			setActionIdle(champion.idlePause);

		if(action==ACTION_IDLE) {
			if(HP<maxHP && champion.idleHeal>0.0f) {
				if((map.time%FPS)==0) HP += champion.idleHeal;
			}
			if(timer<=0)
				setActionIdle(0);

		} else if(action==ACTION_MOVE) {
			if(path==null) {
				if(target!=null && (target instanceof Unit)) {
					Unit u = (Unit)target;
					stepX = destX = u.mapX;
					stepY = destY = u.mapY;
				} else {
					setActionIdle(champion.idlePause);
					return;
				}
			}

			int d = getDirection(mapX-stepX,mapY-stepY,dir);//dir;
			if(d!=dir) {
				dir = d;
				((AnimationSet)sprite).setAnimation(action,dir);
			}

			int x = (int)mapX,y = (int)mapY;
			/* Look for a wall to destroy: */
				  if(dir==DIR_NORTH) { y = (int)(mapY-champion.range); }
			else if(dir==DIR_EAST ) { x = (int)(mapX+champion.range); }
			else if(dir==DIR_SOUTH) { y = (int)(mapY+champion.range); }
			else if(dir==DIR_WEST ) { x = (int)(mapX-champion.range); }
			if((x!=(int)mapX || y!=(int)mapY) && (x==(int)stepX && y==(int)stepY)) {
				Tile t = map.getTile(x,y);
				if(t.wall!=null) {
					setActionBattleWall(t.wall);
					return;
				}
			}

			if(target!=null && (target instanceof Unit)) {
				Unit u = (Unit)target;
				if(distance(u)<=champion.range+u.type.radius) {
Gdx.app.log(APP_NAME,className+":["+map.time+"]move(setActionBattleUnit: distance: "+distance(u)+")");
					setActionBattleUnit(u);
					return;
				}
			}

			if(HP<maxHP && champion.moveHeal>0.0f) {
				if((map.time%FPS)==0) HP += champion.moveHeal;
			}

				  if(dir==DIR_NORTH) setPosition(mapX,mapY-speed);
			else if(dir==DIR_EAST ) setPosition(mapX+speed,mapY);
			else if(dir==DIR_SOUTH) setPosition(mapX,mapY+speed);
			else if(dir==DIR_WEST ) setPosition(mapX-speed,mapY);

			if(mapX-stepX>-speed && mapY-stepY>-speed && mapX-stepX<speed && mapY-stepY<speed) {
				if(path==null || !path.hasMoreSteps()) setActionIdle(champion.idlePause);
				else {
					if(target!=null && (target instanceof Unit)) {
						Unit u = (Unit)target;
						x = (int)u.mapX;
						y = (int)u.mapY;
						if(x!=(int)destX || y!=(int)destY) {
							int i = path.findStep(x,y);
							if(i<0) {
								setActionIdle(champion.idlePause);
								return;
							}
							path.setSteps(null,i+1);
						}
					}
					path.next();
					stepX = ((float)path.getX())+.05f;
					stepY = ((float)path.getY())+.05f;
					if(!path.hasMoreSteps() && (int)stepX==(int)destX && (int)stepY==(int)destY) {
						stepX = destX;
						stepY = destY;
					}
Gdx.app.log(APP_NAME,className+":["+map.time+"]move(x: "+(int)stepX+", y: "+(int)stepY+")");
				}
			}

		} else if(action==ACTION_BATTLE) {
			if(focus!=null) {
				if(((AnimationSet)sprite).index==0 && timer<0)
					timer = ((AnimationSet)sprite).getAnimationActivateFrame();
				if(timer==0) {
					battle(focus);
					if(focus.isDestroyed())
						focus = null;
				}
			}
			if(focus==null) {
				timer = 0;
				if(target!=null && path!=null && path.hasMoreSteps()) {
					setActionMove();
					return;
				} else {
					setActionIdle(champion.idlePause);
					return;
				}
			}
		}
	}

	@Override
	public void draw(IsometricScreen screen,SpriteBatch batch) {
		if(sprite!=null && (state&HIDDEN)==0) {
			int x = (int)Math.round(screenX)+screen.focus.x;
			int y = (int)Math.round(screenY)+screen.focus.y;
			champion.shadow.draw(screen.view,batch,x,y);
			if(sprite.draw(screen.view,batch,x,y)) {
				if(HP<maxHP)
					screen.drawBar(x,y-((AnimationSet)sprite).height,CLIP_BAR_HP,HP/maxHP);
			}
		}
	}

	public void setDestination(float x,float y) {
		if(mapX-x>-speed && mapY-x>-speed && mapX-x<speed && mapY-x<speed) return;
		target = null;
		focus = null;
		path = map.pathFinder.searchPath(this,(int)mapX,(int)mapY,(int)x,(int)y,0);
		destX = x;
		destY = y;
		if(path==null || !path.hasMoreSteps()) {
			stepX = destX;
			stepY = destY;
		} else {
			path.next();
			stepX = ((float)path.getX())+.05f;
			stepY = ((float)path.getY())+.05f;
Gdx.app.log(APP_NAME,className+":["+map.time+"]setDestination(x: "+(int)stepX+", y: "+(int)stepY+")");
		}
		setActionMove();
	}

	public void setTarget(Target t) {
		if(t==null) {
			setActionIdle(champion.idlePause);
		} else if(t instanceof Unit) {
			Unit u = (Unit)t;
			setDestination(u.mapX,u.mapY);
		} else if(t instanceof Wall) {
			Wall w = (Wall)t;
			if(w.tile==null) t = null;
			else setDestination(w.tile.mapX,w.tile.mapY);
		}
		target = t;
		focus = null;
	}

	public Unit findTarget() {
		float d,d1 = 0.0f;
		Unit u,u1 = null;
		for(u=map.units; u!=null; u=u.list)
			if(u!=this && u.wizard!=wizard) {
				if((u.state&TARGET)==0) continue;
				d = distance(u);
				if(wizard==map.defender && d>champion.vision) continue;
				if(u1==null || d<d1) {
					u1 = u;
					d1 = d;
				}
			}
		return u1;
	}

	public int getDirection(float x,float y,int dir) {
		if(x<0.0f) {
			x = -x;
			if(y<0.0f) {
				if(dir==DIR_EAST || dir==DIR_SOUTH) return dir;
				y = -y;
				if(x<y) return DIR_SOUTH;
			} else if(y>0.0f) {
				if(dir==DIR_EAST || dir==DIR_NORTH) return dir;
				if(x<y) return DIR_NORTH;
			}
			return DIR_EAST;
		} else if(x>0.0f) {
			if(y<0.0f) {
				if(dir==DIR_WEST || dir==DIR_SOUTH) return dir;
				y = -y;
				if(x<y) return DIR_SOUTH;
			} else if(y>0.0f) {
				if(dir==DIR_WEST || dir==DIR_NORTH) return dir;
				if(x<y) return DIR_NORTH;
			}
			return DIR_WEST;
		} else {
			if(y>0.0f) return DIR_NORTH;
			return DIR_SOUTH;
		}
	}

	/** Set action to idle.
	 * @param t Timer for remaining idle, if <=0 find new target directly.
	 */
	public void setActionIdle(int t) {
		if(t<=0) {
			Unit u = findTarget();
			if(u!=null) {
				setTarget(u);
				return;
			}
			// Defending units return to start position:
			if(wizard==map.defender && distance(startX,startY)>speed) {
				setDestination(startX,startY);
				return;
			}
		}
		action  = ACTION_IDLE;
		((AnimationSet)sprite).setAnimation(action,dir);
		path    = null;
		target  = null;
		focus   = null;
		timer   = t;
	}

	public void setActionMove() {
		action = ACTION_MOVE;
		((AnimationSet)sprite).setAnimation(action,dir);
	}

	public void setActionBattleWall(Wall w) {
		action = ACTION_BATTLE;
		focus = w;
		((AnimationSet)sprite).setAnimation(action,dir);
		timer = ((AnimationSet)sprite).getAnimationActivateFrame();
	}

	public void setActionBattleUnit(Unit u) {
		action = ACTION_BATTLE;
		target = u;
		focus = u;
		dir = getDirection(mapX-u.mapX,mapY-u.mapY,-1);
		((AnimationSet)sprite).setAnimation(action,dir);
		timer = ((AnimationSet)sprite).getAnimationActivateFrame();
		path = null;
	}

	public void battle(Target t) {
		float hp = dmg;

		if(t instanceof Wall) {
			// Battle algorithm for Walls:
			hp = dmg*champion.wallAttMod;
		} else if(t instanceof Champion) {
			// Battle algorithm for Champions:
			Champion c = (Champion)t;

			// If not in battle or in battle with Wall etc. retaliate:
			if(c.action!=ACTION_BATTLE || !(c.focus instanceof Unit))
				setActionBattleUnit(this);

			float d = (float)map.die20[(map.time+id)%20];
			float a = 10.0f*att/c.def;
			if(a==0.0f) a = 1.0f;
			if(a>20.0f) a = 20.0f;
			if(a<d) return;
			hp = dmg-c.arm*(d/a);
Gdx.app.log(APP_NAME,className+":["+map.time+"]battle(id: "+id+", d: "+d+", a: "+a+", dmg: "+dmg+", hp: "+hp+")");
			if(hp<=0.0f) return;
		} else if(t instanceof Unit) {
			// Battle algorithm for Units:
			Unit u = (Unit)t;
		}

		t.damage(hp);
	}

	@Override
	public int getPathStepCost(PathFinder p,PathNode fr,PathNode to) {
		if(to==null) return PathFinder.CANNOT_MOVE;
		Tile t = (Tile)to;
		if(t.wall!=null) {
			// Defending Champions don't destroy walls: 
			if(wizard==map.defender)
				return PathFinder.CANNOT_MOVE;
			Wall w = t.wall;
			float m = w.type.moveCost*champion.wallMoveMod;
			if(w.HP<w.maxHP) m *= w.HP/w.maxHP;
			return (int)m;
		}
		return champion.floorMoveCost;
	}

	// Not used by this game:
	@Override
	public int getReachStepCost(PathFinder p,PathNode fr,PathNode to) { return 0; }

	@Override
	public int getTargetStepCost(PathFinder p,PathNode fr,PathNode to) {
		if(to==null) return PathFinder.CANNOT_MOVE;
		Tile t = (Tile)to;
//		if(t.wall!=null) return PathFinder.AVOID_MOVE;
		if(t.wall!=null) return 10;
		return champion.floorMoveCost;
	}

	@Override
	public int countTargets(PathFinder p) {
		int n;
		Unit u;
		for(n=0,u=map.units; u!=null; u=u.list)
			if(u!=this && u.wizard!=wizard) ++n;
		return 0;
	}

	@Override
	public PathNode pickTarget(PathFinder p,PathNode t1,PathNode t2) {
		if(t1==null) return t2;
		if(t2==null) return t1;
		return t1;
	}
};





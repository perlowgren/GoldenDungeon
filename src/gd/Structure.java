package com.seshat.gd;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.seshat.Point;
import com.seshat.Rect;
import com.seshat.AnimationSet;
import com.seshat.Clip;
import com.seshat.gd.Unit.UnitType;


/** The Structure class is the base class for all Structure
 * units.
 */
public class Structure extends Unit {

	/** The Champion type is the same as the Unit type,
	 * but for Champions. It contains additional data
	 * specific for Champions.
	 */
	public static class StructureType extends UnitType {
		public float range;
		public float vision;

		public StructureType(int i,int lvl,String nm,float rd,float rn,float vs,float hp) {
			super(i,lvl,nm,rd,hp);
			range            = rn;
			vision           = vs;
		}
	};

	public static final StructureType structureChest = new StructureType(
		STRUCTURE_CHEST,1,
		"Treasure Chest",
		1.0f,    // Radius
		0.0f,    // Range
		0.0f,    // Vision
		100.0f   // HP
	);

	public static final StructureType[] structures = {null,
		structureChest,
	null};

	/** Static data specific for the type of the Champion. */
	public StructureType structure;

	public Structure(Wizard wz,int i,int t) {
		super(wz,i,UNIT_STRUCTURE);
		state     |= TARGET;
		structure  = structures[t];
		type       = structure;
		HP         = type.HP;
	}

	public Structure(Wizard wz,int i,int t,Map m,float x,float y) {
		this(wz,i,t);
		setPosition(m,x,y);
	}
}
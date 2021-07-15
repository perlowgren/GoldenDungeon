package com.seshat.gd;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.seshat.Sprite;


/** The UnitEffect class makes an effect as a Unit, which means
 * it can interact inside the map.
 */
public class VisualEffect extends Unit implements Effect {

	protected EffectListener listener;

	public VisualEffect(Wizard wz,int i) {
		super(wz,i,UNIT_EFFECT);
	}

	public VisualEffect(Wizard wz,int i,Map m,float x,float y) {
		this(wz,i);
		setPosition(m,x,y);
	}

	@Override
	public void setEffectListener(EffectListener l) {
		listener = l;
	}

	@Override
	public EffectListener getEffectListener() {
		return listener;
	}
};





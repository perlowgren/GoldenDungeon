package com.seshat.gd;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.seshat.Sprite;


/** The FadeEffect class takes a sprite image and fades in or out.
 */
public class FadeEffect extends VisualEffect {

	private float alpha_from;
	private float alpha_to;
	private float alpha;
	private float transition;

	/** Create a new FadeEffect.
	 * @param wz Wizard object owning the effect
	 * @param i Id
	 * @param s Sprite object
	 * @param f Number of frames to show effect
	 * @param af Alpha from
	 * @param at Alpha to
	 */
	public FadeEffect(Wizard wz,int i,Sprite s,int f,float af,float at) {
		super(wz,i);
		sprite      = s;
		alpha_from  = af;
		alpha_to    = at;
		alpha       = alpha_from;
		transition  = 1.0f/(float)f;
		if(alpha_from>alpha_to)
			transition = -transition;
	}

	public FadeEffect(Wizard wz,int i,Sprite s,int f,float af,float at,Map m,float x,float y) {
		this(wz,i,s,f,af,at);
		setPosition(m,x,y);
	}

	public float getAlpha() { return alpha; }

	@Override
	public void move() {
		boolean done = false;
		--timer;
		if(alpha_from<alpha_to) {
			alpha -= transition;
			done = alpha>=alpha_to;
		} else {
			alpha += transition;
			done = alpha<=alpha_to;
		}
		if(done) {
			if(listener!=null)
				listener.effectActivated(this);
			delete();
		}
	}

	@Override
	public void draw(IsometricScreen screen,SpriteBatch batch) {
		int x = (int)Math.round(screenX)+screen.focus.x;
		int y = (int)Math.round(screenY)+screen.focus.y;
		batch.setColor(1.0f,1.0f,1.0f,alpha);
		sprite.draw(screen.view,batch,x,y);
		batch.setColor(1.0f,1.0f,1.0f,1.0f);
	}
};





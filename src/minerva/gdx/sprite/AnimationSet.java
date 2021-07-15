package com.spirangle.minerva.gdx.sprite;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.spirangle.minerva.Rectangle;

public class AnimationSet extends Rectangle implements Sprite {
	public Clip[] frames;
	public Clip frame;
	public Animation[] anims;
	public Animation anim;
	public int dir;
	public int index;
	public float speed;
	public int timer;

	public AnimationSet(int x,int y,int w,int h,Clip[] f,int i,Animation[] a) {
		super(x,y,w,h);
		frames = f;
		index = i;
		anims = a;
		speed = 1.0f;
		setAnimation(0,1);
	}

	public AnimationSet(AnimationSet s,int i,float sp) {
		super.set(s);
		frames = s.frames;
		index = s.index;
		if(i>0) index += (i%(frames.length-index));
		anims = s.anims;
		speed = sp;
		setAnimation(0,1);
	}

	public void setAnimation(int a,int d) {
		if(anims==null) {
			if(index<0 || index>=frames.length) index = 0;
			frame = frames[index];
			anim = null;
		} else {
			a = a*4+d;
			if(a>=anims.length) a = 1;
			if(anim!=null && anim==anims[a]) return;
			dir = d;
			anim = anims[a];
			if(frames.length==1 || anim==null || anim.frames.length==1) {
				frame = frames[anim==null? 0 : anim.frames[0]];
				timer = 0;
			} else {
				index = -1;
				nextFrame();
			}
		}
	}

	public int getAnimationActivateFrame() {
		if(anim==null) return 0;
		return (int)Math.round(anim.activate*anim.speed*speed);
	}

	public void nextFrame() {
		if(anim==null) return;
		++index;
		if(index==anim.frames.length) index = 0;
		frame = frames[anim.frames[index]];
		timer = (int)Math.round(anim.timers[index]*anim.speed*speed);
		if(timer<=0) timer = 1;
	}

	@Override
	public Clip getClip() {
		return frame;
	}

	@Override
	public boolean isTouched(int x,int y,int tx,int ty) {
		return frame!=null && frame.isTouched(x,y,tx,ty);
	}

	@Override
	public boolean isVisible(Rectangle viewport,int x,int y) {
		return isVisible(viewport,frame,x,y);
	}

	public boolean isVisible(Rectangle viewport,Clip f,int x,int y) {
		if(f==null) f = frame;
		return f!=null &&
		       x-f.centerX+f.width>0 &&
		       y-f.centerY+f.height>0 &&
		       x-f.centerX<viewport.width &&
		       y-f.centerY<viewport.height;
	}

	@Override
	public boolean draw(SpriteBatch batch,Rectangle viewport,int x,int y) {
		if(!isVisible(viewport,frame,x,y)) return false;
		batch.draw(frame.texture,
		           x-frame.centerX,y-frame.centerY,frame.width,frame.height,
		           frame.x,frame.y,frame.width,frame.height,
		           frame.flip,true);
		if(timer>0) {
			--timer;
			if(timer==0) nextFrame();
		}
		return true;
	}

	public boolean draw(SpriteBatch batch,Rectangle viewport,int x,int y,int i) {
		if(i<0) {
			i = frames.length+i;
			if(i<0) i = 0;//return false;
		}
		if(i>=frames.length) i %= frames.length;
		Clip f = frames[i];
		if(!isVisible(viewport,f,x,y)) return false;
		batch.draw(f.texture,
		           x-f.centerX,y-f.centerY,f.width,f.height,
		           f.x,f.y,f.width,f.height,
		           f.flip,true);
		return true;
	}
}

package com.spirangle.minerva.gdx.sprite;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.spirangle.minerva.Rectangle;

public class Clip extends Rectangle implements Sprite {
	public Texture texture;
	public int centerX;
	public int centerY;
	public Rectangle solid;
	public boolean flip;
	public float time;

	public Clip(Texture t,int x,int y,int w,int h,int cx,int cy,boolean fl) {
		this(t,x,y,w,h,cx,cy,null,fl);
	}

	public Clip(Texture t,int x,int y,int w,int h,int cx,int cy,Rectangle s,boolean fl) {
		super(x,y,w,h);
		texture = t;
		centerX = cx;
		centerY = cy;
		solid = s;
		flip = fl;
		if(flip) centerX = width-centerX;
	}

	@Override
	public Clip getClip() {
		return this;
	}

	@Override
	public boolean isTouched(int x,int y,int tx,int ty) {
		if(solid!=null)
			return tx>=x-centerX+solid.x &&
			       ty>=y-centerY+solid.y &&
			       tx<x-centerX+solid.x+solid.width &&
			       ty<y-centerY+solid.y+solid.height;
		else
			return tx>=x-centerX &&
			       ty>=y-centerY &&
			       tx<x-centerX+width &&
			       ty<y-centerY+height;
	}

	@Override
	public boolean isVisible(Rectangle viewport,int x,int y) {
		return x-centerX+width>0 &&
		       y-centerY+height>0 &&
		       x-centerX<viewport.width &&
		       y-centerY<viewport.height;
	}

	@Override
	public boolean draw(SpriteBatch batch,Rectangle viewport,int x,int y) {
		if(viewport!=null && !isVisible(viewport,x,y)) return false;
		batch.draw(texture,
		           x-centerX,y-centerY,width,height,
		           this.x,this.y,width,height,
		           flip,true);
		return true;
	}
}



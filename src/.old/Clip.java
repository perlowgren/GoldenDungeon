package com.seshat;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Clip extends Rect implements Sprite {
	public Texture texture;
	public int centerX;
	public int centerY;
	public boolean flip;
	public float time;

	public Clip(Texture t,int x,int y,int w,int h,int cx,int cy,boolean fl) {
		super(x,y,w,h);
		texture = t;
		centerX = cx;
		centerY = cy;
		flip = fl;
		if(flip) centerX = width-centerX;
	}

	@Override
	public Clip getClip() {
		return this;
	}

	@Override
	public boolean isTouched(int x,int y,int tx,int ty) {
		return tx>=x-centerX &&
		       ty>=y-centerY &&
		       tx<x-centerX+width &&
		       ty<y-centerY+height;
	}

	@Override
	public boolean isVisible(Size view,int x,int y) {
		return x-centerX+width>0 &&
		       y-centerY+height>0 &&
		       x-centerX<view.width &&
		       y-centerY<view.height;
	}

	@Override
	public boolean draw(Size view,SpriteBatch batch,int x,int y) {
		if(!isVisible(view,x,y)) return false;
		batch.draw(texture,
		           x-centerX,y-centerY,width,height,
		           this.x,this.y,width,height,
		           flip,true);
		return true;
	}
}



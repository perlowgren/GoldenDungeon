package com.seshat;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Animation implements Sprite {
	public float speed;
	public int[] frames;
	public float[] timers;
	public float activate;

	public Animation(float sp,int[] f,float[] t,float a) {
		speed     = sp;
		frames    = f;
		timers    = t;
		activate  = a;
	}

	@Override
	public Clip getClip() {
		return null;
	}

	@Override
	public boolean isTouched(int x,int y,int tx,int ty) { return false; }

	@Override
	public boolean isVisible(Size view,int x,int y) { return false; }

	@Override
	public boolean draw(Size view,SpriteBatch batch,int x,int y) { return false; }
}



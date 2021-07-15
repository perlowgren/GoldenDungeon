package com.spirangle.minerva.gdx.sprite;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.spirangle.minerva.Rectangle;

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
	public boolean isVisible(Rectangle viewport,int x,int y) { return false; }

	@Override
	public boolean draw(SpriteBatch batch,Rectangle viewport,int x,int y) { return false; }
}



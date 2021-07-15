package com.seshat;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** The Sprite can draw an image or animation.
 */
public interface Sprite {
	public Clip getClip();

	public boolean isTouched(int x,int y,int tx,int ty);

	public boolean isVisible(Size view,int x,int y);

	public boolean draw(Size view,SpriteBatch batch,int x,int y);
};


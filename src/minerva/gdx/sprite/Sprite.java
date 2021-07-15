package com.spirangle.minerva.gdx.sprite;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.spirangle.minerva.Rectangle;

/** The Sprite can draw an image or animation.
 */
public interface Sprite {
	public Clip getClip();

	public boolean isTouched(int x,int y,int tx,int ty);

	public boolean isVisible(Rectangle viewport,int x,int y);

	public boolean draw(SpriteBatch batch,Rectangle viewport,int x,int y);
};


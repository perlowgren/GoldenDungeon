package com.seshat.gd;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
//import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Align;

import com.seshat.Point;
import com.seshat.Rect;
import com.seshat.Size;
import com.seshat.AnimationSet;
import com.seshat.Clip;

/**
 * The DungeonScreen class handles interaction in a dungeon, it simply
 * displays the Map and provides UI.
 */
public class DungeonScreen extends IsometricScreen {
	private static final String className = "DungeonScreen";

	public DungeonScreen(Main m,Map mp) {
		super(m,mp);
	}

	@Override
	public boolean handleTouchDown(float xs,float ys,float xm,float ym) {
		map.setFocusPoint(xs,ys,xm,ym);
		map.activateFocusPoint();
		return true;
	}

	@Override
	public boolean handleTouchUp(float xs,float ys,float xm,float ym) {
		return false;
	}

	@Override
	public boolean handleTouchDragged(float xs,float ys,float xm,float ym) {
		return false;
	}
}


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
import com.seshat.gui.Button;
import com.seshat.gui.Widget;
import com.seshat.gui.WidgetListener;

/**
 * The DungeonEditorScreen class handles editing of a Map.
 */
public class DungeonEditorScreen extends DungeonScreen implements WidgetListener {
	private static final String className = "DungeonEditorScreen";

	public DungeonEditorScreen(Main m,Map mp) {
		super(m,mp);
		Widget w,w1,w3,w2;
		Button b;
		w = new Widget(1,Widget.VIRTUAL|Widget.ARRANGE_V,0,0,display.width,display.height);
		w1 = new Widget(2,Widget.VIRTUAL|Widget.ARRANGE_H/*|Widget.FILL*/|Widget.ALIGN_LEFT,0,0,0,0);
		w.append(w1);
		w2 = new Widget(3,Widget.VIRTUAL|Widget.EXPAND|Widget.FILL,0,0,0,0);
		w.append(w2);
		w3 = new Widget(4,Widget.VIRTUAL|Widget.ARRANGE_H/*|Widget.FILL*/|Widget.ALIGN_RIGHT,0,0,0,0);
		w.append(w3);

		b = new Button(5,Widget.EXPAND|Widget.FILL,0,0,20,20);
		b.setWidgetListener(this);
		w1.append(b);
		b = new Button(6,0,0,0,20,20);
		b.setWidgetListener(this);
		w1.append(b);
		b = new Button(7,Widget.EXPAND,0,0,20,20);
		b.setWidgetListener(this);
		w1.append(b);

		b = new Button(8,0,0,0,20,20);
		b.setWidgetListener(this);
		w3.append(b);
		b = new Button(9,Widget.EXPAND|Widget.FILL,0,0,20,20);
		b.setWidgetListener(this);
		w3.append(b);
		b = new Button(10,0,0,0,20,20);
		b.setWidgetListener(this);
		w3.append(b);

		appendWidget(w);
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
		if(map.focus!=null) {
			map.moveFocusPoint(xs,ys,xm,ym);
			return true;
		}
		return false;
	}

	@Override
	public boolean widgetActivated(Widget w) {
Gdx.app.log(className,"widgetActivated("+w.getId()+")");
		return true;
	}
}


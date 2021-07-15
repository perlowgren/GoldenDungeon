package com.seshat.gd;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
//import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.OrthographicCamera;

import com.seshat.Point;
import com.seshat.Size;
import com.seshat.gui.Widget;
import com.seshat.gui.WidgetListener;

/**
 * The LoadingScreen class handles loading assets.
 */
public abstract class BasicScreen implements Data,Screen,InputProcessor {
	private static final String className = "BasicScreen";

	private static final int[] box_styles = {  1,1, 84,1 };

	protected Main main;
	protected AssetManager assets;

	protected SpriteBatch batch;
	protected ShapeRenderer renderer;

	/** PORTRAIT or LANDSCAPE. */
	protected int orientation;

	/** Size of display in pixels. */
	protected Size display;

	protected float density;

	private Widget widgets = null;

	private Widget focusWidget = null;
	private Widget activeWidget = null;

	public BasicScreen(Main m) {
		Gdx.app.log(APP_NAME,className+":init");

		main = m;
		assets = main.getAssetManager();

		batch = new SpriteBatch();
		renderer = new ShapeRenderer();

		int w,h;
		w = (int)Gdx.graphics.getWidth();
		h = (int)Gdx.graphics.getHeight();

		orientation = w<h? PORTRAIT : LANDSCAPE;

		display = new Size(w,h);
		density = Gdx.graphics.getDensity();

		Gdx.app.log(APP_NAME,className+":width: "+display.width+", height: "+display.height+", density: "+density);

		Gdx.input.setInputProcessor(this);
		Gdx.app.log(APP_NAME,className+":attached");
	}

	@Override
	public void dispose() {
		Gdx.app.log(APP_NAME,className+":dispose()");
		batch.dispose();
		renderer.dispose();
	}

	public void setDisplaySize(int w,int h) {
		display.set(w,h);
	}

	public void appendWidget(Widget w) {
		if(widgets==null) {
			widgets = new Widget(0,Widget.VIRTUAL,0,0,display.width,display.height);
			widgets.setScreen(this);
		}
		widgets.append(w);
		widgets.updateAll();
	}

	public void drawWidgets() {
		if(widgets!=null)
			widgets.drawAll();
	}

	public boolean touchWidget(int x,int y) {
		if(widgets!=null) {
			if(focusWidget!=null) focusWidget.setFocus(false);
			focusWidget = null;
			if(activeWidget!=null) activeWidget.setActive(false);
			activeWidget = widgets.get(x,y);
			if(activeWidget==widgets) activeWidget = null;
			else if(activeWidget!=null) {
				activeWidget.setActive(true);
				focusWidget = activeWidget;
				focusWidget.setFocus(true);
				return activeWidget.touch(true,x,y);
			}
		}
		return false;
	}

	public boolean releaseWidget(int x,int y) {
		if(activeWidget!=null) {
			if(focusWidget!=null) focusWidget.setFocus(false);
			focusWidget = widgets.get(x,y);
			if(focusWidget==widgets) focusWidget = null;
			else if(focusWidget!=null) {
				focusWidget.setFocus(true);
				if(focusWidget==activeWidget)
					activeWidget.touch(false,x,y);
			}
			if(activeWidget!=null) activeWidget.setActive(false);
			activeWidget = null;
			return true;
		}
		return false;
	}

	public boolean dragWidget(int x,int y) {
		if(activeWidget!=null) {
			activeWidget.drag(x,y);
			return true;
		}
		return false;
	}

	public void drawBox(int x,int y,int w,int h,int style) {
		int sx = box_styles[style*2];
		int sy = box_styles[style*2+1];
		int i,j,m,n;
		Texture texture = main.getTexture(TEXTURE_UI);
		batch.draw(texture,x,     y,     8, 8, sx,    sy,    8, 8,false,true);
		batch.draw(texture,x+w-8, y,     8, 8, sx+74, sy,    8, 8,false,true);
		batch.draw(texture,x,     y+h-8, 8, 9, sx,    sy+74, 8, 9,false,true);
		batch.draw(texture,x+w-8, y+h-8, 8, 9, sx+74, sy+74, 8, 9,false,true);

		for(i=x+8,m=64; i<x+w-8 && m==64; i+=64) {
			if(i+64>=x+w-8) m = (x+w-8)-i;
			batch.draw(texture,i, y,     m, 8, sx+9, sy,    m, 8,false,true);
			batch.draw(texture,i, y+h-8, m, 9, sx+9, sy+74, m, 9,false,true);
		}

		for(j=y+8,n=64; j<y+h-8 && n==64; j+=64) {
			if(j+64>=y+h-8) n = (y+h-8)-j;
			batch.draw(texture,x,     j, 8, n, sx,    sy+9, 8, n,false,true);
			batch.draw(texture,x+w-8, j, 8, n, sx+74, sy+9, 8, n,false,true);
			for(i=x+8,m=64; i<x+w-8 && m==64; i+=64) {
				if(i+64>=x+w-8) m = (x+w-8)-i;
				batch.draw(texture,i,j,m,n,sx+9,sy+9,m,n,false,true);
			}
		}
	}

	public void drawCenteredBox(int w,int h,int style,Point p) {
		int x = (display.width-w)/2;
		int y = (display.height-h)/2;
		drawBox(x,y,w,h,style);
		if(p!=null) p.set(x,y);
	}

	@Override
	public boolean touchDown(int x,int y,int pointer,int button) {
		return false;
	}

	@Override
	public boolean touchUp(int x,int y,int pointer,int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int x,int y,int pointer) {
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean mouseMoved(int x,int y) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}


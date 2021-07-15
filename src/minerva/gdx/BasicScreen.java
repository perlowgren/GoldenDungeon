package com.spirangle.minerva.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.OrthographicCamera;

import com.spirangle.minerva.gdx.gui.Widget;
import com.spirangle.minerva.gdx.gui.WidgetListener;
import com.spirangle.minerva.Dimension;
import com.spirangle.minerva.Point;
import com.spirangle.minerva.Rectangle;

/**
 * The BasicScreen class handles widgets and basic drawing.
 */
public abstract class BasicScreen implements Screen,InputProcessor {
	public static final int PORTRAIT      = 0;
	public static final int LANDSCAPE     = 1;

	private static final int[] boxStyles  = {  1,1, 84,1 };

	protected BasicGame game              = null;

	/** PORTRAIT or LANDSCAPE. */
	protected int orientation             = PORTRAIT;
	/** Size of display in pixels. */
	protected Dimension display           = new Dimension(0,0);
	/** Density of display; i.e. 1.0 ~= 160 DPI. */
	protected float density               = 1.0f;
	/** Zoom of view. */
	protected float zoom                  = 1.0f;
	/** Display scaled by zoom value. */
	protected Rectangle viewport          = new Rectangle(0,0,0,0);

	/** Camera for game view */
	protected OrthographicCamera camera;
	/** Camera for UI */
	protected OrthographicCamera uiCamera;

	protected SpriteBatch batch           = null;
	protected ShapeRenderer renderer      = null;

	/** Frame counter. */
	protected int frame                   = 0;

	private Widget widgets                = null;

	private Widget focusWidget            = null;
	private Widget activeWidget           = null;

	private Texture gui                   = null;

	public BasicScreen(BasicGame g) {
BasicGame.log("BasicScreen()");

		game = g;

		density = Gdx.graphics.getDensity();

		camera = new OrthographicCamera();
		uiCamera = new OrthographicCamera();

		batch = new SpriteBatch();
		renderer = new ShapeRenderer();

BasicGame.log("BasicScreen(density: "+density+")");

		gui = game.getGUITexture();

		Gdx.input.setInputProcessor(this);
BasicGame.log("BasicScreen(done)");
	}

	@Override
	public void render(float delta) {
		++frame;
	}

	@Override
	public void dispose() {
BasicGame.log("BasicScreen.dispose()");
		batch.dispose();
		renderer.dispose();
	}

	@Override
	public void resize(int w,int h) {
		setDisplaySize(w,h);
BasicGame.log("BasicScreen.resize(w: "+w+", h: "+h+" ["+viewport.width+", "+viewport.height+"])");
		camera.setToOrtho(true,viewport.width,viewport.height);
		camera.update();
		uiCamera.setToOrtho(true,display.width,display.height);
		uiCamera.update();
	}

	@Override
	public void show() {
BasicGame.log("BasicScreen.show()");
	}

	@Override
	public void hide() {
BasicGame.log("BasicScreen.hide()");
	}

	@Override
	public void pause() {
BasicGame.log("BasicScreen.pause()");
	}

	@Override
	public void resume() {
BasicGame.log("BasicScreen.resume()");
	}

	public void setDisplaySize(int w,int h) {
		orientation = w<h? PORTRAIT : LANDSCAPE;
		display.set(w,h);
		viewport.setSize(display).divide(zoom);
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
		int sx = boxStyles[style*2];
		int sy = boxStyles[style*2+1];
		int i,j,m,n;
		batch.draw(gui,x,     y,     8, 8, sx,    sy,    8, 8,false,true);
		batch.draw(gui,x+w-8, y,     8, 8, sx+74, sy,    8, 8,false,true);
		batch.draw(gui,x,     y+h-8, 8, 9, sx,    sy+74, 8, 9,false,true);
		batch.draw(gui,x+w-8, y+h-8, 8, 9, sx+74, sy+74, 8, 9,false,true);

		for(i=x+8,m=64; i<x+w-8 && m==64; i+=64) {
			if(i+64>=x+w-8) m = (x+w-8)-i;
			batch.draw(gui,i, y,     m, 8, sx+9, sy,    m, 8,false,true);
			batch.draw(gui,i, y+h-8, m, 9, sx+9, sy+74, m, 9,false,true);
		}

		for(j=y+8,n=64; j<y+h-8 && n==64; j+=64) {
			if(j+64>=y+h-8) n = (y+h-8)-j;
			batch.draw(gui,x,     j, 8, n, sx,    sy+9, 8, n,false,true);
			batch.draw(gui,x+w-8, j, 8, n, sx+74, sy+9, 8, n,false,true);
			for(i=x+8,m=64; i<x+w-8 && m==64; i+=64) {
				if(i+64>=x+w-8) m = (x+w-8)-i;
				batch.draw(gui,i,j,m,n,sx+9,sy+9,m,n,false,true);
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

	public int getOrientation() { return orientation; }
	public int getDisplayWidth() { return display.width; }
	public int getDisplayHeight() { return display.height; }
	public int getViewportWidth() { return viewport.width; }
	public int getViewportHeight() { return viewport.height; }
	public float getZoom() { return zoom; }
}


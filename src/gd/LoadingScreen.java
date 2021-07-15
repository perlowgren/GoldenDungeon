package com.seshat.gd;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
import com.seshat.Rect;
import com.seshat.Size;

/**
 * The LoadingScreen class handles loading assets.
 */
public class LoadingScreen extends BasicScreen {
	private static final String className = "LoadingScreen";

	/** Camera for UI */
	private OrthographicCamera camera;

	public LoadingScreen(Main m) {
		super(m);
		camera = new OrthographicCamera();
		Gdx.app.log(APP_NAME,className+":Attached");
	}

	@Override
	public void render(float delta) {
		if(assets.update()) {
			main.loadAssetsDone();
			return;
		}

		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

/*		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.enableBlending();

		batch.draw(textures[TEXTURE_MARKERS],x-8,y-30,15,29,0,32,15,29,false,true);
		batch.end();*/

		float progress = assets.getProgress();

		renderer.setProjectionMatrix(camera.combined);
		renderer.begin(ShapeType.Line);
		renderer.setColor(1.0f,1.0f,1.0f,1.0f);
		renderer.rect(0.5f,0.5f,display.width-1.0f,display.height-1.0f);
		renderer.rect(2.5f,display.height-9.5f,(display.width-5.0f),7.0f);
		renderer.end();
		renderer.begin(ShapeType.Filled);
		renderer.setColor(0.0f,1.0f,0.0f,1.0f);
		renderer.rect(3.5f,display.height-9.0f,(display.width-6.5f)*progress,5.5f);
		renderer.end();

/*		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.enableBlending();
//		batch.draw(UncivMain.cityTexture,0,0,58,51,0,0,58,51,false,true);
		fonts[FONT_RISQUE18].draw(batch,"The Golden Dungeon",10.0f,10.0f);
		fonts[FONT_PROFONT12W].draw(batch,
		       "Frame: "+frame+
		    "   Focus: "+focus.x+","+focus.y+" (x: "+((float)((int)(map.focusX*100))/100)+", y: "+((float)((int)(map.focusY*100))/100)+")"+
		    "   View: "+view.width+","+view.height+
		    "   Touch: "+touch[0].x+","+touch[0].y+
		    "   Zoom: "+zoom+(zooming? " : zooming" : "")+" - "+zoomFocus.x+","+zoomFocus.y,10.0f,(float)display.height-22.0f);
		batch.end();*/

		Main.sleepFPS(FRAME_RATE,delta);
	}

	@Override
	public void resize(int w,int h) {
		Gdx.app.log(APP_NAME,className+":resize(w: "+w+", h: "+h+")");
		setDisplaySize(w,h);
		camera.setToOrtho(true,display.width,display.height);
		camera.update();
	}

	@Override
	public void show() {
		Gdx.app.log(APP_NAME,className+":show()");
	}

	@Override
	public void hide() {
		Gdx.app.log(APP_NAME,className+":hide()");
	}

	@Override
	public void pause() {
		Gdx.app.log(APP_NAME,className+":pause()");
	}

	@Override
	public void resume() {
		Gdx.app.log(APP_NAME,className+":resume()");
	}
}


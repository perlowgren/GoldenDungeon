package com.spirangle.minerva.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * The LoadingScreen class handles loading assets.
 */
public class LoadingScreen extends BasicScreen {
	private AssetManager assets;
	private float progress         = -1.0f;
	private String backgroundFile;
	private Texture background     = null;
	private String barFile;
	private Texture bar            = null;
	private int[] data             = null;

	public LoadingScreen(BasicGame g,float z,String bg,String b,int[] d) {
		super(g);
		zoom            = z;
		assets          = game.getAssetManager();
		backgroundFile  = bg;
		barFile         = b;
		data            = d;
BasicGame.log("LoadingScreen()");
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		if(assets.update(100)) {
			game.loadingAssetsCompleted();
			return;
		}

		float p = assets.getProgress();
		if(p==progress) return;
		progress = p;

		if(background==null &&
		   backgroundFile!=null &&
		   assets.getLoadedAssets()>0 && 
		   assets.isLoaded(backgroundFile,Texture.class))
				background = assets.get(backgroundFile,Texture.class);

		if(bar==null &&
		   barFile!=null &&
		   assets.getLoadedAssets()>1 && 
		   assets.isLoaded(barFile,Texture.class))
				bar = assets.get(barFile,Texture.class);


		if(background!=null) {
			int w = background.getWidth();
			int h = background.getHeight();
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			batch.enableBlending();
			float vx = w>=viewport.width? 0.0f : (float)((viewport.width-w)/2);
			float vy = h>=viewport.height? 0.0f : (float)((viewport.height-h)/2);
			float vw = w>=viewport.width? (float)viewport.width : (float)w;
			float vh = h>=viewport.height? (float)viewport.height : (float)h;
			int tx = w<=viewport.width? 0 : (w-viewport.width)/2;
			int ty = h<=viewport.height? 0 : (h-viewport.height)/2;
			int tw = w<=viewport.width? w : viewport.width;
			int th = h<=viewport.height? h : viewport.height;
BasicGame.log("LoadingScreen.render(vx: "+vx+", vy: "+vy+", vw: "+vw+", vh: "+vh+", tx: "+tx+", ty: "+ty+", tw: "+tw+", th: "+th+")");
			if(w<viewport.width || h<viewport.height) {
				Gdx.gl.glClearColor(0.0f,0.0f,0.0f,1.0f);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			}
			batch.draw(background,vx,vy,vw,vh,tx,ty,tw,th,false,true);
			if(bar!=null && data!=null && data.length>=27) {
				w = viewport.width-data[0];
				h = data[2];
				tx = data[0];
				ty = viewport.height-data[1]-h;
				batch.draw(bar,(float)tx,(float)ty,(float)data[5],(float)data[6],data[3],data[4],data[5],data[6],false,true);
				for(tx+=data[5]; tx<w-data[13]; tx+=tw) {
					tw = tx+data[9]<=w-data[13]? data[9] : (w-data[13])-tx;
					batch.draw(bar,(float)tx,(float)ty,(float)tw,(float)data[10],data[7],data[8],tw,data[10],false,true);
				}
				batch.draw(bar,(float)tx,(float)ty,(float)data[13],(float)data[14],data[11],data[12],data[13],data[14],false,true);

				w = data[0]+(int)Math.round((float)(viewport.width-data[0]*2)*progress);
				h = data[2];
				tx = data[0];
				ty = viewport.height-data[1]-h;
				batch.draw(bar,(float)tx,(float)ty,(float)data[17],(float)data[18],data[15],data[16],data[17],data[18],false,true);
				for(tx+=data[17]; tx<w-data[25]; tx+=tw) {
					tw = tx+data[21]<=w-data[25]? data[21] : (w-data[25])-tx;
					batch.draw(bar,(float)tx,(float)ty,(float)tw,(float)data[22],data[19],data[20],tw,data[22],false,true);
				}
				batch.draw(bar,(float)tx,(float)ty,(float)data[25],(float)data[26],data[23],data[24],data[25],data[26],false,true);
			}
			batch.end();
		} else {
			Gdx.gl.glClearColor(0.0f,0.0f,0.0f,1.0f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

			renderer.setProjectionMatrix(uiCamera.combined);
			renderer.begin(ShapeType.Line);
			renderer.setColor(1.0f,1.0f,1.0f,1.0f);
			renderer.rect(0.5f,0.5f,display.width-1.0f,display.height-1.0f);
			renderer.rect(2.5f,display.height-9.5f,(display.width-5.0f),7.0f);
			renderer.end();
			renderer.begin(ShapeType.Filled);
			renderer.setColor(0.0f,1.0f,0.0f,1.0f);
			renderer.rect(3.5f,display.height-9.0f,(display.width-6.5f)*progress,5.5f);
			renderer.end();
		}
//BasicGame.sleepFPS(1.0f,delta);
	}

	@Override
	public void resize(int w,int h) {
		super.resize(w,h);
BasicGame.log("LoadingScreen.resize(w: "+w+", h: "+h+")");
	}
}


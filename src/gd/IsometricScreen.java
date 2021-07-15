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
 * The IsometricScreen class handles displaying of and user interaction
 * in the Map.
 */
public abstract class IsometricScreen extends BasicScreen {
	private static final String className = "IsometricScreen";

	/** Camera for map */
	private OrthographicCamera mapCamera;
	/** Camera for UI */
	private OrthographicCamera uiCamera;

	/** The Map. */
	protected Map map;
	/** View size on map, in pixels * zoom. */
	protected Size view = new Size(0,0);
	/** Focus point of view on map, upper left, in pixels * zoom. */
	protected Point focus = new Point(0,0);
	/** Touch points. */
	protected Point[] touch;

	/** Is true if user is dragging. */
	private boolean dragging = false;
	/** The X-coordinate of dragging starting point, focus.x * zoom. */
	private int dragX = 0;
	/** The Y-coordinate of dragging starting point, focus.y * zoom. */
	private int dragY = 0;
	/** The X-coordinate of dragging starting point on screen, in pixels. */
	private int dragScreenX = 0;
	/** The Y-coordinate of dragging starting point on screen, in pixels. */
	private int dragScreenY = 0;

	/** Is true if view is panning. */
	private boolean panning = false;
	/** The X-coordinate of panning starting point, to calculate panning velocity. */
	private int panX = 0;
	/** The Y-coordinate of panning starting point, to calculate panning velocity. */
	private int panY = 0;
	/** Panning velocity. */
	private double panVel = 0.0;
	/** Panning direction, in radians. */
	private double panDir = 0.0;

	/** Is true if view is zooming. */
	private boolean zooming = false;
	/** The point in pixels relative to map focus for zooming. */
	private Point zoomPoint = new Point(0,0);
	/** The point in pixels relative to map focus for zooming, adjusted to map size. */
	private Point zoomFocus = new Point(0,0);
	/** Value of zoom when starting zooming. */
	private double zoomBase = 1.0;
	/** Distance between touch points when staring zooming, to calculate zoom value when moving points. */
	private double zoomRate = 1.0;

	public IsometricScreen(Main m,Map mp) {
		super(m);
		map = mp;
		mapCamera = new OrthographicCamera();
		uiCamera = new OrthographicCamera();

		touch = new Point[10];
		for(int i=0; i<10; ++i) touch[i] = new Point(0,0);

/*
		setView(
			(int)Math.round((double)map.getWidthPoints()*zoom)-w/2,
			(int)Math.round((double)map.getHeightPoints()*zoom)-h/2
		);
*/

		zoom = Math.floor(density)+1.0;

		int w,h,mw,mh;
		w = (int)Gdx.graphics.getWidth();
		h = (int)Gdx.graphics.getHeight();
		mw = map.getWidth();
		mh = map.getHeight();

		setDisplaySize(w,h);

		setView(
			(w-(int)Math.round((double)((mw+mh)*TILE_WIDTH_HALF-1)*zoom))/2,
			(h-(int)Math.round((double)((mw+mh)*TILE_HEIGHT_HALF-1)*zoom))/2
		);

		Gdx.app.log(APP_NAME,className+":Attached");
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		int x,y;
		int w = map.getWidth();
		int h = map.getHeight();

		map.moveUnits();

		if(dragging) {
			x = focus.x-panX;
			y = focus.y-panY;
			panVel = Math.sqrt(x*x+y*y);
			panDir = Math.atan2(y,x);
			panX = focus.x;
			panY = focus.y;
			if(panVel>MAX_PAN_VEL) panVel = MAX_PAN_VEL;
		} else if(panning) {
			panVel *= 0.95;
			moveView((int)Math.round(Math.cos(panDir)*panVel*zoom),(int)Math.round(Math.sin(panDir)*panVel*zoom));
			if(panVel<MIN_PAN_VEL) panning = false;
		}

		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		int x1 = 0;//((-focus.x)/TILE_WIDTH_HALF+(-focus.y)/TILE_HEIGHT_HALF)/2-h;
		int y1 = 0;//((-focus.y)/TILE_HEIGHT_HALF-((view.width-focus.x)/TILE_WIDTH_HALF))/2;
		int x2 = w-1;//((view.width-focus.x)/TILE_WIDTH_HALF+(view.height-focus.y)/TILE_HEIGHT_HALF)/2-h;
		int y2 = h-1;//((view.height-focus.y)/TILE_HEIGHT_HALF-((-focus.x)/TILE_WIDTH_HALF))/2;
		/*if(x1<0) x1 = 0;
		if(y1<0) y1 = 0;
		if(x2>=w) x2 = w-1;
		if(y2>=h) y2 = h-1;*/

		batch.setProjectionMatrix(mapCamera.combined);
		batch.begin();
		batch.enableBlending();
		drawFloorLayer(x1,y1,x2,y2);
		drawFloorMarker(map.focusTile);
		drawWallLayer(x1,y1,x2,y2);
		drawUnitMarker(map.focus);
		batch.end();

/*
		renderer.setProjectionMatrix(uiCamera.combined);
		renderer.begin(ShapeType.Line);
		renderer.setColor(1,1,1,1);
		renderer.rect(1,0,display.width-1,display.height-1);
		renderer.end();
*/

		batch.setProjectionMatrix(uiCamera.combined);
		batch.begin();
		batch.enableBlending();

		drawWidgets();

//		main.getFont(FONT_RISQUE18).draw(batch,"The Golden Dungeon",10.0f,10.0f,(float)(display.width-10.0f),Align.left,false);

		if(map.isGameEnded()) {
			Point p = new Point();
			String str = map.isWinnerAttacker()? "Attacker Win!" : "Attacker Lose!";
			drawCenteredBox(180,120,1,p);
			main.getFont(FONT_RISQUE18).draw(batch,str,(float)p.x,(float)p.y+10.0f,180.0f,Align.center,false);
		}
		main.getFont(FONT_PROFONT12W).draw(batch,
		       "Frame: "+frame+
		    "   Density: "+density+
		    "   Focus: "+focus.x+","+focus.y+" (x: "+((float)((int)(map.focusX*100))/100)+", y: "+((float)((int)(map.focusY*100))/100)+")"+
		    "   View: "+view.width+","+view.height+
		    "   Touch: "+touch[0].x+","+touch[0].y+
		    "   Zoom: "+zoom+(zooming? " : zooming" : "")+" - "+zoomFocus.x+","+zoomFocus.y,10.0f,(float)display.height-22.0f);
		batch.end();

		Main.sleepFPS(FRAME_RATE,delta);
	}

	public void drawFloorLayer(int x1,int y1,int x2,int y2) {
		int x,y,x3,y3;
		Tile t;
		for(x3=x1,y3=y1; true; ) {
			for(x=x3,y=y3; x<=x2 && y>=y1; ++x,--y) {
				t = map.getTile(x,y);
				if(t!=null && t.wall==null)
					main.getSprite(SPRITE_FLOORS+t.floor).draw(view,batch,t.screenX+focus.x,t.screenY+focus.y);
			}
			if(x3==x2 && y3==y2) break;
			if(y3<y2) ++y3;
			else ++x3;
		}
	}

	public void drawFloorMarker(Tile t) {
		if(t!=null)
			main.getSprite(SPRITE_FLOOR_MARKER).draw(view,batch,t.screenX+focus.x,t.screenY+focus.y);
	}

	public void drawWallLayer(int x1,int y1,int x2,int y2) {
		int x,y,x3,y3;
		Tile t;
		for(x3=x1,y3=y1; true; ) {
			for(x=x3,y=y3; x<=x2 && y>=y1; ++x,--y) {
				t = map.getTile(x,y);
				if(t!=null) t.drawWall(this,batch);
			}
			for(x=x3,y=y3; x<=x2 && y>=y1; ++x,--y) {
				t = map.getTile(x,y);
				if(t!=null) t.drawUnits(this,batch);
			}
			if(x3==x2 && y3==y2) break;
			if(y3<y2) ++y3;
			else ++x3;
		}
	}

	public void drawUnitMarker(Unit u) {
		if(u!=null) {
			AnimationSet as = (AnimationSet)u.sprite;
			int x = (int)u.screenX+focus.x;
			int y = (int)u.screenY+focus.y-as.height;
			main.getSprite(SPRITE_UNIT_MARKER).draw(view,batch,x,y);
		}
	}

	@Override
	public void resize(int w,int h) {
		Gdx.app.log(APP_NAME,className+":resize(w: "+w+", h: "+h+")");
		setDisplaySize(w,h);
		mapCamera.setToOrtho(true,view.width,view.height);
		mapCamera.update();
		uiCamera.setToOrtho(true,display.width,display.height);
		uiCamera.update();
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

	@Override
	public void dispose() {
		Gdx.app.log(APP_NAME,className+":dispose()");
		if(map!=null) {
//			FileHandle file = Gdx.files.local("map.json");
//			file.writeString(map.toJson(),false);
			map.close();
			map.clear();
		}
		batch.dispose();
		renderer.dispose();
//		profont12w.dispose();
//		risque18.dispose();
	}

	@Override
	public boolean touchDown(int x,int y,int pointer,int button) {
		Gdx.app.log(APP_NAME,className+":touchDown(x: "+x+", y: "+y+", pointer: "+pointer+", button: "+button+")");
		touch[pointer].x = x;
		touch[pointer].y = y;
		if(pointer==1 || button==Buttons.RIGHT) {
			if(button==Buttons.RIGHT) {
				dragScreenX = 0;
				dragScreenY = 0;
			}
			zooming = true;
			zoomPoint.set(focus).multiply(zoom).move(-touch[0].x,-touch[0].y);
			adjustViewPoint(zoomPoint,zoomFocus);
//			map.setFocusPoint((float)zoomFocus.x,(float)zoomFocus.y,0.0f,0.0f);
			x = dragScreenX-x;
			y = dragScreenY-y;
			zoomBase = zoom;
			zoomRate = Math.sqrt(x*x+y*y);
		} else if(pointer==0) {
			dragging = false;
			panning = false;
			panVel = 0.0;
			zooming = false;

			if(!touchWidget(x,y)) {
				float xs = (float)((double)x/zoom)-(float)focus.x;
				float ys = (float)((double)y/zoom)-(float)focus.y;
//				float w = (float)map.width;
//				float h = (float)map.height;
				float xh = xs-(float)map.getHeight()*TWH;
				float xm = (xh/TWH+ys/THH)/2.0f;
				float ym = (ys/THH-xh/TWH)/2.0f;
//				if(xm>=w) xm -= w;
//				else if(xm<0) xm += w;
//				if(ym>=h) ym -= h;
//				else if(ym<0) ym += h;
				handleTouchDown(xs,ys,xm,ym);
			}
		}
		return true; // Return true to say we handled the touch.
	}

	@Override
	public boolean touchUp(int x,int y,int pointer,int button) {
Gdx.app.log(APP_NAME,className+":touchUp(x: "+x+", y: "+y+", pointer: "+pointer+", button: "+button+
", dragging: "+(dragging? "yes" : "no")+", panning: "+(panning? "yes" : "no")+", zooming: "+(zooming? "yes" : "no")+")");
		touch[pointer].x = x;
		touch[pointer].y = y;
		if(pointer==0 || button==Buttons.LEFT) {
			if(!releaseWidget(x,y)) {
				if(/*!dragging && !panning && */!zooming) {
					float xs = (float)((double)x/zoom)-(float)focus.x;
					float ys = (float)((double)y/zoom)-(float)focus.y;
//					float w = (float)map.width;
//					float h = (float)map.height;
					float xh = xs-(float)map.getHeight()*TWH;
					float xm = (xh/TWH+ys/THH)/2.0f;
					float ym = (ys/THH-xh/TWH)/2.0f;
//					if(xm>=w) xm -= w;
//					else if(xm<0) xm += w;
//					if(ym>=h) ym -= h;
//					else if(ym<0) ym += h;
					if(!handleTouchUp(xs,ys,xm,ym))
						if(panVel>=MIN_PAN_VEL) panning = true;
				}
			}
			dragging = false;
			zooming = false;
		}
		return true;
	}

	@Override
	public boolean touchDragged(int x,int y,int pointer) {
//		Gdx.app.log(APP_NAME,className+":touchDragged(x: "+x+", y: "+y+", pointer: "+pointer+")");


		if(!zooming && pointer==0) {
			if(!dragging && (touch[0].x!=x || touch[0].y!=y)) {
				dragging = true;
				dragX = (int)Math.round((double)focus.x*zoom);
				dragY = (int)Math.round((double)focus.y*zoom);
				dragScreenX = x;
				dragScreenY = y;
				panX = focus.x;
				panY = focus.y;
			}

			if(!dragWidget(x,y)) {
				float xs = (float)((double)x/zoom)-(float)focus.x;
				float ys = (float)((double)y/zoom)-(float)focus.y;
//				float w = (float)map.width;
//				float h = (float)map.height;
				float xh = xs-(float)map.getHeight()*TWH;
				float xm = (xh/TWH+ys/THH)/2.0f;
				float ym = (ys/THH-xh/TWH)/2.0f;
//				if(xm>=w) xm -= w;
//				else if(xm<0) xm += w;
//				if(ym>=h) ym -= h;
//				else if(ym<0) ym += h;
				if(!handleTouchDragged(xs,ys,xm,ym)) {
					if(dragging && (x!=dragScreenX || y!=dragScreenY))
						setView(dragX+(x-dragScreenX),dragY+(y-dragScreenY));
				}
			}
		}
		touch[pointer].x = x;
		touch[pointer].y = y;
		if(zooming) {
			double z;
			x = dragScreenX-x;
			y = dragScreenY-y;
			z = Math.sqrt(x*x+y*y);
			z = zoomBase*(z/zoomRate);
			if(z<1.0) z = 1.0;
			else if(z>4.0) z = 4.0;
//			z = Math.round(z);
			if(z!=zoom) {
				zoom = z;
				view.set(display).divide(zoom);
				zoomPoint.set(zoomFocus).multiply(zoom);
				setView(zoomPoint.x+touch[0].x,zoomPoint.y+touch[0].y);
//				map.setFocusPoint((float)focus.x,(float)focus.y,(float)((double)touch[0].x/zoom),(float)((double)touch[0].y/zoom));
				mapCamera.setToOrtho(true,view.width,view.height);
			}
		}
		return true;
	}

	public abstract boolean handleTouchDown(float xs,float ys,float xm,float ym);
	public abstract boolean handleTouchUp(float xs,float ys,float xm,float ym);
	public abstract boolean handleTouchDragged(float xs,float ys,float xm,float ym);

	@Override
	public void setDisplaySize(int w,int h) {
		display.set(w,h);
		view.set(display).divide(zoom);
		focus.multiply(zoom);
		adjustViewPoint(focus,focus);
	}

	public void setView(int x,int y) {
		focus.set(x,y);
		adjustViewPoint(focus,focus);
	}

	public void moveView(int x,int y) {
		focus.multiply(zoom).move(x,y);
		adjustViewPoint(focus,focus);
	}

	public void adjustViewPoint(Point f,Point m) {
		int x = (int)Math.round((double)f.x/zoom);
		int y = (int)Math.round((double)f.y/zoom);
		int w = view.width;
		int h = view.height;
		int mw = map.getWidthPoints();
		int mh = map.getHeightPoints();

		if(w+TILE_WIDTH_HALF<mw) {
			if(x<=w-mw) x = w-mw;
			if(x>0) x = 0;
		} else { /* Center view */
			x = (w-mw)/2;
		}
		if(h+TILE_HEIGHT_HALF<mh) {
			if(y<=h-mh) y = h-mh;
			if(y>0) y = 0;
		} else { /* Center view */
			y = (h-mh)/2;
		}

		if(f!=m) f.set(x,y).multiply(zoom);
		m.set(x,y);
	}

	public void drawBar(int x,int y,int bar,float rate) {
		Clip f;
		f = main.getClip(CLIP_BAR);
		batch.draw(main.getTexture(TEXTURE_MARKERS),x-f.centerX,y-f.centerY,f.width,f.height,f.x,f.y,f.width,f.height,false,true);
		f = main.getClip(bar);
		int n = (int)((float)f.width*rate);
		batch.draw(main.getTexture(TEXTURE_MARKERS),x-f.centerX,y-f.centerY,n,f.height,f.x,f.y,n,f.height,false,true);
	}
}


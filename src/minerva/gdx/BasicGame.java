package com.spirangle.minerva.gdx;

import java.util.Locale;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader.BitmapFontParameter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.I18NBundle;

public abstract class BasicGame extends Game {
	public static BasicGame game;
	public static I18NBundle str;

	private AssetManager assets;

	private BitmapFont[] fonts      = null;
	private Texture[] textures      = null;
	private Sound[] sounds          = null;
	private Music[] music           = null;

	private int bgmusic             = -1;

	private static float fpsTimer   = 0.0f;

	public static void sleepFPS(float fps,float delta) {
		delta += fpsTimer;
		float rate = 1.0f/fps;
		float sleep = rate-delta;
		if(sleep>0.0f) {
			fpsTimer = 0.0f;
		} else {
			fpsTimer = delta-rate;
			sleep = rate*0.5f;
		}
		try {
//BasicGame.log("BasicGame.sleepFPS(rate: "+rate+", delta: "+delta+", sleep: "+sleep+", fpsTimer: "+fpsTimer+")");
			Thread.sleep((long)(sleep*1000.f));
		} catch (InterruptedException ex) {
BasicGame.log("BasicGame.sleepFPS(Exception: "+ex.getMessage()+"\n"+getStackTraceString(ex)+"\n)");
		}
	}

	public static void log(String message) {
		Gdx.app.log(game.getAppName(),message);
	}

	public BasicGame() {
		BasicGame.game = this;
	}

	@Override
	public void create() {
BasicGame.log("BasicGame.create()");
		int i;
		String[] files;

		assets = new AssetManager();

		files = getTextureFiles();
		textures = new Texture[files.length];
		for(i=0; i<files.length; ++i) {
			assets.load(files[i],Texture.class);
			textures[i] = null;
		}

		files = getFontFiles();
		fonts = new BitmapFont[files.length];
		BitmapFontParameter fontParameter = new BitmapFontParameter();
		fontParameter.flip = true;
		for(i=0; i<files.length; ++i) {
			assets.load(files[i],BitmapFont.class,fontParameter);
			fonts[i] = null;
		}

		files = getSoundFiles();
		sounds = new Sound[files.length];
		for(i=0; i<files.length; ++i) {
			assets.load(files[i],Sound.class);
			sounds[i] = null;
		}

		files = getMusicFiles();
		music = new Music[files.length];
		for(i=0; i<files.length; ++i) {
			assets.load(files[i],Music.class);
			music[i] = null;
		}

		assets.load(getBundleBaseFileHandle(),I18NBundle.class);
	}

	@Override
	public void dispose() {
BasicGame.log("BasicGame.dispose()");
		stopMusic();

		Screen s = getScreen();
		s.dispose();

		super.dispose();
		assets.dispose();
	}

	public void loadingAssetsCompleted() {
		createGlobalObjects();
	}

	/** Creates all global objects.
	 */
	public void createGlobalObjects() {
		int i;
		String[] files;

		files = getTextureFiles();
		for(i=0; i<files.length; ++i)
			textures[i] = assets.get(files[i],Texture.class);

		files = getFontFiles();
		for(i=0; i<files.length; ++i)
			fonts[i] = assets.get(files[i],BitmapFont.class);

		files = getSoundFiles();
		for(i=0; i<files.length; ++i)
			sounds[i] = assets.get(files[i],Sound.class);

		files = getMusicFiles();
		for(i=0; i<files.length; ++i)
			music[i] = assets.get(files[i],Music.class);

		str = assets.get(getBundleBaseFileHandle(),I18NBundle.class);
	}

	public abstract String getAppName();

	public abstract String[] getTextureFiles();
	public abstract String[] getFontFiles();
	public abstract String[] getSoundFiles();
	public abstract String[] getMusicFiles();

	public abstract String getTextureFileName(int i);
	public abstract String getFontFileName(int i);
	public abstract String getSoundFileName(int i);
	public abstract String getMusicFileName(int i);

	public abstract String getBundleBaseFileHandle();

	public AssetManager getAssetManager() { return assets; }

	public BitmapFont getFont(int i) { return fonts[i]; }
	public Texture getTexture(int i) { return textures[i]; }

	public abstract Texture getGUITexture();

	public long playSound(int n) { return sounds[n].play(); }
	public long playSound(int n,float v) { return sounds[n].play(v); }
	public long playSound(int n,float v,float p,float a) { return sounds[n].play(v,p,a); }
	public long loopSound(int n) { return sounds[n].loop(); }
	public long loopSound(int n,float v) { return sounds[n].loop(v); }
	public long loopSound(int n,float v,float p,float a) { return sounds[n].loop(v,p,a); }
	public void stopSound(int n) { sounds[n].stop(); }
	public void stopSound(int n,long id) { sounds[n].stop(id); }

	public void playMusic(int n) {
		if(bgmusic>=0) music[bgmusic].stop();
		bgmusic = n<0 || n>=music.length? -1 : n;
		if(bgmusic>=0) {
			music[bgmusic].play();
			music[bgmusic].setLooping(true);
		}
	}
	public void stopMusic() {
		if(bgmusic>=0) music[bgmusic].stop();
		bgmusic = -1;
	}

	public static String getStackTraceString(Throwable e) {
		return getStackTraceString(e,"");
	}

	private static String getStackTraceString(Throwable e,String indent) {
		StringBuilder sb = new StringBuilder();
		sb.append(e.toString());
		sb.append("\n");

		StackTraceElement[] stack = e.getStackTrace();
		if(stack!=null) {
			for(StackTraceElement stackTraceElement : stack) {
				sb.append(indent);
				sb.append("\tat ");
				sb.append(stackTraceElement.toString());
				sb.append("\n");
			}
		}

		Throwable[] suppressedExceptions = e.getSuppressed();
		// Print suppressed exceptions indented one level deeper.
		if(suppressedExceptions!=null) {
			for(Throwable throwable : suppressedExceptions) {
				sb.append(indent);
				sb.append("\tSuppressed: ");
				sb.append(getStackTraceString(throwable,indent+"\t"));
			}
		}

		Throwable cause = e.getCause();
		if(cause!=null) {
			sb.append(indent);
			sb.append("Caused by: ");
			sb.append(getStackTraceString(cause,indent));
		}

		return sb.toString();
	}
}


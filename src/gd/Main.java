package com.seshat.gd;

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
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.seshat.AnimationSet;
import com.seshat.Animation;
import com.seshat.Clip;

/**
 * The Main class handles loading, initialising and disposing of
 * data and resources used globally by the application. It also handles
 * which screen to display.
 */
public class Main extends Game implements Data {
	private static final String className = "Main";

	private static final String[] fontFiles = {
		"profont12w.fnt",
		"profont12b.fnt",
		"risque18.fnt",
	};

	private static final String[] textureFiles = {
		"ui.png",
		"floors.png",
		"walls.png",
		"sprite-dwarf.png",
		"markers.png",
		"shadows.png"
	};

	private static final String[] soundFiles = {
	};

	private static final String[] musicFiles = {
		"I Wanna Be Loved.ogg",
		"I Found A New Baby.ogg"
	};

	private AssetManager assets;
	private BitmapFont[] fonts      = null;
	private Texture[] textures      = null;
	private Sound[] sounds          = null;
	private Music[] music           = null;

	private Map map;
	private Screen screen;
	private Clip[] clips            = null;
	private AnimationSet[] sprites  = null;
	private int bgmusic             = -1;

	public static void sleepFPS(float fps,float delta) {
		if(delta<fps) {
			try {
				Thread.sleep((long)((fps-delta)*1000.f));
			} catch (InterruptedException ex) {
//TrollGame.log("GameScreen.render(Exception: "+e.getMessage()+"\n"+TrollGame.getStackTraceString(e)+"\n)");
			}
		}
	}

	@Override
	public void create() {
		Gdx.app.log(APP_NAME,className+":create()");
		int i;
		assets = new AssetManager();
		BitmapFontParameter fontParameter = new BitmapFontParameter();
		fontParameter.flip = true;
		for(i=0; i<fontFiles.length; ++i) {
			assets.load(fontFiles[i],BitmapFont.class,fontParameter);
		}
		for(i=0; i<textureFiles.length; ++i) {
			assets.load(textureFiles[i],Texture.class);
		}
		for(i=0; i<soundFiles.length; ++i) {
			assets.load(soundFiles[i],Sound.class);
		}
		for(i=0; i<musicFiles.length; ++i) {
			assets.load(musicFiles[i],Music.class);
		}

		setScreen(new LoadingScreen(this));
	}

	@Override
	public void dispose() {
		Gdx.app.log(APP_NAME,className+":dispose()");

		stopMusic();

		Screen s = getScreen();
		s.dispose();

		super.dispose();
		assets.dispose();
	}

	public void loadingAssetsCompleted() {
		createGlobalObjects();
		loadDungeon();
	}

	/** Creates all global objects.
	 */
	public void createGlobalObjects() {
		int i;
		Texture t;

		fonts = new BitmapFont[FONTS];
		for(i=0; i<fontFiles.length; ++i) {
			fonts[i] = assets.get(fontFiles[i],BitmapFont.class);
		}
		textures = new Texture[TEXTURES];
		for(i=0; i<textureFiles.length; ++i) {
			textures[i] = assets.get(textureFiles[i],Texture.class);
		}
/*		sounds = new Sound[soundFiles.length];
		for(i=0; i<soundFiles.length; ++i) {
			assets.load(soundFiles[i],Sound.class);
		}*/
		music = new Music[musicFiles.length];
		for(i=0; i<musicFiles.length; ++i) {
			music[i] = assets.get(musicFiles[i],Music.class);
		}

		clips = new Clip[CLIPS];
		t = textures[TEXTURE_MARKERS];
		clips[CLIP_BAR]       = new Clip(t, 20, 35,34, 8,17, 8,false);
		clips[CLIP_BAR_HP]    = new Clip(t, 22, 44,30, 4,15, 6,false);
		clips[CLIP_BAR_2]     = new Clip(t, 22, 49,30, 4,15, 6,false);
		clips[CLIP_BAR_3]     = new Clip(t, 22, 54,30, 4,15, 6,false);
		clips[CLIP_BAR_4]     = new Clip(t, 22, 59,30, 4,15, 6,false);
		t = textures[TEXTURE_SHADOWS];
		clips[CLIP_SHADOW_1]  = new Clip(t,  2,  2,32,16,12,10,false);

		t = textures[TEXTURE_FLOORS];
		Clip[] floor_mud_frames = new Clip[] {
			new Clip(t,  1,  1,62,32,32, 0,false),
		};
		Clip[] floor_stone_frames = new Clip[] {
			new Clip(t, 65,  1,62,32,32, 0,false),
		};
		Clip[] floor_cslate_frames = new Clip[] {
			new Clip(t,129,  1,62,32,32, 0,false),
		};
		Clip[] floor_check_frames = new Clip[] {
			new Clip(t,193,  1,62,32,32, 0,false),
		};

		t = textures[TEXTURE_WALLS];
		Clip[] wall_mud_frames = new Clip[] {
			new Clip(t,  1,  1,62,44,32,12,false),
			new Clip(t,  1,  1,62,44,32,12,false),
			new Clip(t,  1,  1,62,44,32,12,false),
			new Clip(t,  1,  1,62,44,32,12,false),
			new Clip(t,  1,  1,62,44,32,12,false),
			new Clip(t,  1,  1,62,44,32,12,false),
			new Clip(t,  1,  1,62,44,32,12,false),
			new Clip(t,  1,  1,62,44,32,12,false),
			new Clip(t, 65,  1,62,44,32,12,false),
			new Clip(t, 65,  1,62,44,32,12,false),
			new Clip(t, 65,  1,62,44,32,12,false),
			new Clip(t, 65,  1,62,44,32,12,false),
			new Clip(t,129,  1,62,44,32,12,false),
			new Clip(t,129,  1,62,44,32,12,false),
			new Clip(t,193,  1,62,44,32,12,false),
			new Clip(t,193,  1,62,44,32,12,false),
			new Clip(t,257,  1,62,44,32,12,false),
			new Clip(t,257,  1,62,44,32,12,false),
			new Clip(t,321,  1,62,44,32,12,false),
//			new Clip(t,129, 47,62,44,32,12,false),
//			new Clip(t,193, 47,62,44,32,12,false),
		};
		Clip[] wall_stone_frames = new Clip[] {
			new Clip(t,  1, 93,62,45,32,13,false),
			new Clip(t,  1, 93,62,45,32,13,false),
			new Clip(t,  1, 93,62,45,32,13,false),
			new Clip(t,  1, 93,62,45,32,13,false),
			new Clip(t, 65, 93,62,45,32,13,false),
		};
		Clip[] wall_rusted_iron_frames = new Clip[] {
			new Clip(t,129, 93,62,45,32,13,false),
		};
		Clip[] wall_steel_frames = new Clip[] {
			new Clip(t,193, 93,62,45,32,13,false),
		};
		Clip[] wall_amethyst_frames = new Clip[] {
			new Clip(t,257, 93,62,45,32,13,false),
		};

		t = textures[TEXTURE_MARKERS];
		Clip[] floor_marker_frames = new Clip[] {
			new Clip(t,  0,  0,62,32,32, 0,false),
		};
		Clip[] unit_marker_frames = new Clip[] {
			new Clip(t,  0, 32,15,29, 8,30,false),
		};

		t = textures[TEXTURE_SPRITE_DWARF];
		Clip[] dwarf_frames = new Clip[] {
			new Clip(t,  6, 65, 48, 60, 20, 57,false), //  0. Stand     NE
			new Clip(t, 57, 66, 50, 60, 21, 56,false), //  1. Walk 1    NE
			new Clip(t,111, 66, 45, 60, 19, 56,false), //  2. Walk 2    NE
			new Clip(t, 11,  3, 34, 61, 17, 57,false), //  3. Stand     SE
			new Clip(t, 49,  4, 36, 60, 17, 56,false), //  4. Walk 1    SE
			new Clip(t, 88,  4, 33, 58, 17, 56,false), //  5. Walk 2    SE
			new Clip(t, 11,  3, 34, 61, 17, 57, true), //  6. Stand     SW
			new Clip(t, 49,  4, 36, 60, 17, 56, true), //  7. Walk 1    SW
			new Clip(t, 88,  4, 33, 58, 17, 56, true), //  8. Walk 2    SW
			new Clip(t,  6, 65, 48, 60, 20, 57, true), //  9. Stand     NW
			new Clip(t, 57, 66, 50, 60, 21, 56, true), // 10. Walk 1    NW
			new Clip(t,111, 66, 45, 60, 19, 56, true), // 11. Walk 2    NW
			new Clip(t, 11,189, 43, 64, 19, 61,false), // 12. Battle 1  NE
			new Clip(t, 83,193, 51, 60, 20, 57,false), // 13. Battle 2  NE
			new Clip(t,144,193, 56, 60, 21, 57,false), // 14. Battle 3  NE
			new Clip(t, 19,127, 53, 61, 34, 57,false), // 15. Battle 1  SE
			new Clip(t, 98,127, 46, 61, 29, 57,false), // 16. Battle 2  SE
			new Clip(t,148,127, 36, 61, 17, 57,false), // 17. Battle 3  SE
			new Clip(t, 19,127, 53, 61, 34, 57, true), // 18. Battle 1  SW
			new Clip(t, 98,127, 46, 61, 29, 57, true), // 19. Battle 2  SW
			new Clip(t,148,127, 36, 61, 17, 57, true), // 20. Battle 3  SW
			new Clip(t, 11,189, 43, 64, 19, 61, true), // 21. Battle 1  NW
			new Clip(t, 83,193, 51, 60, 20, 57, true), // 22. Battle 2  NW
			new Clip(t,144,193, 56, 60, 21, 57, true), // 23. Battle 3  NW
		};

		Animation[] dwarf_anims = new Animation[] {
			new Animation(1.0f,new int[] {  0          },new float[] { 1.0f                }, 0.0f), // Stand NE
			new Animation(1.0f,new int[] {  3          },new float[] { 1.0f                }, 0.0f), // Stand SE
			new Animation(1.0f,new int[] {  6          },new float[] { 1.0f                }, 0.0f), // Stand SW
			new Animation(1.0f,new int[] {  9          },new float[] { 1.0f                }, 0.0f), // Stand NW
			new Animation(1.0f,new int[] {  1, 0, 2, 0 },new float[] { 1.0f,1.0f,1.0f,1.0f }, 0.0f), // Walk NE
			new Animation(1.0f,new int[] {  4, 3, 5, 3 },new float[] { 1.0f,1.0f,1.0f,1.0f }, 0.0f), // Walk SE
			new Animation(1.0f,new int[] {  7, 6, 8, 6 },new float[] { 1.0f,1.0f,1.0f,1.0f }, 0.0f), // Walk SW
			new Animation(1.0f,new int[] { 10, 9,11, 9 },new float[] { 1.0f,1.0f,1.0f,1.0f }, 0.0f), // Walk NW
			new Animation(1.0f,new int[] { 12,13,14, 0 },new float[] { 1.5f,1.0f,1.5f,2.5f }, 2.5f), // Battle NE
			new Animation(1.0f,new int[] { 15,16,17, 3 },new float[] { 1.5f,1.0f,1.5f,2.5f }, 2.5f), // Battle SE
			new Animation(1.0f,new int[] { 18,19,20, 6 },new float[] { 1.5f,1.0f,1.5f,2.5f }, 2.5f), // Battle SW
			new Animation(1.0f,new int[] { 21,22,23, 9 },new float[] { 1.5f,1.0f,1.5f,2.5f }, 2.5f), // Battle NW
		};

		sprites = new AnimationSet[SPRITES];
		sprites[SPRITE_FLOOR_MUD]         = new AnimationSet(32,32,62,32, floor_mud_frames,         0,null        );
		sprites[SPRITE_FLOOR_STONE]       = new AnimationSet(32,32,62,32, floor_stone_frames,       0,null        );
		sprites[SPRITE_FLOOR_CSLATE]      = new AnimationSet(32,32,62,32, floor_cslate_frames,      0,null        );
		sprites[SPRITE_FLOOR_CHECK]       = new AnimationSet(32,32,62,32, floor_check_frames,       0,null        );

		sprites[SPRITE_WALL_MUD]          = new AnimationSet(32,44,62,44, wall_mud_frames,          0,null        );
		sprites[SPRITE_WALL_STONE]        = new AnimationSet(32,45,62,45, wall_stone_frames,        0,null        );
		sprites[SPRITE_WALL_RUSTED_IRON]  = new AnimationSet(32,45,62,45, wall_rusted_iron_frames,  0,null        );
		sprites[SPRITE_WALL_STEEL]        = new AnimationSet(32,45,62,45, wall_steel_frames,        0,null        );
		sprites[SPRITE_WALL_AMETHYST]     = new AnimationSet(32,45,62,45, wall_amethyst_frames,     0,null        );

		sprites[SPRITE_FLOOR_MARKER]      = new AnimationSet(32,32,62,32, floor_marker_frames,      0,null        );
		sprites[SPRITE_UNIT_MARKER]       = new AnimationSet( 8,30,15,29, unit_marker_frames,       0,null        );

		sprites[SPRITE_DWARF]             = new AnimationSet( 9,48,17,58, dwarf_frames,             0,dwarf_anims );

		Wall.wallMud.sprite               = sprites[SPRITE_WALL_MUD];
		Wall.wallStone.sprite             = sprites[SPRITE_WALL_STONE];
		Wall.wallRustedIron.sprite        = sprites[SPRITE_WALL_RUSTED_IRON];
		Wall.wallSteel.sprite             = sprites[SPRITE_WALL_STEEL];
		Wall.wallAmethyst.sprite          = sprites[SPRITE_WALL_AMETHYST];

		Champion.championDwarf1.sprite    = sprites[SPRITE_DWARF];
		Champion.championDwarf1.shadow    = clips[CLIP_SHADOW_1];
	}

	/** Load a dungeon and start a new Screen.
	 */
	private void loadDungeon() {
		Wizard def,att;
		Map map = new Map(this);
		try {
			FileHandle file = Gdx.files.local("dungeon.json");
			Gdx.app.log(APP_NAME,className+":create(file: "+file+")");
			map.create(file.readString());
		} catch(Exception ex) {
			FileHandle file = Gdx.files.internal("dungeon-default.json");
			Gdx.app.log(APP_NAME,className+":create(file: "+file+")");
			map.create(file.readString());
//			Gdx.app.log(APP_NAME,className+":create(message: "+ex.getMessage()+")");
//			map.create(0,0,0,40,40,mapFloorTiles);
		}

		def = new Wizard(1,"Abra-Melin");
		def.setTeam(new Unit[] {
			new Champion(def,1,CHAMPION_DWARF_1,map, 3.5f, 3.5f),
			new Champion(def,2,CHAMPION_DWARF_1,map,19.5f,19.5f),
			new Champion(def,3,CHAMPION_DWARF_1,map,27.5f, 4.5f)
		});
		map.setDefender(def);

		att = new Wizard(2,"Agrippa");
		att.setTeam(new Unit[] {
			new Champion(att,4,CHAMPION_DWARF_1),//,map,27.5f,19.5f),
			new Champion(att,5,CHAMPION_DWARF_1),//,map, 6.5f,22.5f),
			new Champion(att,6,CHAMPION_DWARF_1)//,map,26.5f,28.5f)
		});
		map.setAttacker(att);

Gdx.app.log(APP_NAME,className+":loadDungeon(1)");
		Screen s0 = getScreen();
		s0.dispose();
Gdx.app.log(APP_NAME,className+":loadDungeon(2)");
		BasicScreen s = new DungeonEditorScreen(this,map);
Gdx.app.log(APP_NAME,className+":loadDungeon(3)");
		setScreen(s);
Gdx.app.log(APP_NAME,className+":loadDungeon(4)");
//		playMusic(1);
	}

	/** Download a map from the http-server, and load the dungeon.
	 */
	private void downloadMap() {
		String url = "http://seshat.comlu.com/gd/map.json.php";
		String content = "q=libgdx&example=example";
		HttpRequest httpGet = new HttpRequest(HttpMethods.GET);
		httpGet.setUrl(url);
		httpGet.setContent(content);
		Gdx.net.sendHttpRequest(httpGet,new HttpResponseListener() {
			public void handleHttpResponse(HttpResponse httpResponse) {
				String response = httpResponse.getResultAsString();
				Gdx.app.log(APP_NAME,className+":create(HttpResponse: "+response+")");
			}
			public void failed(Throwable t) {}
			public void cancelled() {}
		});
	}

	public String getFontFileName(int i) { return fontFiles[i]; }
	public String getTextureFileName(int i) { return textureFiles[i]; }
	public String getSoundFileName(int i) { return soundFiles[i]; }
	public String getMusicFileName(int i) { return musicFiles[i]; }

	public AssetManager getAssetManager() { return assets; }

	public BitmapFont getFont(int i) { return fonts[i]; }
	public Texture getTexture(int i) { return textures[i]; }
	public Clip getClip(int i) { return clips[i]; }
	public AnimationSet getSprite(int i) { return sprites[i]; }

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
		if(n>=music.length) n = -1;
		bgmusic = n;
		if(bgmusic>=0) {
			music[bgmusic].play();
			music[bgmusic].setLooping(true);
		}
	}
	public void stopMusic() { playMusic(-1); }
}


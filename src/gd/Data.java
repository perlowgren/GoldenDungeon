package com.seshat.gd;

/**
 * This interface contains static data which can be accessed by all classes
 * implementing it.
 */
public interface Data {
	public static final String APP_NAME                = "GoldenDungeon";

	/** Screen portrait orientation. */
	public static final int PORTRAIT                   = 0;
	/** Screen landscape orientation. */
	public static final int LANDSCAPE                  = 1;

	/** Minimum velocity of panning. */
	public static final double MIN_PAN_VEL             = 0.5;
	/** Maximum velocity of panning. */
	public static final double MAX_PAN_VEL             = 50.0;

	/** Frames per second. */
	public static final int FPS                        = 20;
	/** Seconds per frame. */
	public static float FRAME_RATE                     = 1.0f/(float)FPS;

	public static final int TILE_WIDTH                 = 62;
	public static final int TILE_HEIGHT                = 32;
	public static final int TILE_WIDTH_HALF            = 32;
	public static final int TILE_HEIGHT_HALF           = 16;

	public static final float TW                       = (float)TILE_WIDTH;
	public static final float TH                       = (float)TILE_HEIGHT;
	public static final float TWH                      = (float)TILE_WIDTH_HALF;
	public static final float THH                      = (float)TILE_HEIGHT_HALF;
	public static final float TWP                      = 1.0f/TW;
	public static final float THP                      = 1.0f/TH;
	public static final float TWHP                     = 1.0f/TWH;
	public static final float THHP                     = 1.0f/THH;

	public static final int GAME_WINNER_DEF            = 0x0001;
	public static final int GAME_WINNER_ATT            = 0x0002;
	public static final int GAME_ENDED                 = 0x0003;
	public static final int GAME_MAP_HWRAP             = 0x0004;
	public static final int GAME_MAP_VWRAP             = 0x0008;

	public static final int FONT_PROFONT12W            = 0;
	public static final int FONT_PROFONT12B            = 1;
	public static final int FONT_RISQUE18              = 2;
	public static final int FONTS                      = 3;

	public static final int TEXTURE_UI                 = 0;
	public static final int TEXTURE_FLOORS             = 1;
	public static final int TEXTURE_WALLS              = 2;
	public static final int TEXTURE_SPRITES            = 3;
	public static final int TEXTURE_SPRITE_DWARF       = 3;
	public static final int TEXTURE_MARKERS            = 4;
	public static final int TEXTURE_SHADOWS            = 5;
	public static final int TEXTURES                   = 6;

	public static final int CLIP_BAR                   = 0;
	public static final int CLIP_BAR_HP                = 1;
	public static final int CLIP_BAR_2                 = 2;
	public static final int CLIP_BAR_3                 = 3;
	public static final int CLIP_BAR_4                 = 4;
	public static final int CLIP_SHADOW_1              = 5;
	public static final int CLIPS                      = 6;

	public static final int SPRITE_FLOORS              = 0;
	public static final int SPRITE_FLOOR_MUD           = 0;
	public static final int SPRITE_FLOOR_STONE         = 1;
	public static final int SPRITE_FLOOR_CSLATE        = 2;
	public static final int SPRITE_FLOOR_CHECK         = 3;
	public static final int SPRITE_WALLS               = 4;
	public static final int SPRITE_WALL_MUD            = 4;
	public static final int SPRITE_WALL_STONE          = 5;
	public static final int SPRITE_WALL_RUSTED_IRON    = 6;
	public static final int SPRITE_WALL_STEEL          = 7;
	public static final int SPRITE_WALL_AMETHYST       = 8;
	public static final int SPRITE_DWARF               = 9;
	public static final int SPRITE_MARKERS             = 10;
	public static final int SPRITE_FLOOR_MARKER        = 10;
	public static final int SPRITE_UNIT_MARKER         = 11;
	public static final int SPRITES                    = 12;

	public static final int WALL_MUD                   = 1;
	public static final int WALL_STONE                 = 2;
	public static final int WALL_RUSTED_IRON           = 3;
	public static final int WALL_STEEL                 = 4;
	public static final int WALL_AMETHYST              = 5;
	public static final int WALLS                      = 5;

	public static final int UNIT_EFFECT                = 1;
	public static final int UNIT_STRUCTURE             = 2;
	public static final int UNIT_CHAMPION              = 3;
	public static final int UNITS                      = 3;

	public static final int EFFECT_FADE                = 1;
	public static final int EFFECTS                    = 1;

	public static final int STRUCTURE_CHEST            = 1;
	public static final int STRUCTURES                 = 1;

	public static final int CHAMPION_DWARF_1           = 1;
	public static final int CHAMPIONS                  = 1;

	public static final int DIR_NORTH                  = 0;
	public static final int DIR_EAST                   = 1;
	public static final int DIR_SOUTH                  = 2;
	public static final int DIR_WEST                   = 3;
}


package propra2012.gruppe33.bomberman;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;

/**
 * 
 * Here we store the grid constants.
 * 
 * @author Christopher Probst
 * @author Malte Schmidt
 * 
 */
public interface GameConstants {

	int START_SPEED_ITEMS = 5;

	Vector2f BOMB_SCALE = new Vector2f(0.4f, 0.4f);
	Vector2f BOMB_SCALE_VELOCITY = new Vector2f(0.1f, 0.1f);

	Vector2f ITEM_SCALE = new Vector2f(0.6f, 0.6f);
	Vector2f PLAYER_SCALE = new Vector2f(1.5f, 1.5f);

	String MAP_NAME_KEY = "map";
	String MAP_WIDTH_PROP = "width";
	String MAP_HEIGHT_PROP = "height";

	/*
	 * The next map!
	 */
	String NEXT = "next";

	// ************ SOUND START
	String EXP_SOUND = "exp_sound";
	String EAT_SOUND = "eat_sound";

	String SHIELD_ON_SOUND = "shield_on_sound";
	String SHIELD_OFF_SOUND = "shield_off_sound";

	String PICKUP_SOUND = "pickup_sound";
	String PLACE_SOUND = "place_sound";

	String GLASS_SOUND = "glass_sound";

	// ************ SOUND END

	// ************ ITEMS GUI START
	String ITEM_INTERFACE = "item_interface";
	// ************ ITEMS GUI END

	String ACTIVE_FLARE_IMAGE = "active_flare_image";
	
	// ************ ITEMS START
	String EXP_SPRITE = "exp_sprite";
	String EXP_SPRITE_WIDTH = "exp_sprite_columns";
	String EXP_SPRITE_HEIGHT = "exp_sprite_rows";

	String DEFAULT_BOMB_IMAGE = "default_bomb_image";
	String DEFAULT_BOMB_BAG_IMAGE = "default_bomb_bag_image";
	int DEFAULT_BOMB_RANGE = 1;
	float DEFAULT_BOMB_DELAY = 3;

	String NUKE_BOMB_IMAGE = "nuke_bomb_image";
	String NUKE_BOMB_BAG_IMAGE = "nuke_bomb_bag_image";
	int NUKE_BOMB_RANGE = 3;
	float NUKE_BOMB_DELAY = 3;

	String FAST_BOMB_IMAGE = "fast_bomb_image";
	String FAST_BOMB_BAG_IMAGE = "fast_bomb_bag_image";
	int FAST_BOMB_RANGE = 1;
	float FAST_BOMB_DELAY = 1;

	String PALISADE_HORI_IMAGE = "palisade_hori_image";
	String PALISADE_VERT_IMAGE = "palisade_vert_image";
	String PALISADE_BAG_IMAGE = "palisade_bag_image";

	String SHIELD_IMAGE = "shield_image";
	String SHIELD_POTION_IMAGE = "shield_potion_image";

	// ************ ITEMS END

	// ************ PERM - ITEMS START
	String SPEED_IMAGE = "speed_image";
	String SLOW_SHROOM_IMAGE = "slow_shroom_image";
	String FAST_SHROOM_IMAGE = "fast_shroom_image";
	// ************ PERM - ITEMS END

	// ************ NATURE START

	String BREAKABLE_IMAGE = "breakable_image";
	String SOLID_IMAGE = "solid_image";
	String GROUND_IMAGE = "ground_image";
	String ESCAPE_IMAGE = "escape_image";

	// ************ NATURE END

	// ************ WALLS START
	String CORNER_LD_IMAGE = "corner_ld_image";
	String CORNER_LU_IMAGE = "corner_lu_image";
	String CORNER_RD_IMAGE = "corner_rd_image";
	String CORNER_RU_IMAGE = "corner_ru_image";

	String WALL_D_IMAGE = "wall_d_image";
	String WALL_U_IMAGE = "wall_u_image";
	String WALL_L_IMAGE = "wall_l_image";
	String WALL_R_IMAGE = "wall_r_image";
	// ************ WALLS END

	String SESSIONS_KEY = "sessions";
	String PLAYERS_KEY = "players";

	int BACKGROUND_INDEX = -10;
	int EXPLOSION_INDEX = 100000;
	int SHIELD_INDEX = 100;
	int ITEM_INDEX = -1;

	String BOMB_TAG = "bomb";
	String SOLID_TAG = "solid";
	String BREAKABLE_TAG = "breakable";
	String PLAYER_TAG = "player";
	String FREE_TAG = "free";
	String EXPLOSION_TAG = "explosion";
	String SHIELD_TAG = "shield";

	/**
	 * MAP FILE CONSTANTS!
	 */

	char UP_BARRIER = 'u';
	char DOWN_BARRIER = 'd';
	char LEFT_BARRIER = 'l';
	char RIGHT_BARRIER = 'r';

	char UP_LEFT_CORNER = '(';
	char UP_RIGHT_CORNER = ')';

	char DOWN_LEFT_CORNER = '[';
	char DOWN_RIGHT_CORNER = ']';

	char SPAWN = 's';

	/**
	 * Free fields use the defined default speed. To configure field with a
	 * custom speed you can use the numbers 0-9.
	 * <p>
	 * ' ' = 100% of default speed.
	 * <p>
	 * <b>Range '0' to '4'</b>
	 * <p>
	 * <ul>
	 * <li>'0' = 0% of default speed.</li>
	 * <li>'1' = 20% of default speed.</li>
	 * <li>'2' = 40% of default speed.</li>
	 * <li>'3' = 60% of default speed.</li>
	 * <li>'4' = 80% of default speed.</li>
	 * </ul>
	 * 
	 * <p>
	 * <b>Range '5' to '9'</b>
	 * <p>
	 * <ul>
	 * <li>'5' = 120% of default speed.</li>
	 * <li>'6' = 140% of default speed.</li>
	 * <li>'7' = 160% of default speed.</li>
	 * <li>'8' = 180% of default speed.</li>
	 * <li>'9' = 200% of default speed.</li>
	 * </ul>
	 */
	char FREE = ' ';

	char SOLID = '#';

	/**
	 * This value specifies the offset of a breakable object. To setup a
	 * breakable field on which you can go with 60% of the default speed just
	 * <p>
	 * <code>char field = '3' + BREAKABLE_OFFSET;</code>
	 */
	char BREAKABLE_OFFSET = 10000;
}

package propra2012.gruppe33.bomberman.graphics.sprite;

import propra2012.gruppe33.engine.graphics.sprite.AnimationBundle;
import propra2012.gruppe33.engine.graphics.sprite.Sprite;
import propra2012.gruppe33.engine.resources.assets.AssetManager;

/**
 * Utility class to bundle some default animation routines.
 * 
 * @author Christopher Probst
 * @author Matthias Hesse
 * @author Malte Schmidt
 */
public final class AnimationRoutines {

	public static final String RUN_PREFIX = "run_", DIE_PREFIX = "die_";

	/**
	 * Creates an animated knight char with the default keys.
	 * 
	 * @param assetManager
	 *            The asset manager which contains the sprite.
	 * @param dieSpeed
	 *            The speed of dying :D.
	 * @param moveSpeed
	 *            The speed of moving :D.
	 * @return a new animation bundle with all animations.
	 * @throws Exception
	 *             If an exception occurs.
	 */
	public static AnimationBundle createKnight(AssetManager assetManager,
			long dieSpeed, long moveSpeed) throws Exception {
		// Load the knight sprite
		Sprite knightSprite = new Sprite(assetManager.loadImage(
				"assets/images/animated/chars/knight.png", false), 9, 9);

		// Create a new map
		AnimationBundle knight = new AnimationBundle();

		/*
		 * Load the die animation.
		 */
		knight.add(knightSprite.newAnimationFromRange(DIE_PREFIX + "north",
				dieSpeed, 5, 4, 9));
		knight.add(knightSprite.newAnimationFromRange(DIE_PREFIX + "south",
				dieSpeed, 5, 5, 9));
		knight.add(knightSprite.newAnimationFromRange(DIE_PREFIX + "west",
				dieSpeed, 5, 6, 9));
		knight.add(knightSprite.newAnimationFromRange(DIE_PREFIX + "east",
				dieSpeed, 5, 3, 9));

		/*
		 * Load the run animation.
		 */
		knight.add(knightSprite.newAnimationFromRange(RUN_PREFIX + "north",
				moveSpeed, 8, 0, 8));
		knight.add(knightSprite.newAnimationFromRange(RUN_PREFIX + "south",
				moveSpeed, 7, 1, 8));
		knight.add(knightSprite.newAnimationFromRange(RUN_PREFIX + "west",
				moveSpeed, 6, 2, 8));
		knight.add(knightSprite.newAnimationFromRange(RUN_PREFIX + "east",
				moveSpeed, 0, 0, 8));

		return knight;
	}

	// Should not be instantiated
	private AnimationRoutines() {
	}
}

package propra2012.gruppe33.routines;

import java.io.IOException;

import propra2012.gruppe33.graphics.assets.AssetManager;
import propra2012.gruppe33.graphics.sprite.AnimationMap;
import propra2012.gruppe33.graphics.sprite.Sprite;

/**
 * Utility class to bundle some default animation routines.
 * 
 * @author Christopher Probst
 * 
 */
public final class AnimationRoutines {

	/**
	 * Creates an animated knight char with the default keys.
	 * 
	 * @param assetManager
	 *            The asset manager which contains the sprite.
	 * @param speed
	 *            The speed of the animation
	 * @return a new animation map with all animations.
	 * @throws IOException
	 *             If an exception occurs.
	 */
	public static AnimationMap createKnight(AssetManager assetManager,
			long speed) throws Exception {
		// Load the knight sprite
		Sprite knightSprite = assetManager.loadSprite(
				"assets/images/animated/chars/knight.png", 9, 9);

		// Create a new map
		AnimationMap knight = new AnimationMap();

		/*
		 * Load the die animation.
		 */
		knight.addAnimation(knightSprite.newAnimationFromRange("die_north",
				speed, 5, 4, 9));
		knight.addAnimation(knightSprite.newAnimationFromRange("die_south",
				speed, 5, 5, 9));
		knight.addAnimation(knightSprite.newAnimationFromRange("die_west",
				speed, 5, 6, 9));
		knight.addAnimation(knightSprite.newAnimationFromRange("die_east",
				speed, 5, 3, 9));

		/*
		 * Load the run animation.
		 */
		knight.addAnimation(knightSprite.newAnimationFromRange("run_north",
				speed, 8, 0, 8));
		knight.addAnimation(knightSprite.newAnimationFromRange("run_south",
				speed, 7, 1, 8));
		knight.addAnimation(knightSprite.newAnimationFromRange("run_west",
				speed, 6, 2, 8));
		knight.addAnimation(knightSprite.newAnimationFromRange("run_east",
				speed, 0, 0, 8));

		return knight;
	}

	// Should not be instantiated
	private AnimationRoutines() {
	}
}

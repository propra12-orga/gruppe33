package propra2012.gruppe33.bomberman.graphics.sprite;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridController;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridController.GridControllerEvent;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.animation.RenderedAnimation;
import propra2012.gruppe33.engine.graphics.sprite.Animation;
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

	public static Entity createGridControllerAnimationHandler(
			final RenderedAnimation renderedAnimation) {
		if (renderedAnimation == null) {
			throw new NullPointerException("renderedAnimation");
		}

		return new Entity() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(Entity source, Object event, Object... params) {
				super.onEvent(source, event, params);

				if (event instanceof GridControllerEvent) {

					switch ((GridControllerEvent) event) {
					case DirectionChanged:

						GridController gc = (GridController) params[0];

						// Set active animation
						renderedAnimation.animationName(RUN_PREFIX
								+ gc.direction().toString().toLowerCase());

						// Lookup the animation
						Animation running = renderedAnimation.animation();

						if (running != null) {
							running.reset().loop(true).paused(!gc.isMoving());
						}

						break;

					case MovingChanged:

						gc = (GridController) params[0];

						// Lookup the animation
						running = renderedAnimation.animation();

						if (running != null) {
							// Update running state
							running.paused(!gc.isMoving());
						}

						break;
					}

				}
			}
		};
	}

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

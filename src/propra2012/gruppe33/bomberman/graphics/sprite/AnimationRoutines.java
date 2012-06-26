package propra2012.gruppe33.bomberman.graphics.sprite;

import java.awt.geom.AffineTransform;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.animation.RenderedAnimation;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f.Direction;
import com.indyforge.twod.engine.graphics.sprite.Animation;
import com.indyforge.twod.engine.graphics.sprite.AnimationBundle;
import com.indyforge.twod.engine.graphics.sprite.Sprite;
import com.indyforge.twod.engine.resources.assets.AssetManager;

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
	 * Creates a grid movement animation handler to switch animations during
	 * running.
	 * 
	 * @param renderedAnimation
	 * @return
	 */
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

			private Vector2f lastAbsolutePosition = null;
			private Direction lastDirection = Direction.Undefined;

			@Override
			protected void onUpdate(float tpf) {
				super.onUpdate(tpf);

				// Get new transform
				AffineTransform at = renderedAnimation.lastWorldTransform();
				Vector2f newAbsolutePosition = new Vector2f(
						(float) at.getTranslateX(), (float) at.getTranslateY());

				// Does the player has moved ?
				boolean moved = lastAbsolutePosition != null ? !newAbsolutePosition
						.equals(lastAbsolutePosition) : false;

				// Get new direction
				Direction newDirection = moved ? (newAbsolutePosition
						.sub(lastAbsolutePosition)).nearestDirection()
						: Direction.Undefined;

				// Save the change
				boolean dirChanged = newDirection != Direction.Undefined ? newDirection != lastDirection
						: false;

				// Set
				if (dirChanged && moved) {
					// Set active animation
					renderedAnimation.animationName(RUN_PREFIX
							+ newDirection.toString().toLowerCase());

					// Save
					lastDirection = newDirection;
				}

				// Lookup the animation
				Animation running = renderedAnimation.animation();

				if (running != null) {
					if (dirChanged) {
						running.reset();
					}

					running.loop(true).paused(!moved);
				}

				// Store
				lastAbsolutePosition = newAbsolutePosition;
			}
		};
	}

	/**
	 * Creates an animated santa claus char with the default keys.
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
	public static AnimationBundle createSanta(AssetManager assetManager,
			long dieSpeed, long moveSpeed) throws Exception {
		// Load the santa sprite
		Sprite santaSprite = new Sprite(assetManager.loadImage(
				"assets/images/animated/chars/santa.png", false), 10, 10);

		// Create a new map
		AnimationBundle santa = new AnimationBundle();

		/*
		 * Load the die animation.
		 */
		santa.add(
				santaSprite.newAnimationFromRange(DIE_PREFIX + "north",
						dieSpeed, 5, 4, 13)).loop(true).paused(true);
		santa.add(
				santaSprite.newAnimationFromRange(DIE_PREFIX + "south",
						dieSpeed, 8, 5, 13)).loop(true).paused(true);
		santa.add(
				santaSprite.newAnimationFromRange(DIE_PREFIX + "west",
						dieSpeed, 1, 7, 13)).loop(true).paused(true);
		santa.add(
				santaSprite.newAnimationFromRange(DIE_PREFIX + "east",
						dieSpeed, 2, 3, 13)).loop(true).paused(true);

		/*
		 * Load the run animation.
		 */
		santa.add(
				santaSprite.newAnimationFromRange(RUN_PREFIX + "north",
						moveSpeed, 8, 0, 8)).loop(true).paused(true);
		santa.add(
				santaSprite.newAnimationFromRange(RUN_PREFIX + "south",
						moveSpeed, 6, 1, 8)).loop(true).paused(true);
		santa.add(
				santaSprite.newAnimationFromRange(RUN_PREFIX + "west",
						moveSpeed, 4, 2, 8)).loop(true).paused(true);
		santa.add(
				santaSprite.newAnimationFromRange(RUN_PREFIX + "east",
						moveSpeed, 0, 0, 8)).loop(true).paused(true);

		return santa;
	}

	/**
	 * Creates an animated wizard char with the default keys.
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
	public static AnimationBundle createWizard(AssetManager assetManager,
			long dieSpeed, long moveSpeed) throws Exception {
		// Load the wizard sprite
		Sprite wizardSprite = new Sprite(assetManager.loadImage(
				"assets/images/animated/chars/wizard.png", false), 9, 9);

		// Create a new map
		AnimationBundle wizard = new AnimationBundle();

		/*
		 * Load the die animation.
		 */
		wizard.add(
				wizardSprite.newAnimationFromRange(DIE_PREFIX + "north",
						dieSpeed, 1, 1, 10)).loop(true).paused(true);
		wizard.add(
				wizardSprite.newAnimationFromRange(DIE_PREFIX + "south",
						dieSpeed, 2, 2, 10)).loop(true).paused(true);
		wizard.add(
				wizardSprite.newAnimationFromRange(DIE_PREFIX + "west",
						dieSpeed, 4, 4, 10)).loop(true).paused(true);
		wizard.add(
				wizardSprite.newAnimationFromRange(DIE_PREFIX + "east",
						dieSpeed, 0, 0, 10)).loop(true).paused(true);

		/*
		 * Load the run animation.
		 */
		wizard.add(
				wizardSprite.newAnimationFromRange(RUN_PREFIX + "north",
						moveSpeed, 3, 5, 8)).loop(true).paused(true);
		wizard.add(
				wizardSprite.newAnimationFromRange(RUN_PREFIX + "south",
						moveSpeed, 2, 6, 8)).loop(true).paused(true);
		wizard.add(
				wizardSprite.newAnimationFromRange(RUN_PREFIX + "west",
						moveSpeed, 1, 7, 8)).loop(true).paused(true);
		wizard.add(
				wizardSprite.newAnimationFromRange(RUN_PREFIX + "east",
						moveSpeed, 4, 4, 8)).loop(true).paused(true);

		return wizard;
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
		knight.add(
				knightSprite.newAnimationFromRange(DIE_PREFIX + "north",
						dieSpeed, 5, 4, 9)).loop(true).paused(true);
		knight.add(
				knightSprite.newAnimationFromRange(DIE_PREFIX + "south",
						dieSpeed, 5, 5, 9)).loop(true).paused(true);
		knight.add(
				knightSprite.newAnimationFromRange(DIE_PREFIX + "west",
						dieSpeed, 5, 6, 9)).loop(true).paused(true);
		knight.add(
				knightSprite.newAnimationFromRange(DIE_PREFIX + "east",
						dieSpeed, 5, 3, 9)).loop(true).paused(true);

		/*
		 * Load the run animation.
		 */
		knight.add(
				knightSprite.newAnimationFromRange(RUN_PREFIX + "north",
						moveSpeed, 8, 0, 8)).loop(true).paused(true);
		knight.add(
				knightSprite.newAnimationFromRange(RUN_PREFIX + "south",
						moveSpeed, 7, 1, 8)).loop(true).paused(true);
		knight.add(
				knightSprite.newAnimationFromRange(RUN_PREFIX + "west",
						moveSpeed, 6, 2, 8)).loop(true).paused(true);
		knight.add(
				knightSprite.newAnimationFromRange(RUN_PREFIX + "east",
						moveSpeed, 0, 0, 8)).loop(true).paused(true);

		return knight;
	}

	/**
	 * Creates an animated dwarf char with the default keys.
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
	public static AnimationBundle createDwarf(AssetManager assetManager,
			long dieSpeed, long moveSpeed) throws Exception {
		// Load the dwarf sprite
		Sprite dwarfSprite = new Sprite(assetManager.loadImage(
				"assets/images/animated/chars/dwarf.png", false), 9, 9);

		// Create a new map
		AnimationBundle dwarf = new AnimationBundle();

		/*
		 * Load the die animation.
		 */
		dwarf.add(
				dwarfSprite.newAnimationFromRange(DIE_PREFIX + "north",
						dieSpeed, 7, 4, 11)).loop(true).paused(true);
		dwarf.add(
				dwarfSprite.newAnimationFromRange(DIE_PREFIX + "south",
						dieSpeed, 0, 6, 11)).loop(true).paused(true);
		dwarf.add(
				dwarfSprite.newAnimationFromRange(DIE_PREFIX + "west",
						dieSpeed, 2, 7, 11)).loop(true).paused(true);
		dwarf.add(
				dwarfSprite.newAnimationFromRange(DIE_PREFIX + "east",
						dieSpeed, 5, 3, 11)).loop(true).paused(true);

		/*
		 * Load the run animation.
		 */
		dwarf.add(
				dwarfSprite.newAnimationFromRange(RUN_PREFIX + "north",
						moveSpeed, 8, 0, 8)).loop(true).paused(true);
		dwarf.add(
				dwarfSprite.newAnimationFromRange(RUN_PREFIX + "south",
						moveSpeed, 7, 1, 8)).loop(true).paused(true);
		dwarf.add(
				dwarfSprite.newAnimationFromRange(RUN_PREFIX + "west",
						moveSpeed, 6, 2, 8)).loop(true).paused(true);
		dwarf.add(
				dwarfSprite.newAnimationFromRange(RUN_PREFIX + "east",
						moveSpeed, 0, 0, 8)).loop(true).paused(true);

		return dwarf;
	}

	// Should not be instantiated
	private AnimationRoutines() {
	}
}

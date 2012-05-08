package propra2012.gruppe33.graphics.rendering;

import propra2012.gruppe33.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.graphics.rendering.scenegraph.animation.AnimationController;
import propra2012.gruppe33.graphics.rendering.scenegraph.grid.Grid;
import propra2012.gruppe33.graphics.rendering.scenegraph.grid.GridController;
import propra2012.gruppe33.graphics.rendering.scenegraph.timeout.TimeoutController;
import propra2012.gruppe33.graphics.sprite.AnimationMap;
import propra2012.gruppe33.graphics.sprite.Sprite;

/**
 * Utility class to bundle some default entity routines.
 * 
 * @author Christopher Probst
 * 
 */
public final class EntityRoutines {

	public static Entity createBoom(Grid grid, Sprite sprite, int x, int y,
			float dur) throws Exception {

		Entity explosion = new Entity("boom");
		AnimationController ac = new AnimationController();
		ac.getAnimationMap().addAnimation(
				sprite.newAnimationFromRange("main", 20, 0, 0, 25));
		ac.setAnimation("main", true);
		explosion.putController(ac);
		explosion.getPosition().set(grid.gridToWorld(x, y));
		explosion.getScale().set(grid.getRasterWidth(), grid.getRasterHeight());
		explosion.putController(new TimeoutController(dur) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onTimeout(Entity entity) {
				entity.detach();
			}
		});
		return explosion;
	}

	/**
	 * Creates a new local player using the given animation map and the coords.
	 * 
	 * @param name
	 *            The name of the new local player.
	 * @param charAnimation
	 *            The character animation.
	 * @param grid
	 *            The grid.
	 * @param sx
	 *            The spawn x.
	 * @param sy
	 *            The spawn y.
	 * @return the player.
	 */
	public static Entity createLocalPlayer(String name,
			AnimationMap charAnimation, Grid grid, int sx, int sy) {

		// Check the coords
		if (!grid.validate(sx, sy)) {
			throw new IllegalArgumentException("sx and/or sy out of range");
		}

		// Create the player entity
		Entity player = new Entity(name);

		// Create a new animation controller
		final AnimationController animation = new AnimationController(
				charAnimation);

		// Attach animation
		player.putController(animation);

		// Attach new grid controller
		player.putController(new GridController());

		// Adjust scale the grid size
		player.getScale().set(grid.getRasterWidth(), grid.getRasterHeight());

		// Set position
		player.getPosition().set(grid.vectorAt(sx, sy));

		return player;
	}

	// Should not be instantiated
	private EntityRoutines() {
	}
}

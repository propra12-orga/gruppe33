package propra2012.gruppe33.bomberman.graphics.rendering;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.Grid;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.animation.AnimationController;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.timeout.TimeoutController;
import propra2012.gruppe33.engine.graphics.sprite.AnimationMap;
import propra2012.gruppe33.engine.graphics.sprite.Sprite;

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
	 * Creates a new field entity using the given animation map and the coords.
	 * 
	 * @param name
	 *            The name of the new field entity.
	 * @param animationMap
	 *            The animation map of the field entity.
	 * @param grid
	 *            The grid.
	 * @param sx
	 *            The spawn x.
	 * @param sy
	 *            The spawn y.
	 * @return the field entity.
	 */
	public static Entity createFieldEntity(String name,
			AnimationMap animationMap, Grid grid, int sx, int sy) {

		// Check the coords
		if (!grid.validate(sx, sy)) {
			throw new IllegalArgumentException("sx and/or sy out of range");
		}

		// Create the field entity
		Entity field = new Entity(name);

		// Put animation controller if not null
		if (animationMap != null) {
			// Create a new animation controller
			AnimationController animation = new AnimationController(
					animationMap);

			// Attach animation
			field.putController(animation);
		}

		// Adjust scale the grid size
		field.getScale().set(grid.getRasterWidth(), grid.getRasterHeight());

		// Set position
		field.getPosition().set(grid.vectorAt(sx, sy));

		return field;
	}

	// Should not be instantiated
	private EntityRoutines() {
	}
}

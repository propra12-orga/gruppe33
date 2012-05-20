package propra2012.gruppe33.bomberman.graphics.rendering;

import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.Grid;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridController;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.DefaultEntityController;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Vector2f;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.animation.RenderedAnimation;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.timeout.Timeout;
import propra2012.gruppe33.engine.graphics.sprite.Animation;
import propra2012.gruppe33.engine.graphics.sprite.AnimationBundle;
import propra2012.gruppe33.engine.graphics.sprite.Sprite;

/**
 * Utility class to bundle some default entity routines.
 * 
 * @author Christopher Probst
 * 
 */
public final class EntityRoutines {

	public static Entity createBomb(Grid grid, Sprite sprite, String name,
			Point p, int distance, float dur) {
		return createBomb(grid, sprite, name, p.x, p.y, distance, dur);
	}

	public static Map<Point, Timeout> BOMBS = new HashMap<Point, Timeout>();

	public static Entity PLAYER;

	public static Entity createBomb(Grid grid, Sprite sprite, String name,
			int x, int y, int distance, float dur) {

		// Collect all points
		List<Point> points = grid.collectPoints(x, y, distance, null);

		// Create animation controller
		RenderedAnimation explosion = new RenderedAnimation() {
			@Override
			public void onMessage(Entity entity, Object message, Object... args) {

				if (message == Message.LastImage) {
					entity.detach();
				}
			}

		};

		explosion.getAnimationMap().addAnimation(
				sprite.newAnimationFromRange("main",
						(long) ((dur / 25) * 1000), 0, 0, 25));
		explosion.setAnimationName("main", true);
		explosion.getAnimation().setLoop(false);
		explosion.getAnimation().setPaused(false);

		// The entity bomb
		Entity bomb = new Entity(name);

		// Set position
		bomb.getPosition().set(grid.vectorAt(x, y));

		bomb.putController(new DefaultEntityController() {
			@Override
			public void onChildDetached(Entity entity, Entity child) {

				if (!entity.hasChildren()) {
					entity.detach();
				}
			}
		});

		for (Point p : points) {

			Timeout tc = BOMBS.remove(p);
			if (tc != null) {
				tc.setTimeout(0);
			}

			if (PLAYER != null) {
				// Is the player dead now ?
				if (grid.worldToNearestPoint(PLAYER.getPosition()).equals(p)) {

					RenderedAnimation ac = PLAYER
							.getController(RenderedAnimation.class);

					ac.setAnimationName(
							"die_"
									+ PLAYER.getController(GridController.class)
											.getDirection().toString()
											.toLowerCase(), true);

					PLAYER.removeController(GridController.class);

					Animation running = ac.getAnimationMap().get(
							ac.getAnimation());

					running.setLoop(false);
					running.setPaused(false);

					PLAYER = null;
				}
			}

			// Get world vector
			Vector2f v = grid.vectorAt(p);

			// Create new bomb part
			Entity bombPart = new Entity(name + p);

			// Use explosion controller
			bombPart.putController(explosion);

			// Set position relative to bomb
			bombPart.getPosition().set(v.sub(bomb.getPosition()));

			// Config scale
			bombPart.getScale().set(grid.getRasterWidth(),
					grid.getRasterHeight());

			// Attach the bomb part
			bomb.attach(bombPart);
		}

		return bomb;
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
			AnimationBundle animationMap, Grid grid, int sx, int sy) {

		// Check the coords
		if (!grid.validate(sx, sy)) {
			throw new IllegalArgumentException("sx and/or sy out of range");
		}

		// Create the field entity
		Entity field = new Entity(name);

		// Put animation controller if not null
		if (animationMap != null) {
			// Create a new animation controller
			RenderedAnimation animation = new RenderedAnimation(
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

package propra2012.gruppe33.bomberman.graphics.rendering;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.Grid;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridController;
import propra2012.gruppe33.bomberman.graphics.sprite.AnimationRoutines;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.GraphicsEntity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.animation.RenderedAnimation;
import propra2012.gruppe33.engine.graphics.sprite.Sprite;

/**
 * Utility class to bundle some default entity routines.
 * 
 * @author Christopher Probst
 * 
 */
public final class EntityRoutines {

	// public static Entity createBomb(Grid grid, Sprite sprite, String name,
	// Point p, int distance, float dur) {
	// return createBomb(grid, sprite, name, p.x, p.y, distance, dur);
	// }
	//
	// public static Map<Point, Timeout> BOMBS = new HashMap<Point, Timeout>();
	//
	// public static Entity PLAYER;
	//
	// public static Entity createBomb(Grid grid, Sprite sprite, String name,
	// int x, int y, int distance, float dur) {
	//
	// // Collect all points
	// List<Point> points = grid.collectPoints(x, y, distance, null);
	//
	// // Create animation controller
	// RenderedAnimation explosion = new RenderedAnimation() {
	// @Override
	// public void onMessage(Entity entity, Object message, Object... args) {
	//
	// if (message == Message.LastImage) {
	// entity.detach();
	// }
	// }
	//
	// };
	//
	// explosion.getAnimationMap().addAnimation(
	// sprite.newAnimationFromRange("main",
	// (long) ((dur / 25) * 1000), 0, 0, 25));
	// explosion.setAnimationName("main", true);
	// explosion.getAnimation().setLoop(false);
	// explosion.getAnimation().setPaused(false);
	//
	// // The entity bomb
	// Entity bomb = new Entity(name);
	//
	// // Set position
	// bomb.getPosition().set(grid.vectorAt(x, y));
	//
	// bomb.putController(new DefaultEntityController() {
	// @Override
	// public void onChildDetached(Entity entity, Entity child) {
	//
	// if (!entity.hasChildren()) {
	// entity.detach();
	// }
	// }
	// });
	//
	// for (Point p : points) {
	//
	// Timeout tc = BOMBS.remove(p);
	// if (tc != null) {
	// tc.setTimeout(0);
	// }
	//
	// if (PLAYER != null) {
	// // Is the player dead now ?
	// if (grid.worldToNearestPoint(PLAYER.getPosition()).equals(p)) {
	//
	// RenderedAnimation ac = PLAYER
	// .getController(RenderedAnimation.class);
	//
	// ac.setAnimationName(
	// "die_"
	// + PLAYER.getController(GridController.class)
	// .getDirection().toString()
	// .toLowerCase(), true);
	//
	// PLAYER.removeController(GridController.class);
	//
	// Animation running = ac.getAnimationMap().get(
	// ac.getAnimation());
	//
	// running.setLoop(false);
	// running.setPaused(false);
	//
	// PLAYER = null;
	// }
	// }
	//
	// // Get world vector
	// Vector2f v = grid.vectorAt(p);
	//
	// // Create new bomb part
	// Entity bombPart = new Entity(name + p);
	//
	// // Use explosion controller
	// bombPart.putController(explosion);
	//
	// // Set position relative to bomb
	// bombPart.getPosition().set(v.sub(bomb.getPosition()));
	//
	// // Config scale
	// bombPart.getScale().set(grid.getRasterWidth(),
	// grid.getRasterHeight());
	//
	// // Attach the bomb part
	// bomb.attach(bombPart);
	// }
	//
	// return bomb;
	// }

	
	public static GraphicsEntity createPlayer(Grid grid, int sx, int sy)
			throws Exception {
		// Create a new local player
		GraphicsEntity player = EntityRoutines.createFieldEntity(grid, sx, sy);

		// Create knight animation
		RenderedAnimation charAni = new RenderedAnimation(
				AnimationRoutines.createKnight(grid.assetManager(), 33, 33));

		// Attach char ani
		player.attach(charAni);

		// Use default grid control
		player.attach(new GridController());

		// Swap animations
		player.attach(AnimationRoutines
				.createGridControllerAnimationHandler(charAni));

		player.scale().scaleLocal(1.5f);

		return player;
	}

	public static GraphicsEntity createFieldEntity(Grid grid, int sx, int sy) {

		// Check the coords
		if (!grid.validate(sx, sy)) {
			throw new IllegalArgumentException("sx and/or sy out of range");
		}

		// Create the field entity
		GraphicsEntity field = new GraphicsEntity();

		// Adjust scale the grid size
		field.scale().set(grid.getRasterWidth(), grid.getRasterHeight());

		// Set position
		field.position().set(grid.vectorAt(sx, sy));

		return field;
	}

	// Should not be instantiated
	private EntityRoutines() {
	}
}

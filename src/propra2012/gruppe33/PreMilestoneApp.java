package propra2012.gruppe33;

import java.awt.Color;
import java.io.File;

import propra2012.gruppe33.bomberman.graphics.rendering.EntityRoutines;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.Grid;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.GraphicsEntity;
import propra2012.gruppe33.engine.resources.assets.AssetManager;

/**
 * 
 * @author Christopher Probst
 */
public class PreMilestoneApp {

	public static Grid createDemoGame() throws Exception {

		AssetManager assets = new AssetManager(new File("scenes/default.zip"));

		// Load grid from file
		Grid grid = new Grid(assets, "assets/maps/smallmap.txt", 2048, 2048);

		// Define the chars on which the character can move
		grid.maxFieldVelocities().put('0', 600f);
		grid.maxFieldVelocities().put('3', 600f);

		// Same as vec fields
		grid.defaultCollectChars().addAll(grid.maxFieldVelocities().keySet());

		grid.defaultLineOfSightChars().add('0');
		grid.defaultLineOfSightChars().add('2');

		// Create ground
		Entity ground = EntityRoutines.createGround(grid);

		// Create walls
		Entity walls = EntityRoutines.createWalls(grid);

		// Create the solid blocks
		Entity solids = EntityRoutines.createSolidBlocks(grid);

		// Create a new local player
		GraphicsEntity player = EntityRoutines.createPlayer("Kr0e", grid, 1, 1);

		GraphicsEntity merged = new GraphicsEntity();
		merged.attach(ground);
		merged.attach(walls);
		merged.attach(solids);
		merged = grid.renderedOpaqueEntity(Color.white, merged);

		grid.attach(merged);

		// Attach child
		grid.attach(player);

		// EntityRoutines.PLAYER = player;
		//
		// player.putController(new DefaultEntityController() {
		//
		// boolean valid = true;
		//
		// @Override
		// public void doUpdate(Entity entity, float tpf) {
		//
		// final Grid grid = (Grid) entity.findParent(new TypeFilter(
		// Grid.class, false), true);
		//
		// if (grid != null) {
		//
		// if (grid.isPressed(KeyEvent.VK_SPACE)) {
		//
		// final Point exp = grid.worldToNearestPoint(entity
		// .getPosition());
		//
		// if (valid && !EntityRoutines.BOMBS.containsKey(exp)) {
		// valid = false;
		//
		// final char oldChar = grid.charAt(exp, '3');
		//
		// Timeout tc = new Timeout(3);
		//
		// EntityController ec = new DefaultEntityController() {
		//
		// public void onMessage(Entity entity,
		// Object message, Object[] args) {
		//
		// if (message == TimeoutController.Timeout.Timeout) {
		// Entity bomb = EntityRoutines
		// .createBomb(grid, boom,
		// entity.getName()
		// + " bomb", exp,
		// 10, 0.25f);
		//
		// Entity p = entity.getParent();
		//
		// entity.detach();
		//
		// p.attach(bomb);
		//
		// grid.charAt(exp, oldChar);
		//
		// }
		// }
		// };
		//
		// EntityRoutines.BOMBS.put(exp, tc);
		//
		// Entity b = new Entity("waiting");
		// b.putController(new RenderedImage(bombImage));
		//
		// b.setOrder(-1);
		// b.getScale().set(grid.getRasterWidth(),
		// grid.getRasterHeight());
		//
		// b.putController(ec);
		// b.putController(tc);
		//
		// b.getPosition().set(grid.vectorAt(exp));
		//
		// entity.getParent().attach(b);
		//
		// b.getScale().scaleLocal(0.5f);
		// b.putController(new TransformMotor());
		// b.getController(TransformMotor.class)
		// .setScaleVelocity(new Vector2f(20f, 20f));
		//
		// }
		// } else {
		// valid = true;
		// }
		// }
		// }
		// });

		return grid;
	}
}

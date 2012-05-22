package propra2012.gruppe33;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import propra2012.gruppe33.bomberman.graphics.rendering.EntityRoutines;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.Grid;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.GraphicsEntity;
import propra2012.gruppe33.engine.graphics.sprite.Sprite;
import propra2012.gruppe33.engine.resources.Resource;
import propra2012.gruppe33.engine.resources.assets.Asset;
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
		grid.getMaxFieldVelocities().put('0', 600f);
		grid.getMaxFieldVelocities().put('3', 600f);

		// Same as vec fields
		grid.getDefaultCollectChars().addAll(
				grid.getMaxFieldVelocities().keySet());

		grid.getDefaultLineOfSightChars().add('0');
		grid.getDefaultLineOfSightChars().add('2');

		// Render solid block
		Asset<BufferedImage> solidImage = grid.assetManager().loadImage(
				"assets/images/box1.png", true);

		Asset<BufferedImage> groundImage = grid.assetManager().loadImage(
				"assets/images/ground3.jpg", true);

		final Asset<BufferedImage> bombImage = grid.assetManager().loadImage(
				"assets/images/bomb.png", true);

		final Asset<BufferedImage> wallUP = grid.assetManager().loadImage(
				"assets/images/wall/wallUP.png", true);

		final Asset<BufferedImage> wallDOWN = grid.assetManager().loadImage(
				"assets/images/wall/wallDOWN.png", true);

		final Asset<BufferedImage> wallLEFT = grid.assetManager().loadImage(
				"assets/images/wall/wallLEFT.png", true);

		final Asset<BufferedImage> wallRIGHT = grid.assetManager().loadImage(
				"assets/images/wall/wallRIGHT.png", true);

		final Asset<BufferedImage> rc = grid.assetManager().loadImage(
				"assets/images/wall/cornerRU.png", true);

		final Asset<BufferedImage> lc = grid.assetManager().loadImage(
				"assets/images/wall/cornerLU.png", true);

		Map<Character, Resource<? extends Image>> map = new HashMap<Character, Resource<? extends Image>>();
		map.put('1', solidImage);
		map.put('9', wallUP);
		map.put('8', wallDOWN);

		map.put('7', wallLEFT);
		map.put('6', wallRIGHT);

		map.put('a', rc);
		map.put('b', lc);

		Map<Character, Resource<? extends Image>> floorMap = new HashMap<Character, Resource<? extends Image>>();
		floorMap.put('1', groundImage);
		floorMap.put('0', groundImage);
		floorMap.put('9', groundImage);
		floorMap.put('8', groundImage);
		floorMap.put('7', groundImage);
		floorMap.put('6', groundImage);
		floorMap.put('b', groundImage);
		floorMap.put('a', groundImage);

		// Bundle render to picture
		Entity ground = grid.bundle(floorMap);

		// Bundle render to picture
		Entity solid = grid.bundle(map);

		// Create a new local player
		GraphicsEntity player = EntityRoutines.createPlayer(grid, 1, 1);

		// Load sprite
		final Sprite boom = new Sprite(grid.assetManager().loadImage(
				"assets/images/animated/boom.png", false), 5, 5);

		GraphicsEntity merged = new GraphicsEntity();
		merged.attach(ground);
		merged.attach(solid);

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

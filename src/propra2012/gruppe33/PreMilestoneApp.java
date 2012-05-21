package propra2012.gruppe33;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import propra2012.gruppe33.bomberman.graphics.rendering.EntityRoutines;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.Grid;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridController;
import propra2012.gruppe33.bomberman.graphics.sprite.AnimationRoutines;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.EntityController;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.DefaultEntityController;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.RenderedImage;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.TransformMotor;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Vector2f;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.animation.RenderedAnimation;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.timeout.Timeout;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.util.TypeFilter;
import propra2012.gruppe33.engine.graphics.sprite.Animation;
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
		Grid grid = new Grid("root", assets, "assets/maps/smallmap.txt", 2048,
				2048);

		// Define the chars on which the character can move
		grid.getMaxFieldVelocities().put('0', 600f);
		grid.getMaxFieldVelocities().put('2', 300f);
		grid.getMaxFieldVelocities().put('3', 600f);

		// Same as vec fields
		grid.getDefaultCollectChars().addAll(
				grid.getMaxFieldVelocities().keySet());

		grid.getDefaultLineOfSightChars().add('0');
		grid.getDefaultLineOfSightChars().add('2');

		// Render solid block
		Asset<BufferedImage> solidImage = grid.getAssetManager().loadImage(
				"assets/images/solid.png", true);

		final Asset<BufferedImage> bombImage = grid.getAssetManager()
				.loadImage("assets/images/bomb.png", true);

		Map<Character, Resource<? extends Image>> map = new HashMap<Character, Resource<? extends Image>>();
		map.put('1', solidImage);

		// Bundle render to picture
		Entity solid = grid.bundleToRenderedEntity("solid", map,
				Transparency.OPAQUE, Color.white);

		// Create a new local player
		Entity player = EntityRoutines.createFieldEntity("Kr0e",
				AnimationRoutines.createKnight(grid.getAssetManager(), 33),
				grid, 1, 1);

		// Use default grid control
		player.putController(new GridController());

		player.putController(new DefaultEntityController() {
			@Override
			public void onMessage(Entity entity, Object message, Object... args) {

				/*
				 * If this entity has an animation controller let us update the
				 * image.
				 */
				RenderedAnimation ani = entity
						.getController(RenderedAnimation.class);

				if (message == GridController.GridController.DirectionChanged) {

					GridController gc = (GridController) args[0];

					// Set active animation
					ani.setAnimationName("run_"
							+ gc.getDirection().toString().toLowerCase(), true);

					ani.getAnimation().setLoop(true);
					ani.getAnimation().setPaused(!gc.isMoving());

				} else if (message == GridController.GridController.MovingChanged) {

					GridController gc = (GridController) args[0];

					// Lookup the animation
					Animation running = ani.getAnimationMap().get(
							ani.getAnimation());

					// Update running state
					if (running != null) {
						running.setPaused(!gc.isMoving());
					}
				}
			}
		});

		// Load sprite
		final Sprite boom = new Sprite(grid.getAssetManager().loadImage(
				"assets/images/animated/boom.png", false), 5, 5);

		player.getScale().scaleLocal(2);

		// Put to background
		solid.setOrder(-100);

		// Attach solid blocks
		grid.attach(solid);

		// Attach child
		grid.attach(player);

		EntityRoutines.PLAYER = player;

		player.putController(new DefaultEntityController() {

			boolean valid = true;

			@Override
			public void doUpdate(Entity entity, float tpf) {

				final Grid grid = (Grid) entity.findParent(new TypeFilter(
						Grid.class, false), true);

				if (grid != null) {

					if (grid.isPressed(KeyEvent.VK_SPACE)) {

						final Point exp = grid.worldToNearestPoint(entity
								.getPosition());

						if (valid && !EntityRoutines.BOMBS.containsKey(exp)) {
							valid = false;

							final char oldChar = grid.charAt(exp, '3');

							Timeout tc = new Timeout(3);

							EntityController ec = new DefaultEntityController() {

								public void onMessage(Entity entity,
										Object message, Object[] args) {

									if (message == TimeoutController.Timeout.Timeout) {
										Entity bomb = EntityRoutines
												.createBomb(grid, boom,
														entity.getName()
																+ " bomb", exp,
														10, 0.25f);

										Entity p = entity.getParent();

										entity.detach();

										p.attach(bomb);

										grid.charAt(exp, oldChar);

									}
								}
							};

							EntityRoutines.BOMBS.put(exp, tc);

							Entity b = new Entity("waiting");
							b.putController(new RenderedImage(bombImage));

							b.setOrder(-1);
							b.getScale().set(grid.getRasterWidth(),
									grid.getRasterHeight());

							b.putController(ec);
							b.putController(tc);

							b.getPosition().set(grid.vectorAt(exp));

							entity.getParent().attach(b);

							b.getScale().scaleLocal(0.5f);
							b.putController(new TransformMotor());
							b.getController(TransformMotor.class)
									.setScaleVelocity(new Vector2f(20f, 20f));

						}
					} else {
						valid = true;
					}
				}
			}
		});

		return grid;
	}
}

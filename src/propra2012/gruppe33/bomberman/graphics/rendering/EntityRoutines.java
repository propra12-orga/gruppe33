package propra2012.gruppe33.bomberman.graphics.rendering;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import propra2012.gruppe33.PreMilestoneApp;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.Grid;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridController;
import propra2012.gruppe33.bomberman.graphics.sprite.AnimationRoutines;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.GraphicsEntity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.RenderedImage;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Scene;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.SceneProcessor;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.TransformMotor;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.animation.RenderedAnimation;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.animation.RenderedAnimation.AnimationEvent;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.timeout.DetachOnTimeout;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.timeout.Timeout;
import propra2012.gruppe33.engine.graphics.sprite.Sprite;
import propra2012.gruppe33.engine.resources.Resource;
import propra2012.gruppe33.engine.resources.assets.Asset;

/**
 * Utility class to bundle some default entity routines.
 * 
 * @author Christopher Probst
 * 
 */
public final class EntityRoutines {

	public static Point randomExit;
	public static Asset<BufferedImage> exit;
	public static boolean active = false;

	// Here we can store all items
	public static final Map<Point, GraphicsEntity> items = new HashMap<Point, GraphicsEntity>();

	// Here we can store all explosions
	public static final Map<Point, RenderedAnimation> explosions = new HashMap<Point, RenderedAnimation>();

	public static boolean createBomb(final Grid grid, final Sprite sprite,
			Resource<? extends Image> bombImage, int x, int y, float growSize,
			float timeout, final int range) {

		// Create point for the explosion
		final Point p = new Point(x, y);

		// Lookup older explosion
		GraphicsEntity item = items.get(p);

		if (item != null) {

			return false;
		}

		// Create new entity
		item = new RenderedImage(bombImage) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onDetached() {
				super.onDetached();
				items.remove(p);

				List<Point> points = grid.collectPoints(p, range, null);

				for (Point pp : points) {
					createExplosion(grid, sprite, pp.x, pp.y);

					grid.charAt(pp, '0');
				}
			}
		};
		item.tags().add("bomb");
		items.put(p, item);

		// Set index to 10
		item.index(-1);

		// Adjust position and scale
		item.scale().set(grid.rasterWidth() / 3, grid.rasterHeight() / 3);
		item.position().set(grid.vectorAt(x, y));

		// Create transform motor
		TransformMotor tm = new TransformMotor();
		tm.scaleVelocity().set(1, 1).scaleLocal(growSize);

		// Attach motor
		item.attach(tm);

		// Attach timeout
		item.attach(new Timeout(timeout).attach(new DetachOnTimeout()));

		// Attach to grid
		grid.attach(item);

		return true;
	}

	public static boolean createExplosion(Grid grid, Sprite sprite, int x, int y) {

		// Create point for the explosion
		final Point p = new Point(x, y);

		GraphicsEntity bomb = items.get(p);

		if (bomb != null) {

			bomb.detach();

			if (p.equals(randomExit)) {
				RenderedImage ri = new RenderedImage(exit);
				ri.scale().set(grid.rasterWidth(), grid.rasterHeight());
				ri.position().set(grid.vectorAt(p));
				ri.index(-1);
				grid.attach(ri);
				active = true;
			}

			items.remove(p);
		}

		// Lookup older explosion
		RenderedAnimation renderedAnimation = explosions.get(p);

		if (renderedAnimation != null) {
			renderedAnimation.animation().reset();
			return true;
		}

		// Create new boom animation
		renderedAnimation = new RenderedAnimation() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onDetached() {
				super.onDetached();
				// Remove the explosion
				explosions.remove(p);
			}
		};
		explosions.put(p, renderedAnimation);
		renderedAnimation.animationBundle.add(sprite.newAnimationFromRange(
				"boom", 33, 0, 0, 25));
		renderedAnimation.animationName("boom").loop(false).paused(false);

		// Set index to 10
		renderedAnimation.index(10);

		// Adjust position and scale
		renderedAnimation.scale().set(grid.rasterWidth(), grid.rasterHeight());
		renderedAnimation.position().set(grid.vectorAt(x, y));

		// Detach if finished

		renderedAnimation.attach(new Entity() {

			/**
				 * 
				 */
			private static final long serialVersionUID = 1L;

			/*
			 * (non-Javadoc)
			 * 
			 * @see propra2012.gruppe33.engine.graphics.rendering.scenegraph.
			 * Entity#onEvent(java.lang.Object, java.lang.Object[])
			 */
			@Override
			protected void onEvent(Object event, Object... params) {
				super.onEvent(event, params);
				if (event instanceof AnimationEvent) {
					switch ((AnimationEvent) event) {
					case LastImage:
						((RenderedAnimation) params[0]).detach();
						break;
					}
				}
			}

		});

		grid.attach(renderedAnimation);
		return true;
	}


	public static GraphicsEntity createPlayer(String playerName,
			final Grid grid, int sx, int sy) throws Exception {
		// Create a new local player
		GraphicsEntity player = EntityRoutines.createFieldEntity(grid, sx, sy);

		player.attach(new Entity() {

			@Override
			protected void onUpdate(float tpf) {
				super.onUpdate(tpf);

				GraphicsEntity p = (GraphicsEntity) parent();
				if (grid.worldToNearestPoint(p.position()).equals(randomExit)
						&& active) {
					active = false;
					randomExit = null;
					exit = null;
					items.clear();
					explosions.clear();
					SceneProcessor s = grid.processor();
					try {
						s.root(PreMilestoneApp.createDemoGame());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});

		// Set player name
		player.name(playerName);

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
		field.scale().set(grid.rasterWidth(), grid.rasterHeight());

		// Set position
		field.position().set(grid.vectorAt(sx, sy));

		return field;
	}

	// Should not be instantiated
	private EntityRoutines() {
	}
}

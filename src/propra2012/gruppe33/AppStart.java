package propra2012.gruppe33;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import propra2012.gruppe33.graphics.rendering.JGridRenderer;
import propra2012.gruppe33.graphics.rendering.scenegraph.AnimationController;
import propra2012.gruppe33.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.graphics.rendering.scenegraph.EntityController;
import propra2012.gruppe33.graphics.rendering.scenegraph.Vector2f;
import propra2012.gruppe33.graphics.rendering.scenegraph.grid.Grid;
import propra2012.gruppe33.graphics.rendering.scenegraph.grid.GridController;
import propra2012.gruppe33.graphics.rendering.scenegraph.grid.Grid.Direction;
import propra2012.gruppe33.graphics.sprite.AnimationMap;
import propra2012.gruppe33.graphics.sprite.Sprite;

/**
 * 
 * @author Christopher Probst
 */
public class AppStart {

	public static AnimationMap createKnight(long speed) throws IOException {
		// Load the knight sprite
		Sprite knightSprite = new Sprite(
				ImageIO.read(AppStart.class
						.getResourceAsStream("resources/images/animated/chars/knight.png")),
				9, 9);

		// Create a new map
		AnimationMap knight = new AnimationMap();

		/*
		 * Load the die animation.
		 */
		knight.addAnimation(knightSprite.newAnimationFromRange("die_north",
				speed, 5, 4, 9));
		knight.addAnimation(knightSprite.newAnimationFromRange("die_south",
				speed, 5, 5, 9));
		knight.addAnimation(knightSprite.newAnimationFromRange("die_west",
				speed, 5, 6, 9));
		knight.addAnimation(knightSprite.newAnimationFromRange("die_east",
				speed, 5, 3, 9));

		/*
		 * Load the run animation.
		 */
		knight.addAnimation(knightSprite.newAnimationFromRange("run_north",
				speed, 8, 0, 8));
		knight.addAnimation(knightSprite.newAnimationFromRange("run_south",
				speed, 7, 1, 8));
		knight.addAnimation(knightSprite.newAnimationFromRange("run_west",
				speed, 6, 2, 8));
		knight.addAnimation(knightSprite.newAnimationFromRange("run_east",
				speed, 0, 0, 8));

		return knight;
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		// Create grid
		final Grid grid = new Grid("standard", 2048, 2048,
				Grid.loadGrid("smallmap.txt"));

		// Render solid block
		BufferedImage s = ImageIO.read(AppStart.class
				.getResourceAsStream("resources/images/solid.png"));

		Map<Character, BufferedImage> map = new HashMap<Character, BufferedImage>();
		map.put('1', s);

		// Bundle render to picture
		Entity solid = grid.bundleAndRender("solid", map);

		Entity player = new Entity("test player");

		// Load knight from harddrive
		final AnimationController animation = new AnimationController(
				createKnight(33));

		// Attach animation
		player.putController(animation);

		// The animation swapper
		player.putController(new EntityController() {

			private Direction last = null;

			@Override
			public void doUpdate(Entity entity, float tpf) {

				GridController grid = entity
						.getController(GridController.class);

				AnimationController ani = entity
						.getController(AnimationController.class);

				if (last != grid.getDirection()) {
					last = grid.getDirection();
					ani.setAnimation("run_" + last.toString().toLowerCase());
				}

				ani.getAnimationMap().get(ani.getAnimation())
						.setPaused(!grid.isMoving());
			}

			@Override
			public void doRender(Entity entity, Graphics2D original,
					Graphics2D transformed) {
			}
		});

		player.putController(new GridController(new Vector2f(500, 500)));
		player.getScale().set(grid.getRasterWidth() * 1.5f,
				grid.getRasterHeight() * 1.5f);
		player.getPosition().set(grid.vectorAt(1, 3));
		player.getPosition().x += 150;

		// Create new level renderer
		final JGridRenderer renderer = new JGridRenderer(grid);

		// Attach solid blocks
		renderer.getRoot().attach(solid);

		// Attach child
		renderer.getRoot().attach(player);

		// Start rendering
		renderer.start();

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(renderer);
		frame.setSize(500, 500);
		frame.setVisible(true);
	}
}

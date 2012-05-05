package propra2012.gruppe33;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import propra2012.gruppe33.graphics.rendering.JGridRenderer;
import propra2012.gruppe33.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.graphics.rendering.scenegraph.Vector2f;
import propra2012.gruppe33.graphics.rendering.scenegraph.grid.Grid;
import propra2012.gruppe33.graphics.rendering.scenegraph.grid.Grid.Direction;
import propra2012.gruppe33.graphics.rendering.scenegraph.grid.GridController;

/**
 * 
 * @author Christopher Probst
 */
public class AppStart {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		// Create grid
		final Grid grid = new Grid("standard", 2048, 2048,
				Grid.loadGrid("C:/map.txt"));

		Map<Character, BufferedImage> map = new HashMap<Character, BufferedImage>();
		map.put('1', ImageIO.read(new File("C:/box.png")));

		// Bundle render to picture
		Entity solid = grid.bundleAndRender("solid", map);

		Entity player = new Entity("test player") {

			@Override
			public void doRender(Entity entity, Graphics2D original,
					Graphics2D transformed) {

				transformed.setColor(Color.BLUE);
				transformed.translate(-0.5, -0.5);
				transformed.fillRect(0, 0, 1, 1);
				transformed.translate(0.5, 0.5);
				transformed.setColor(Color.black);

				Direction d = entity.getController(GridController.class)
						.getDirection();

				switch (d) {
				case North:
					transformed.scale(0.01f, 1);
					transformed.drawLine(0, 0, 0, -1);
					break;
				case South:
					transformed.scale(0.01f, 1);
					transformed.drawLine(0, 0, 0, 1);
					break;
				case West:
					transformed.scale(1, 0.01f);
					transformed.drawLine(0, 0, -1, 0);
					break;
				case East:
					transformed.scale(1, 0.01f);
					transformed.drawLine(0, 0, 1, 0);
					break;
				}
			}
		};

		player.putController(new GridController(new Vector2f(500, 500)));
		player.getScale().set(grid.getRasterWidth(), grid.getRasterHeight());
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

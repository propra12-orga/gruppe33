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
import propra2012.gruppe33.graphics.rendering.scenegraph.grid.Grid;
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

		//
		// for (int i = 0; i < 15; i++) {
		// for (int j = 0; j < 15; j++) {
		//
		// AnimatedEntity e = new AnimatedEntity("first obj" + i + " " + j) {
		//
		// @Override
		// public void doRender(Graphics2D g) {
		// // g.setStroke(new BasicStroke(4));
		// g.setColor(Color.black);
		// g.drawArc(-10, -10, 20, 20, 0, 360);
		// g.drawLine(0, 0, 0, 10);
		//
		// }
		// };
		// e.getPosition().set(10 + 20 * i, 10 + 20 * j);
		//
		// e.setAngularVelocity((float) (Math.PI * 2));
		//
		// renderer.getScene().attach(e);
		// }
		// }

		// Create grid
		final Grid grid = new Grid("standard", 2048, 2048,
				Grid.loadGrid("C:/map.txt"));

		// Render solid blocks to image
		// BufferedImage solidBlocks = grid.renderSolidBlocks(
		// ImageIO.read(new File("C:/box.png")), '1');

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
			}
		};

		player.putController(new GridController(1, 1));
		player.getScale().set(grid.getRasterWidth(), grid.getRasterHeight());
		player.getPosition().set(
				grid.vectorAt(player.getController(GridController.class)
						.getLocation()));

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

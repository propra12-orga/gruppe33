package propra2012.gruppe33;

import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import propra2012.gruppe33.graphics.rendering.JGridRenderer;
import propra2012.gruppe33.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.graphics.rendering.scenegraph.entities.ImageEntity;
import propra2012.gruppe33.graphics.rendering.scenegraph.scenes.Grid;

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
		Grid grid = new Grid("standard", 1000, 600, Grid.loadGrid("C:/map.txt"));

		// Render solid blocks to image
		// BufferedImage solidBlocks = grid.renderSolidBlocks(
		// ImageIO.read(new File("C:/box.png")), '1');

		Map<Character, BufferedImage> map = new HashMap<Character, BufferedImage>();
		map.put('1', ImageIO.read(new File("C:/box.png")));

		Entity solid = grid.bundle("solid", map);

		ImageEntity ie = new ImageEntity("solid_prerendered", grid.getWidth(),
				grid.getHeight(), Transparency.BITMASK);

		ie.getScale().set(grid.getWidth(), grid.getHeight());
		ie.getPosition().set(grid.getWidth() / 2, grid.getHeight() / 2);

		solid.render(ie.getImage());

		// Player p = new Player("test2");

		// Sprite spriteTest = new Sprite(ImageIO.read(new
		// File("C:/sprite.png")),
		// 6, 5);

		// p.getAnimations().addAnimation(
		// spriteTest.newAnimationFromRange("running", 10, 0, 0, 6 * 5));
		// p.getScale().scaleLocal(0.7f);
		// p.setActiveAnimation("running");

		// Create new level renderer
		final JGridRenderer renderer = new JGridRenderer(grid);

		// Attach solid blocks
		renderer.getRoot().attach(solid);
		// renderer.getRoot().attach(p);

		renderer.start();

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(renderer);
		frame.setSize(500, 500);
		frame.setVisible(true);
	}
}

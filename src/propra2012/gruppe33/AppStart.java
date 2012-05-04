package propra2012.gruppe33;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import propra2012.gruppe33.graphics.rendering.JLevelRenderer;
import propra2012.gruppe33.graphics.rendering.level.Level;
import propra2012.gruppe33.graphics.rendering.scenegraph.Vector2f;
import propra2012.gruppe33.graphics.rendering.scenegraph.entities.ImageEntity;
import propra2012.gruppe33.graphics.rendering.scenegraph.entities.Player;
import propra2012.gruppe33.graphics.sprite.Sprite;

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

		// Create level
		Level level = new Level("standard", 1000, 600,
				Level.loadMap("C:/map.txt"));

	

		// Render solid blocks to image
		BufferedImage solidBlocks = level.renderSolidBlocks(
				ImageIO.read(new File("C:/box.png")), '1');

		Player p = new Player("test2", level);
		p.getTarget().setLocation(5, 5);
//		Sprite spriteTest = new Sprite(ImageIO.read(new File("C:/sprite.png")),
//				6, 5);

//		p.getAnimations().addAnimation(
//				spriteTest.newAnimationFromRange("running", 10, 0, 0, 6 * 5));
		//p.getScale().scaleLocal(0.7f);
//		p.setActiveAnimation("running");

		// Create new level renderer
		final JLevelRenderer renderer = new JLevelRenderer(level);

		// Attach solid blocks
		renderer.getScene().attach(new ImageEntity("solid", solidBlocks));
		renderer.getScene().attach(p);

		renderer.start();

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(renderer);
		frame.setSize(500, 500);
		frame.setVisible(true);
	}

}

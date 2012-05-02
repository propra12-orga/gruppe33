package propra2012.gruppe33;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import propra2012.gruppe33.graphics.rendering.JRenderer;
import propra2012.gruppe33.graphics.rendering.scenegraph.entities.SolidBlocks;

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

		final JRenderer renderer = new JRenderer(1000, 1000);
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

		char[][] map = new char[][] {

				{ '1', '1', '1', '1', '1', '1', '1' },
				{ '1', '0', '0', '0', '0', '0', '1' },
				{ '1', '0', '1', '0', '1', '0', '1' },
				{ '1', '0', '0', '0', '0', '0', '1' },
				{ '1', '0', '1', '0', '1', '0', '1' },
				{ '1', '0', '0', '0', '0', '0', '1' },
				{ '1', '1', '1', '1', '1', '1', '1' }};

		SolidBlocks sb = new SolidBlocks("test", ImageIO.read(new File(
				"C:/box.png")), map, 1000/7, 1000/7);

		renderer.getScene().attach(sb);

		renderer.start();

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(renderer);
		frame.setSize(500, 500);
		frame.setVisible(true);
	}

}

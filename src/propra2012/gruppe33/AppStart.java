package propra2012.gruppe33;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import propra2012.gruppe33.graphics.gui.JSceneGraph;
import propra2012.gruppe33.graphics.scenegraph.AnimatedEntity;

/**
 * 
 * @author Christopher Probst
 */
public class AppStart {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		JFrame frame = new JFrame("JSceneGraph test");
		frame.setSize(700, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final JSceneGraph graph = new JSceneGraph(1024, 1024);

		frame.getContentPane().add(graph);

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < 10; i++) {
					for (int j = 0; j < 10; j++) {

						AnimatedEntity e = new AnimatedEntity("first obj" + i
								+ " " + j) {

							@Override
							public void render(Graphics2D g) {
								g.setStroke(new BasicStroke(4));
								g.drawArc(-50, -50, 100, 100, 0, 360);
								g.drawLine(0, 0, 0, 50);

							}
						};
						e.getPosition().set(50 + 100 * i, 50 + 100 * j);

						e.setRotationVelocity((float) (Math.PI * 2));

						graph.getSceneGraph().getLayer("first").addEntity(e);
					}
				}

			}
		});

		frame.setVisible(true);
	}

}

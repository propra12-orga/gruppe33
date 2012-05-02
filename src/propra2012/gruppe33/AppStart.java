package propra2012.gruppe33;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import propra2012.gruppe33.graphics.scenegraph.AnimatedEntity;
import propra2012.gruppe33.graphics.scenegraph.Entity;

/**
 * 
 * @author Christopher Probst
 */
public class AppStart {

	static final ScheduledExecutorService eee = Executors.newSingleThreadScheduledExecutor();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		final List<Entity> k = new LinkedList<Entity>();

		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 20; j++) {

				AnimatedEntity e = new AnimatedEntity("first obj" + i + " " + j) {

					@Override
					public void render(Graphics2D g) {
						// g.setStroke(new BasicStroke(4));
						g.drawArc(-5, -5, 10, 10, 0, 360);
						g.drawLine(0, 0, 0, 5);

					}
				};
				e.getPosition().set(5 + 10 * i, 5 + 10 * j);

				e.setRotationVelocity((float) (Math.PI * 2));

				k.add(e);
			}
		}

		final JPanel p = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				for (Entity et : k) {

					et.update(0.1f);
					Graphics2D c = (Graphics2D) g.create();

					
					c.translate(et.getPosition().x, et.getPosition().y);

					c.rotate(et.getRotation());
					et.render(c);
					c.dispose();
				}
			}
		};

		final JFrame frame = new JFrame("JSceneGraph test");
		
		frame.getContentPane().add(p);
		
		// Update every 33 ms
	
		eee.scheduleAtFixedRate(
				new Runnable() {

					@Override
					public void run() {

						// Add repaint request
						p.repaint();
						
					}
				}, 0, 33, TimeUnit.MILLISECONDS);

		frame.setSize(700, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setVisible(true);

		// JFrame frame = new JFrame("JSceneGraph test");
		// frame.setSize(700, 700);
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//
		// final JSceneGraph graph = new JSceneGraph(1024, 1024);
		//
		// frame.getContentPane().add(graph);
		//
		// SwingUtilities.invokeLater(new Runnable() {
		//
		// @Override
		// public void run() {
		// for (int i = 0; i < 10; i++) {
		// for (int j = 0; j < 10; j++) {
		//
		// AnimatedEntity e = new AnimatedEntity("first obj" + i
		// + " " + j) {
		//
		// @Override
		// public void render(Graphics2D g) {
		// g.setStroke(new BasicStroke(4));
		// g.drawArc(-50, -50, 100, 100, 0, 360);
		// g.drawLine(0, 0, 0, 50);
		//
		// }
		// };
		// e.getPosition().set(50 + 100 * i, 50 + 100 * j);
		//
		// e.setRotationVelocity((float) (Math.PI * 2));
		//
		// graph.getSceneGraph().getLayer("first").addEntity(e);
		// }
		// }
		//
		// }
		// });
		//
		// frame.setVisible(true);
	}

}

package propra2012.gruppe33;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

import javax.swing.JFrame;

import propra2012.gruppe33.graphics.gui.JSceneGraph;
import propra2012.gruppe33.graphics.scenegraph.AnimatedEntity;
import propra2012.gruppe33.graphics.scenegraph.Entity;

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

		JSceneGraph graph = new JSceneGraph(500, 500);

		frame.getContentPane().add(graph);

		AnimatedEntity e = new AnimatedEntity("first obj") {

			@Override
			public void render(Graphics2D g) {
				g.setStroke(new BasicStroke(4));
				g.drawArc(0, 0, 100, 100, 0, 360);
			}
		};
		e.getPosition().set(300, 300);
		e.getAcceleration().x = 0.01f;

		graph.getSceneGraph().getLayer("first").addEntity(e);

		frame.setVisible(true);
	}

}

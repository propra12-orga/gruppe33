package propra2012.gruppe33.graphics.sprite;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 * Just my little Playground... You dont need to understand what happens
 * here....
 * 
 * @author Matthias Hesse
 * 
 */
public class Playground extends JFrame {

	private AnimationMap am = new AnimationMap();
	private Sprite spriteTest;

	private Timer timer = new Timer();

	public Playground() throws Exception {

		spriteTest = new Sprite(ImageIO.read(new File("C:/sprite.png")), 6, 5);

		am.addAnimation(spriteTest.newAnimationFromRange("running", 33, 0, 0,
				6 * 5));

		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				repaint();
			}
		}, 0, 33);

		setVisible(true);
	}

	public void paint(Graphics g) {

		super.paint(g);

		g.drawImage(am.getImage("running"), 25, 25, null);

	}

	public static void main(String[] args) throws Exception {
		Playground pg = new Playground();
	}
}
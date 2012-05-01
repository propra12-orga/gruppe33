package propra2012.gruppe33.graphics.sprite;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Playground extends JFrame {
	
	private BufferedImage im;
	private SpriteImpl spriteTest;
	
	private Timer timer = new Timer();
	

	public Playground() throws Exception {

		setVisible(true);
		spriteTest = new SpriteImpl();
		spriteTest.setImage(ImageIO.read(new File("C:/male.png")), 4, 4);
		
		spriteTest.addAnimation("test", 33, new Point(0,0),new Point(1,0),new Point(2,0),new Point(3,0));
		
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				repaint();
			}
		}, 0, 33);

	}

	public void paint(Graphics g) {

		super.paint(g);

		g.drawImage(spriteTest.getImage("test"), 25, 25, null);
	}


	
	public static void main(String[] args) throws Exception{
		Playground pg = new Playground();
		
		
		
		
	}
}
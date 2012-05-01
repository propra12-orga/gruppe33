package propra2012.gruppe33.graphics.sprite;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class SpriteImpl implements Sprite {

	private Map<String, Animation> animations = new HashMap<String, Animation>();
	private BufferedImage image;
	// private double sizeXPlate,sizeYPlate;
	private int rasterX, rasterY;

	public void setImage(BufferedImage image, int rasterX, int rasterY) {
		this.image = image;
		this.rasterX = rasterX;
		this.rasterY = rasterY;
		// sizeXPlate = image.getWidth()/rasterX;
		// sizeYPlate = image.getHeight()/rasterY;

		// BufferedImage bi = new BufferedImage(width, height,
		// BufferedImage.TYPE_INT_RGB)
	}

	public static void main(String[] args) throws Exception {

		BufferedImage bi = ImageIO.read(new File("C:/male.png"));
		SpriteImpl spriteTest = new SpriteImpl();
		spriteTest.addAnimation("test", 30, new Point(0,0),new Point(1,0),new Point(2,0),new Point(3,0));
		
		spriteTest.getImage("test");
		
		
	}

	public static BufferedImage getSubImage(BufferedImage image, int rasterX,
			int rasterY, int x, int y) {
		int sizeXPlate = image.getWidth() / rasterX;
		int sizeYPlate = image.getHeight() / rasterY;

		BufferedImage subImage = new BufferedImage(sizeXPlate, sizeYPlate,
				BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2 = subImage.createGraphics();
		g2.drawImage(image, 0, 0, sizeXPlate, sizeYPlate, x * sizeXPlate, y
				* sizeYPlate, sizeXPlate, sizeYPlate, null);
		g2.dispose();

		return subImage;
	}

	public void addAnimation(String name, long time, Point... points) {
		Animation newAnimation = new Animation(name, time, points);
		animations.put(name, newAnimation);

	}

	@Override
	public BufferedImage getImage(String name) {
		Animation animation = animations.get(name);

		return null;
	}

}

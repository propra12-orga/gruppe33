package propra2012.gruppe33.graphics.sprite;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import javax.imageio.ImageIO;

public class SpriteImpl implements Sprite {

	private Map<String, Animation> animations = new HashMap<String, Animation>();
	private List<BufferedImage> imageList = new ArrayList<BufferedImage>();
	private BufferedImage image;
	// private double sizeXPlate,sizeYPlate;
	private int rasterX, rasterY;
	

	

	public void setImage(BufferedImage image, int rasterX, int rasterY) {
		this.image = image;
		this.rasterX = rasterX;
		this.rasterY = rasterY;
	}


	
	

	public  BufferedImage getSubImage(BufferedImage image, int rasterX,
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
		List<BufferedImage> animationImages = new ArrayList<BufferedImage>();
		for(int i = 0; i < points.length; i++){
			animationImages.add(getSubImage(image, rasterX, rasterY, points[i].x, points[i].y));
		}
		
		Animation newAnimation = new Animation(name, animationImages,time, points);
		
		
		animations.put(name, newAnimation);

	}

	@Override
	public BufferedImage getImage(String name) {
		Animation animation = animations.get(name);

		return animation.getAnimationImage();
	}

}

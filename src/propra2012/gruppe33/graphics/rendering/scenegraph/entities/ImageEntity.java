package propra2012.gruppe33.graphics.rendering.scenegraph.entities;

import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

import propra2012.gruppe33.graphics.rendering.scenegraph.Entity;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class ImageEntity extends Entity {

	public static BufferedImage createImage(int width, int height,
			int transparency) {
		return GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getDefaultConfiguration()
				.createCompatibleImage(width, height, transparency);
	}

	// Used for rendering
	private BufferedImage image;

	public ImageEntity(String id) {
		this(id, null);
	}

	public ImageEntity(String id, int width, int height, int transparency) {
		this(id, createImage(width, height, transparency));
	}

	public ImageEntity(String id, BufferedImage image) {
		super(id);
		setImage(image);
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	@Override
	protected void doRender(Graphics2D original, Graphics2D transformed) {
		super.doRender(original, transformed);

		if (image != null) {
			Graphics2D c = (Graphics2D) transformed.create();
			try {
				c.translate(-0.5, -0.5);
				c.drawImage(image, 0, 0, 1, 1, null);
			} finally {
				c.dispose();
			}
		}
	}
}

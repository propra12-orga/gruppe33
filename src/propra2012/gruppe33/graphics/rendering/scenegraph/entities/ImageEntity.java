package propra2012.gruppe33.graphics.rendering.scenegraph.entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import propra2012.gruppe33.graphics.rendering.scenegraph.Entity;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class ImageEntity extends Entity {

	// Used for rendering
	private BufferedImage image;

	public ImageEntity(String id) {
		this(id, null);
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
	protected void doRender(Graphics2D g) {
		super.doRender(g);

		if (image != null) {
			g.drawImage(image, 0, 0, null);
		}
	}
}

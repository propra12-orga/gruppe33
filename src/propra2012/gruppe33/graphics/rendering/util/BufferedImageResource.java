package propra2012.gruppe33.graphics.rendering.util;

import java.awt.image.BufferedImage;
import java.io.IOException;

import propra2012.gruppe33.graphics.GraphicsRoutines;

/**
 * This class represents a serializable buffered image.
 * 
 * @author Christopher Probst
 * @see Resource
 */
public final class BufferedImageResource implements Resource<BufferedImage> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The buffered image
	private transient BufferedImage image;

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {

		// Write default vars
		out.defaultWriteObject();

		// Write some image information
		out.writeInt(image.getWidth());
		out.writeInt(image.getHeight());
		out.writeInt(image.getTransparency());

		// Write all rgb values
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				out.writeInt(image.getRGB(x, y));
			}
		}
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {

		// Read default vars
		in.defaultReadObject();

		// Ready the image data
		image = GraphicsRoutines.createImage(in.readInt(), in.readInt(),
				in.readInt());

		// Read all rgb values
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				image.setRGB(x, y, in.readInt());
			}
		}
	}

	/**
	 * Creates a new default image resource.
	 * 
	 * @param image
	 *            The image you want to use.
	 */
	public BufferedImageResource(BufferedImage image) {
		if (image == null) {
			throw new NullPointerException("image");
		}
		this.image = image;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see propra2012.gruppe33.graphics.rendering.util.Resource#get()
	 */
	@Override
	public BufferedImage get() {
		return image;
	}
}

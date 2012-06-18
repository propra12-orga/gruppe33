package com.indyforge.twod.engine.resources;

import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.indyforge.twod.engine.graphics.GraphicsRoutines;
import com.indyforge.twod.engine.resources.assets.AssetManager;

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

		if (!AssetManager.isHeadless()) {
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
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {

		// Read default vars
		in.defaultReadObject();

		if (!AssetManager.isHeadless()) {
			// Ready the image data
			image = GraphicsRoutines.createImage(in.readInt(), in.readInt(),
					in.readInt());

			// Read all rgb values
			for (int x = 0; x < image.getWidth(); x++) {
				for (int y = 0; y < image.getHeight(); y++) {
					image.setRGB(x, y, in.readInt());
				}
			}
		} else {
			image = null;
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
	 * @see com.indyforge.twod.engine.resources.Resource#get()
	 */
	@Override
	public BufferedImage get() {
		if (AssetManager.isHeadless()) {
			throw new HeadlessException();
		}
		return image;
	}
}

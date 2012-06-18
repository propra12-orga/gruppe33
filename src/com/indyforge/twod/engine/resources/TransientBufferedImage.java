package com.indyforge.twod.engine.resources;

import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.indyforge.twod.engine.graphics.GraphicsRoutines;
import com.indyforge.twod.engine.resources.assets.AssetManager;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class TransientBufferedImage implements Resource<BufferedImage> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The image dimension and the transparency
	private final int width, height, transparency;

	// The buffered image
	private transient BufferedImage image;

	private void createResource() {
		if (!AssetManager.isHeadless()) {
			// Create new image
			image = GraphicsRoutines.createImage(width, height, transparency);
		} else {
			image = null;
		}
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {

		// Read default vars
		in.defaultReadObject();

		// Create the resource
		createResource();
	}

	public TransientBufferedImage(int width, int height, int transparency) {
		if (width <= 0) {
			throw new IllegalArgumentException("Width must be > 0");
		} else if (height <= 0) {
			throw new IllegalArgumentException("Height must be > 0");
		}
		// Save parameters
		this.width = width;
		this.height = height;
		this.transparency = transparency;

		// Create the resource
		createResource();
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}

	public int transparency() {
		return transparency;
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

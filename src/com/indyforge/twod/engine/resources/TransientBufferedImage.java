package com.indyforge.twod.engine.resources;

import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.indyforge.twod.engine.graphics.GraphicsRoutines;
import com.indyforge.twod.engine.graphics.ImageDesc;
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

	// The image description
	private final ImageDesc imageDesc;

	// The buffered image
	private transient BufferedImage image;

	private void createResource() {
		// Create resource
		image = !AssetManager.isHeadless() ? GraphicsRoutines
				.createImage(imageDesc) : null;
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {

		// Read default vars
		in.defaultReadObject();

		// Create the resource
		createResource();
	}

	public TransientBufferedImage(ImageDesc imageDesc) {
		if (imageDesc == null) {
			throw new NullPointerException("imageDesc");
		}
		// Save parameters
		this.imageDesc = imageDesc;

		// Create the resource
		createResource();
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

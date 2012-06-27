package com.indyforge.twod.engine.resources;

import java.awt.Font;
import java.awt.HeadlessException;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.indyforge.twod.engine.resources.assets.AssetManager;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class TransientSystemFontResource implements Resource<Font> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The name of the font
	private final String name;

	// The style and size of the font
	private final int style, size;

	// The font
	private transient Font font;

	/**
	 * Create the font.
	 */
	private void createResource() {

		// Create font based on the headless state...
		font = !AssetManager.isHeadless() ? new Font(name, style, size) : null;
	}

	/*
	 * When deserializing we want to recreate the resource.
	 */
	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {

		// Restore all vars
		in.defaultReadObject();

		// Recreate the resource
		createResource();
	}

	/**
	 * Creates a new transient system font resource using the given parameters.
	 * 
	 * @param name
	 *            The name of the system font.
	 * @param style
	 *            The style of the font.
	 * @param size
	 *            The size of the font.
	 */
	public TransientSystemFontResource(String name, int style, int size) {
		this.name = name;
		this.style = style;
		this.size = size;
		createResource();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.twod.engine.resources.Resource#get()
	 */
	@Override
	public Font get() {
		if (AssetManager.isHeadless()) {
			throw new HeadlessException();
		}
		return font;
	}
}

package com.indyforge.twod.engine.resources;

import java.awt.Font;
import java.awt.HeadlessException;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.indyforge.twod.engine.resources.assets.AssetManager;

/**
 * 
 * Represents a transient derived font resource.
 * 
 * @author Christopher Probst
 * 
 */
public final class TransientDerivedFontResource implements Resource<Font> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The base font
	private final Resource<? extends Font> baseFont;

	// The style of the derived font
	private final int style;

	// The size of the derived font
	private final float size;

	// The derived font
	private transient Font derivedFont;

	/**
	 * Create the font.
	 */
	private void createResource() {

		// Create font based on the headless state...
		derivedFont = !AssetManager.isHeadless() ? baseFont.get().deriveFont(
				style, size) : null;
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

	public TransientDerivedFontResource(Resource<? extends Font> baseFont,
			int style, float size) {
		if (baseFont == null) {
			throw new NullPointerException("baseFont");
		}

		this.baseFont = baseFont;
		this.style = style;
		this.size = size;

		// Create the resource
		createResource();
	}

	public Resource<? extends Font> baseFont() {
		return baseFont;
	}

	public float sze() {
		return size;
	}

	public int style() {
		return style;
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
		return derivedFont;
	}
}

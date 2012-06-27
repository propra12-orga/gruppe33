package com.indyforge.twod.engine.graphics;

import java.io.Serializable;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class ImageDesc implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * The image dimension values.
	 */
	private int width, height, transparency;

	/**
	 * @return the width.
	 */
	public int width() {
		return width;
	}

	/**
	 * @param width
	 *            The width to set.
	 * @return this for chaining.
	 */
	public ImageDesc width(int width) {
		if (width <= 0) {
			throw new IllegalArgumentException("Width must be > 0");
		}
		this.width = width;
		return this;
	}

	/**
	 * @return the height.
	 */
	public int height() {
		return height;
	}

	/**
	 * @param height
	 *            The height to set.
	 * @return this for chaining.
	 */
	public ImageDesc height(int height) {
		if (height <= 0) {
			throw new IllegalArgumentException("Height must be > 0");
		}
		this.height = height;
		return this;
	}

	/**
	 * @return the transparency.
	 */
	public int transparency() {
		return transparency;
	}

	/**
	 * @param transparency
	 *            The transparency to set.
	 * @return this for chaining.
	 */
	public ImageDesc transparency(int transparency) {
		this.transparency = transparency;
		return this;
	}
}

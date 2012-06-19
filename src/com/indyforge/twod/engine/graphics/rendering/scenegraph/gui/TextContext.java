package com.indyforge.twod.engine.graphics.rendering.scenegraph.gui;

import java.awt.Font;
import java.io.Serializable;

import com.indyforge.twod.engine.resources.Resource;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class TextContext implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * The image dimension values.
	 */
	private int width, height, transparency;

	/*
	 * The text font resource.
	 */
	private Resource<? extends Font> fontResource;

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
	public TextContext width(int width) {
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
	public TextContext height(int height) {
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
	public TextContext transparency(int transparency) {
		this.transparency = transparency;
		return this;
	}

	/**
	 * @return the fontResource.
	 */
	public Resource<? extends Font> fontResource() {
		return fontResource;
	}

	/**
	 * @param fontResource
	 *            The font resource to set.
	 * @return this for chaining.
	 */
	public TextContext fontResource(Resource<? extends Font> fontResource) {
		this.fontResource = fontResource;
		return this;
	}
}

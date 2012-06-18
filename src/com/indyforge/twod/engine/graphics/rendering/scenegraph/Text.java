package com.indyforge.twod.engine.graphics.rendering.scenegraph;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Transparency;

import com.indyforge.twod.engine.graphics.GraphicsRoutines;
import com.indyforge.twod.engine.resources.Resource;
import com.indyforge.twod.engine.resources.TransientBufferedImage;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class Text extends RenderedImage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * The text.
	 */
	private String text = "";

	/*
	 * The text and clear color.
	 */
	private Color textColor = Color.BLACK, clearColor = new Color(0, 0, 0, 0);

	/*
	 * The font asset.
	 */
	private Resource<? extends Font> fontResource;

	/*
	 * Whether or not the text has changed.
	 */
	private boolean changed = false, manageImage = true;

	/*
	 * The transparency (Only used if manageImage = true).
	 */
	private int transparency = Transparency.TRANSLUCENT;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.RenderedImage
	 * #onRender(java.awt.Graphics2D, java.awt.Graphics2D)
	 */
	@Override
	protected void onRender(Graphics2D original, Graphics2D transformed) {

		// Has the text changed ??
		if (changed && fontResource != null & textColor != null
				&& (manageImage || imageResource() != null)) {

			/*
			 * Are we allowed to manage the image ?
			 */
			if (manageImage) {
				// Set font (To use the font metrics)
				transformed.setFont(fontResource.get());

				// Get the font metrics
				FontMetrics fm = transformed.getFontMetrics();

				// Create new image based on the text metrics
				TransientBufferedImage image = new TransientBufferedImage(
						fm.stringWidth(text), fm.getHeight(), transparency);

				// Overwrite the image and use ratio
				imageResource(image).useRatio();
			}

			// Draw centered text
			GraphicsRoutines.drawCenteredString(text, fontResource.get(),
					clearColor, textColor, imageResource().get());

			// Disable change flag
			changed = false;
		}

		super.onRender(original, transformed);
	}

	/**
	 * @return the transparency of the image (Only used if the text can manage
	 *         its image).
	 */
	public int transparency() {
		return transparency;
	}

	/**
	 * Sets the transparency of the image (Only used if the text can manage its
	 * image).
	 * 
	 * @param transparency
	 *            The transparency.
	 * @return this for chaining.
	 */
	public Text transparency(int transparency) {
		this.transparency = transparency;
		return this;
	}

	/**
	 * @return the text.
	 */
	public String text() {
		return text;
	}

	/**
	 * @return the font resource.
	 */
	public Resource<? extends Font> fontResource() {
		return fontResource;
	}

	/**
	 * Sets the font resource.
	 * 
	 * @param fontResource
	 *            The font resource.
	 * @return this for chaining.
	 */
	public Text fontResource(Resource<? extends Font> fontResource) {
		this.fontResource = fontResource;
		return this;
	}

	/**
	 * @return the text color.
	 */
	public Color textColor() {
		return textColor;
	}

	/**
	 * Sets the text color.
	 * 
	 * @param color
	 *            The text color.
	 * @return this for chaining.
	 */
	public Text textColor(Color color) {
		textColor = color;
		return this;
	}

	/**
	 * Sets the manage-image flag.
	 * 
	 * @param manageImage
	 *            The manage-image flag. (True = Image recreation + use ratio)
	 * @return this for chaining.
	 */
	public Text manageImage(boolean manageImage) {
		this.manageImage = manageImage;
		return this;
	}

	/**
	 * @return the manage-image flag.
	 */
	public boolean isManageImage() {
		return manageImage;
	}

	/**
	 * @return the clear color.
	 */
	public Color clearColor() {
		return clearColor;
	}

	/**
	 * Sets the text color.
	 * 
	 * @param color
	 *            The clear color.
	 * @return this for chaining.
	 */
	public Text clearColor(Color color) {
		clearColor = color;
		return this;
	}

	/**
	 * @return true if the rendered text is not updated yet, otherwise false.
	 */
	public boolean isChanged() {
		return changed;
	}

	/**
	 * Sets the text.
	 * 
	 * @param text
	 *            The text.
	 * @return this for chaining.
	 */
	public Text text(String text) {
		// If the new text is not null and is different
		if (text != null && !this.text.equals(text)) {

			// Save
			this.text = text;

			// Text has changed
			changed = true;
		}
		return this;
	}
}

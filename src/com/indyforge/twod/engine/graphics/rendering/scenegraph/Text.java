package com.indyforge.twod.engine.graphics.rendering.scenegraph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import com.indyforge.twod.engine.graphics.ImageDesc;
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

	public enum Alignment {
		Left, Center, Right
	}

	/*
	 * The text.
	 */
	private String text = "";

	/*
	 * The text, clear and border color.
	 */
	private Color textColor = Color.BLACK, clearColor = new Color(0, 0, 0, 0),
			borderColor = Color.LIGHT_GRAY;

	/*
	 * The font asset.
	 */
	private Resource<? extends Font> fontResource;

	/*
	 * Whether or not the text has changed.
	 */
	private boolean changed = false;

	/*
	 * The text alignment.
	 */
	private Alignment alignment = Alignment.Left;

	/*
	 * The border width.
	 */
	private float borderWidth = 0f;

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
		if (changed && fontResource != null && imageResource() != null) {

			// Get the image
			Image im = imageResource().get();

			// Get graphics 2d context
			Graphics2D graphics2d = (Graphics2D) im.getGraphics();
			try {

				graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				graphics2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BICUBIC);

				// Read dimension
				int w = im.getWidth(null), h = im.getHeight(null);

				// Clear the image
				graphics2d.setBackground(clearColor);
				graphics2d.clearRect(0, 0, w, h);

				// Set font
				graphics2d.setFont(fontResource.get());

				// Set the text color
				graphics2d.setColor(textColor);

				// Get the font metrics
				FontMetrics fm = graphics2d.getFontMetrics();

				// Do some text calculations
				float textWidth = fm.stringWidth(text), x, y = (fm.getAscent() + (h - (fm
						.getAscent() + fm.getDescent())) * 0.5f);

				// Use the alignment
				switch (alignment) {
				case Left:
					x = borderWidth;
					break;
				case Center:
					x = (w - textWidth) * 0.5f;
					break;
				case Right:
					x = w - textWidth - borderWidth;
					break;
				default:
					throw new IllegalStateException("Unknown alignment: "
							+ alignment);
				}

				// Offset needed for left alignment
				float off = 0;

				// Too much text ?
				if (textWidth > w && alignment == Alignment.Left) {
					off += textWidth - w;
				}

				// Draw the string
				graphics2d.drawString(text, x - off, y);

				// Draw border if necessary
				if (borderWidth > 0) {
					// Round the border width
					int bw = Math.round(borderWidth * 0.5f);

					graphics2d.setColor(borderColor);
					graphics2d.setStroke(new BasicStroke(borderWidth));
					graphics2d.drawRoundRect(bw, bw, w - bw * 2, h - bw * 2,
							h / 2, h / 2);
				}

				// Disable change flag
				changed = false;
			} finally {
				graphics2d.dispose();
			}
		}

		super.onRender(original, transformed);
	}

	/**
	 * Creates an empty text.
	 */
	public Text() {
		/*
		 * A text should always keep the ratio!
		 */
		keepRatio(true);
	}

	/**
	 * Creates a text.
	 * 
	 * @see Text#setup(int, int, int)
	 */
	public Text(ImageDesc imageDesc) {
		// Invoke default constructor!
		this();
		setup(imageDesc);
	}

	/**
	 * Creates an image resource using the given parameters.
	 * 
	 * @param imageDesc
	 *            The image description.
	 * @return this for chaining.
	 */
	public Text setup(ImageDesc imageDesc) {
		if (imageDesc == null) {
			throw new NullPointerException("imageDesc");
		}
		imageResource(new TransientBufferedImage(imageDesc));
		return this;
	}

	/**
	 * @return the text.
	 */
	public String text() {
		return text;
	}

	/**
	 * @return the border width.
	 */
	public float borderWidth() {
		return borderWidth;
	}

	/**
	 * Sets the border width.
	 * 
	 * @param borderWidth
	 *            The border width or 0 (no border!).
	 * @return this for chaining.
	 */
	public Text borderWidth(float borderWidth) {
		if (borderWidth < 0) {
			throw new IllegalArgumentException("Border width must be >= 0");
		} else if (borderWidth != this.borderWidth) {
			changed();
		}
		this.borderWidth = borderWidth;
		return this;
	}

	/**
	 * @return the text alignment.
	 */
	public Alignment alignment() {
		return alignment;
	}

	/**
	 * 
	 * 
	 * @param alignment
	 * @return
	 */
	public Text alignment(Alignment alignment) {
		if (alignment == null) {
			throw new NullPointerException("alignment");
		} else if (this.alignment != alignment) {
			changed();
		}
		this.alignment = alignment;
		return this;
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
		if (fontResource == null || !fontResource.equals(this.fontResource)) {
			changed();
		}
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
	 * @param textColor
	 *            The text color.
	 * @return this for chaining.
	 */
	public Text textColor(Color textColor) {
		if (textColor == null) {
			throw new NullPointerException("textColor");
		} else if (!textColor.equals(this.textColor)) {
			changed();
		}
		this.textColor = textColor;
		return this;
	}

	/**
	 * @return the clear color.
	 */
	public Color clearColor() {
		return clearColor;
	}

	/**
	 * Sets the clear color.
	 * 
	 * @param clearColor
	 *            The clear color.
	 * @return this for chaining.
	 */
	public Text clearColor(Color clearColor) {
		if (clearColor == null) {
			throw new NullPointerException("clearColor");
		} else if (!clearColor.equals(this.clearColor)) {
			changed();
		}
		this.clearColor = clearColor;
		return this;
	}

	/**
	 * @return the border color.
	 */
	public Color borderColor() {
		return borderColor;
	}

	/**
	 * Sets the border color.
	 * 
	 * @param borderColor
	 *            The border color.
	 * @return this for chaining.
	 */
	public Text borderColor(Color borderColor) {
		if (borderColor == null) {
			throw new NullPointerException("borderColor");
		} else if (!borderColor.equals(this.borderColor)) {
			changed();
		}
		this.borderColor = borderColor;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.RenderedImage
	 * #imageResource(com.indyforge.twod.engine.resources.Resource)
	 */
	@Override
	public RenderedImage imageResource(Resource<? extends Image> imageResource) {
		if (imageResource == null || !imageResource.equals(imageResource())) {
			changed();
		}
		return super.imageResource(imageResource);
	}

	/**
	 * @return true if the rendered text is not updated yet, otherwise false.
	 */
	public boolean isChanged() {
		return changed;
	}

	/**
	 * Requests a repaint.
	 * 
	 * @return this for chaining.
	 */
	public Text changed() {
		changed = true;
		return this;
	}

	/**
	 * Appends a backspace.
	 * 
	 * @return this for chaining.
	 */
	public Text backspace() {
		if (text.length() > 1) {
			text(text.substring(0, text.length() - 1));
		} else {
			text("");
		}
		return this;
	}

	/**
	 * Appends text.
	 * 
	 * @param string
	 *            The string.
	 * @return this for chaining.
	 */
	public Text append(String string) {
		if (string == null) {
			throw new NullPointerException("string");
		}
		return text(text + string);
	}

	/**
	 * Appends a char.
	 * 
	 * @param c
	 *            The char.
	 * @return this for chaining.
	 */
	public Text append(char c) {
		return text(text + c);
	}

	/**
	 * Sets the text.
	 * 
	 * @param text
	 *            The text.
	 * @return this for chaining.
	 */
	public Text text(String text) {
		if (text == null) {
			throw new NullPointerException("text");
		} else if (!this.text.equals(text)) {

			// Save
			this.text = text;

			// Text has changed
			changed();
		}
		return this;
	}
}

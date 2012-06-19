package com.indyforge.twod.engine.graphics.rendering.scenegraph.gui;

import java.awt.Font;

import com.indyforge.twod.engine.graphics.ImageDesc;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.RenderedImage;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Text;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Text.Alignment;
import com.indyforge.twod.engine.resources.Resource;

/**
 * A gui label implementation.
 * 
 * @author Christopher Probst
 */
public class Label extends GuiEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The default scale factor of the text (relative to the background image).
	 */
	public static final float TEXT_SCALE = 0.8f;

	/*
	 * The background image.
	 */
	private final RenderedImage background = new RenderedImage().centered(true)
			.keepRatio(true);

	/*
	 * The text component.
	 */
	private final Text text;

	public Label(ImageDesc imageDesc, Resource<? extends Font> fontResource) {

		// Labels can not be selected by default
		selectable(false);

		// Attach background and text
		attach(background,
				text = (Text) new Text(imageDesc).fontResource(fontResource)
						.centered(true));

		// Center a label by default
		text.alignment(Alignment.Center);

		// Apply text scale
		text.scale().scaleLocal(TEXT_SCALE);
	}

	/**
	 * @return the background image.
	 */
	public RenderedImage background() {
		return background;
	}

	/**
	 * @return the text.
	 */
	public Text text() {
		return text;
	}
}

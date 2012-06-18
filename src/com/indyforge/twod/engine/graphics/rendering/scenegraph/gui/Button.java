package com.indyforge.twod.engine.graphics.rendering.scenegraph.gui;

import java.awt.Font;
import java.awt.Image;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.RenderedImage;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Text;
import com.indyforge.twod.engine.resources.Resource;

/**
 * 
 * 
 * @author Christopher Probst
 */
public class Button extends GuiEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * The selected/deselected images.
	 */
	private final RenderedImage selected = new RenderedImage().centered(true),
			deselected = new RenderedImage().centered(true);

	/*
	 * The text component of the button.
	 */
	private final Text text = (Text) new Text().centered(true);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.gui.GuiEntity
	 * #onSelected
	 * (com.indyforge.twod.engine.graphics.rendering.scenegraph.gui.GuiEntity)
	 */
	@Override
	protected void onSelected(GuiEntity guiEntity) {
		super.onSelected(guiEntity);
		selected.visible(true);
		deselected.visible(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.gui.GuiEntity
	 * #onDeselected
	 * (com.indyforge.twod.engine.graphics.rendering.scenegraph.gui.GuiEntity)
	 */
	@Override
	protected void onDeselected(GuiEntity guiEntity) {
		super.onDeselected(guiEntity);
		selected.visible(false);
		deselected.visible(true);
	}

	/**
	 * Creates a button using the default values but without any images and
	 * without a text font.
	 */
	public Button() {
		this(null, null, null);
	}

	/**
	 * Creates a button using the default values and the given parameters.
	 * 
	 * @param selectedImageResource
	 *            The selected image.
	 * @param deselectedImageResource
	 *            The deselected image.
	 * @param fontResource
	 *            The font of the text.
	 */
	public Button(Resource<? extends Image> selectedImageResource,
			Resource<? extends Image> deselectedImageResource,
			Resource<? extends Font> fontResource) {
		selected.imageResource(selectedImageResource).useRatio();
		deselected.imageResource(deselectedImageResource).useRatio();
		attach(selected, deselected, text.fontResource(fontResource));
		text.scale().scaleLocal(0.8f);
		deselect();
	}

	/**
	 * @return the button text.
	 */
	public Text text() {
		return text;
	}

	/**
	 * @return selected image.
	 */
	public RenderedImage selectedImage() {
		return selected;
	}

	/**
	 * @return the deselected image.
	 */
	public RenderedImage deselectedImage() {
		return deselected;
	}
}

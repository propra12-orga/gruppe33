package com.indyforge.twod.engine.graphics.rendering.scenegraph.gui;

import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyEvent;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.RenderedImage;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
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

	public enum ButtonEvent {
		Pressed
	}

	/*
	 * The selected/deselected images.
	 */
	private final RenderedImage selected = new RenderedImage().centered(true),
			deselected = new RenderedImage().centered(true);

	/*
	 * The text component of the button.
	 */
	private final Text text;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.gui.GuiEntity
	 * #onUpdate(float)
	 */
	@Override
	protected void onUpdate(float tpf) {
		super.onUpdate(tpf);

		// Find the scene
		Scene scene = findScene();

		if (scene != null) {
			if (isSelected() && scene.isSinglePressed(KeyEvent.VK_ENTER)) {
				fireEvent(ButtonEvent.Pressed);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.gui.GuiListener
	 * #onEvent(com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity,
	 * java.lang.Object, java.lang.Object[])
	 */
	@Override
	protected void onEvent(Entity source, Object event, Object... params) {
		super.onEvent(source, event, params);

		// Verify the event
		if (event == ButtonEvent.Pressed && source == this) {
			onButtonPressed();
		}
	}

	/**
	 * Gets called when the button is pressed.
	 */
	protected void onButtonPressed() {
	}

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
		if (guiEntity == this) {
			selected.visible(true);
			deselected.visible(false);
		}
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
		if (guiEntity == this) {
			selected.visible(false);
			deselected.visible(true);
		}
	}

	/**
	 * Creates a button using the default values and the given parameters.
	 * 
	 * @param selectedImageResource
	 *            The selected image.
	 * @param deselectedImageResource
	 *            The deselected image.
	 * @param width
	 *            The width of the text label.
	 * @param height
	 *            The height of the text label.
	 * @param transparency
	 *            The transparency of the text label.
	 * @param fontResource
	 *            The font of the text.
	 */
	public Button(Resource<? extends Image> selectedImageResource,
			Resource<? extends Image> deselectedImageResource, int width,
			int height, int transparency, Resource<? extends Font> fontResource) {

		// Register the pressed event
		events().put(ButtonEvent.Pressed, iterableChildren(true, true));

		// Setup images
		selected.imageResource(selectedImageResource).useRatio();
		deselected.imageResource(deselectedImageResource).useRatio();

		// Create the text component
		text = (Text) new Text(width, height, transparency).fontResource(
				fontResource).centered(true);
		text.scale().scaleLocal(0.8f);

		// Attach all components
		attach(selected, deselected, text);

		// Start deselected
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

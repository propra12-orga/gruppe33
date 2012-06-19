package com.indyforge.twod.engine.graphics.rendering.scenegraph.gui;

import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyEvent;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.EntityRoutines;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Text.Alignment;
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
	private final Label selected, deselected;

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
			EntityRoutines.visible(selected.childIterator(true, true), true);
			EntityRoutines.visible(deselected.childIterator(true, true), false);
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
			EntityRoutines.visible(selected.childIterator(true, true), false);
			EntityRoutines.visible(deselected.childIterator(true, true), true);
		}
	}

	public Button(Resource<? extends Image> selectedImageResource,
			Resource<? extends Image> deselectedImageResource,
			TextContext textContext, String buttonText) {
		this(selectedImageResource, deselectedImageResource, textContext
				.width(), textContext.height(), textContext.transparency(),
				textContext.fontResource(), buttonText);
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
			int height, int transparency,
			Resource<? extends Font> fontResource, String buttonText) {

		// Register the pressed event
		events().put(ButtonEvent.Pressed, iterableChildren(true, true));

		// Create both labels
		selected = new Label(width, height, transparency, fontResource);
		deselected = new Label(width, height, transparency, fontResource);

		// Set textures
		selected.background().imageResource(selectedImageResource);
		deselected.background().imageResource(deselectedImageResource);

		// Set text and default center alignment
		selected.text().text(buttonText).alignment(Alignment.Center);
		deselected.text().text(buttonText).alignment(Alignment.Center);

		// Attach all components
		attach(selected, deselected);

		// Start deselected
		deselect();
	}

	/**
	 * @return selected label.
	 */
	public Label selectedLabel() {
		return selected;
	}

	/**
	 * @return the deselected label.
	 */
	public Label deselectedLabel() {
		return deselected;
	}
}

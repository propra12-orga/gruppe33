package com.indyforge.twod.engine.graphics.rendering.scenegraph.gui;

import java.awt.Font;
import java.awt.event.KeyEvent;

import com.indyforge.twod.engine.graphics.ImageDesc;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.resources.Resource;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class TextField extends Label {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum TextFieldEvent {
		Changed
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
			text().borderWidth(10);
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
			text().borderWidth(0);
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
		if (event == TextFieldEvent.Changed && source == this) {
			onTextFieldChanged();
		}
	}

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

		if (isSelected()) {

			// Find the scene
			Scene scene = findScene();

			if (scene != null) {

				for (int i = 0x30; i <= 0x39; i++) {
					if (scene.isSinglePressed(i)) {
						text().append((char) i);
					}
				}

				for (int i = 0x41; i <= 0x5A; i++) {
					if (scene.isSinglePressed(i)) {
						text().append((char) i);
					}
				}

				if (scene.isSinglePressed(KeyEvent.VK_PERIOD)) {
					text().append(".");
				}

				if (scene.isSinglePressed(KeyEvent.VK_BACK_SPACE)) {
					text().backspace();
				}

				if (scene.isSinglePressed(KeyEvent.VK_SPACE)) {
					text().append(" ");
				}

				if (text().isChanged()) {
					fireEvent(TextFieldEvent.Changed);
				}
			}
		}
	}

	/**
	 * Gets called when the text changed.
	 */
	protected void onTextFieldChanged() {
	}

	public TextField(ImageDesc imageDesc, Resource<? extends Font> fontResource) {
		super(imageDesc, fontResource);

		// Text fields ARE selectable!
		selectable(true);

		// Register the text field changed event
		events().put(TextFieldEvent.Changed, iterableChildren(true, true));
	}
}

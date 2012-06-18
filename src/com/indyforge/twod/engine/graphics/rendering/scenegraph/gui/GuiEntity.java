package com.indyforge.twod.engine.graphics.rendering.scenegraph.gui;

import java.awt.event.KeyEvent;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;

/**
 * Represents a gui entity. Gui entities can be "selected" by using the defaults
 * keys.
 * 
 * @author Christopher Probst
 * 
 */
public class GuiEntity extends GuiListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * The selected flag.
	 */
	private boolean selected = false;

	@Override
	protected void onSelected(GuiEntity guiEntity) {
		super.onSelected(guiEntity);
		if (guiEntity == this) {
			selected = true;
		}
	}

	@Override
	protected void onDeselected(GuiEntity guiEntity) {
		super.onDeselected(guiEntity);
		if (guiEntity == this) {
			selected = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity#onUpdate
	 * (float)
	 */
	@Override
	protected void onUpdate(float tpf) {
		super.onUpdate(tpf);

		// Find scene
		Scene scene = findScene();

		if (scene != null) {

			if (selected) {

				// The move dir
				int dir = 0;

				// Assign
				if (scene.isSinglePressed(KeyEvent.VK_UP)) {
					dir = -1;
				} else if (scene.isSinglePressed(KeyEvent.VK_DOWN)) {
					dir = 1;
				}

				if (dir != 0) {
					// Iterate...
					for (int next = cacheIndex() + dir;; next += dir) {

						// Check!
						if (next < 0) {
							next = parent().children().size() - 1;
						} else if (next >= parent().children().size()) {
							next = 0;
						}

						// Lookup child
						Entity nextChild = parent().childAt(next);

						// If gui entity... select it !
						if (nextChild instanceof GuiEntity) {
							deselect();
							((GuiEntity) nextChild).select();
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Selects this entity.
	 */
	public void select() {
		fireEvent(GuiEvent.Selected);
	}

	/**
	 * Deselects this entity.
	 */
	public void deselect() {
		fireEvent(GuiEvent.Deselected);
	}

	/**
	 * @return true if this gui entity is selected, otherwise false.
	 */
	public boolean isSelected() {
		return selected;
	}
}

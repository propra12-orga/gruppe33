package com.indyforge.twod.engine.graphics.rendering.scenegraph.gui;

import java.awt.event.KeyEvent;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;

public class GuiEntity extends GraphicsEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum GuiEvent {
		Selected, Deselected
	}

	private boolean selected = false;

	@Override
	protected void onEvent(Entity source, Object event, Object... params) {
		super.onEvent(source, event, params);

		if (event == GuiEvent.Selected) {
			selected = true;
			onSelected();
		} else if (event == GuiEvent.Deselected) {
			selected = false;
			onDeselected();
		}
	}

	protected void onSelected() {

	}

	protected void onDeselected() {

	}

	public GuiEntity() {
		events().put(GuiEvent.Selected, iterableChildren(true, true));
		events().put(GuiEvent.Deselected, iterableChildren(true, true));
	}

	public void select() {
		fireEvent(GuiEvent.Selected);
	}

	public void deselect() {
		fireEvent(GuiEvent.Deselected);
	}

	@Override
	protected void onUpdate(float tpf) {
		super.onUpdate(tpf);

		// Find scene
		Scene scene = findScene();

		if (scene != null && selected) {

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

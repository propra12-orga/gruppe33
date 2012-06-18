package com.indyforge.twod.engine.graphics.rendering.scenegraph.gui;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;

/**
 * 
 * @author Christopher Probst
 * 
 */
public abstract class GuiListener extends GraphicsEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum GuiEvent {
		Selected, Deselected
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity#onEvent
	 * (com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity,
	 * java.lang.Object, java.lang.Object[])
	 */
	@Override
	protected void onEvent(Entity source, Object event, Object... params) {
		super.onEvent(source, event, params);

		if (event == GuiEvent.Selected) {
			onSelected((GuiEntity) params[0]);
		} else if (event == GuiEvent.Deselected) {
			onDeselected((GuiEntity) params[0]);
		} else if (event == EntityEvent.Update) {
			if (parent() instanceof GuiEntity) {
				GuiEntity guiParent = (GuiEntity) parent();
				Scene scene = guiParent.findScene();
				if (scene != null) {
					onGuiUpdated(scene, guiParent, guiParent.isSelected(),
							(Float) params[0]);
				}
			}
		}
	}

	/**
	 * Called every frame.
	 * 
	 * @param scene
	 *            The scene.
	 * @param guiEntity
	 *            The gui entity.
	 * @param selected
	 *            The selected-flag.
	 * @param tpf
	 *            The time-per-frame.
	 */
	protected void onGuiUpdated(Scene scene, GuiEntity guiEntity,
			boolean selected, float tpf) {

	}

	/**
	 * Called when this entity is selected.
	 * 
	 * @param guiEntity
	 *            The gui entity.
	 */
	protected void onSelected(GuiEntity guiEntity) {

	}

	/**
	 * Called when this entity is deselected.
	 * 
	 * @param guiEntity
	 *            The gui entity.
	 */
	protected void onDeselected(GuiEntity guiEntity) {

	}

	/**
	 * Creates a new gui entity.
	 */
	public GuiListener() {
		events().put(GuiEvent.Selected, iterableChildren(true, true));
		events().put(GuiEvent.Deselected, iterableChildren(true, true));
	}
}

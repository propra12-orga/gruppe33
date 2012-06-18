package com.indyforge.twod.engine.graphics.rendering.scenegraph.gui;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;

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
			onSelected((GuiEntity) source);
		} else if (event == GuiEvent.Deselected) {
			onDeselected((GuiEntity) source);
		}
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

package com.indyforge.twod.engine.graphics.rendering.scenegraph.gui;

import java.awt.Font;
import java.awt.Image;

import com.indyforge.twod.engine.graphics.ImageDesc;
import com.indyforge.twod.engine.resources.Resource;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class MenuButton extends Button {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * If true this button will enter its menu, otherwise it will leave its
	 * menu.
	 */
	private final boolean enter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.twod.engine.graphics.rendering.scenegraph.gui.Button#
	 * onButtonPressed()
	 */
	@Override
	protected void onButtonPressed() {
		super.onButtonPressed();

		if (enter) {
			enter(0);
		} else {
			leave(true);
		}
	}

	public MenuButton(Resource<? extends Image> selectedImageResource,
			Resource<? extends Image> deselectedImageResource,
			ImageDesc imageDesc, Resource<? extends Font> fontResource,
			String buttonText, boolean enter) {
		super(selectedImageResource, deselectedImageResource, imageDesc,
				fontResource, buttonText);
		this.enter = enter;
	}
}

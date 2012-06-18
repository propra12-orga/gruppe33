package com.indyforge.twod.engine.graphics.rendering.scenegraph.gui;

import java.awt.Image;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.RenderedImage;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Text;
import com.indyforge.twod.engine.resources.Resource;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class Button extends GuiEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final RenderedImage selected = new RenderedImage().centered(true),
			deselected = new RenderedImage().centered(true);
	private final Text text = (Text) new Text().centered(true);

	@Override
	protected void onSelected() {
		super.onSelected();
		selected.visible(true);
		deselected.visible(false);
	}

	@Override
	protected void onDeselected() {
		super.onDeselected();
		selected.visible(false);
		deselected.visible(true);
	}

	public Button() {
		this(null, null);
	}

	public Button(Resource<? extends Image> selectedImageResource,
			Resource<? extends Image> deselectedImageResource) {
		selected.imageResource(selectedImageResource).useRatio();
		deselected.imageResource(deselectedImageResource).useRatio();
		attach(selected, deselected, text);
		text.scale().scaleLocal(0.8f);
		deselect();
	}

	public Text text() {
		return text;
	}

	public RenderedImage selectedImage() {
		return selected;
	}

	public RenderedImage deselectedImage() {
		return deselected;
	}
}

package com.indyforge.twod.engine.graphics.rendering.gui;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.RenderedImage;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;
import com.indyforge.twod.engine.resources.assets.Asset;
import com.indyforge.twod.engine.resources.assets.AssetManager;

public class GuiCreator {

	public static Scene createGuiScene() throws Exception {

		final Scene gui = new Scene(new AssetManager(new File(
				"scenes/default.zip")), 1024, 1024);
		gui.scale().set(gui.sizeAsVector());

		Asset<BufferedImage> back = gui.assetManager().loadImage(
				"assets/images/gui/main.jpg", true);

		Asset<BufferedImage> bomb = gui.assetManager().loadImage(
				"assets/images/bomb.png", true);

		RenderedImage backRI = new RenderedImage(back).centered(true);
		final RenderedImage bombRI = new RenderedImage(bomb).centered(true);

		backRI.position().set(0.5f, 0.5f);

		bombRI.position().set(157 / 1024f, 415 / 1024f);
		bombRI.scale().set(0.08f, 0.08f);

		final Vector2f[] fields = new Vector2f[] { new Vector2f(157 / 1024f, 415 / 1024f), new Vector2f(548 / 1024f, 495 / 1024f),new Vector2f(457 / 1024f, 759 / 1024f),new Vector2f(772 / 1024f, 973 / 1024f) };

		Entity border = new Entity() {

			int pos = 0;
			boolean pressed = false;

			@Override
			protected void onUpdate(float tpf) {
				super.onUpdate(tpf);

				if(gui.isPressed(KeyEvent.VK_DOWN) && !pressed) {
					bombRI.position().set(fields[pos++]);
					if(pos >= fields.length) {
						pos = 0;
					}
					pressed = true;
				}else if(gui.isPressed(KeyEvent.VK_UP) && !pressed){
					bombRI.position().set(fields[pos--]);
					if(pos <= 0) {
						pos = 0;
					}
					pressed = true;
				}else if(!gui.isPressed(KeyEvent.VK_UP) && pressed){
					pressed = false;
				}else if(!gui.isPressed(KeyEvent.VK_DOWN) && pressed){
					pressed = false;
				}
			}
		};

		gui.attach(border);
		gui.attach(backRI);
		gui.attach(bombRI);

		return gui;

	}

}

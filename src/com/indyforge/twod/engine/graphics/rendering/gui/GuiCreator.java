package com.indyforge.twod.engine.graphics.rendering.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.RenderedImage;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.resources.assets.Asset;
import com.indyforge.twod.engine.resources.assets.AssetManager;

public class GuiCreator {


	public static Scene createGuiScene2() throws Exception {

		final Scene gui = new Scene(new AssetManager(new File(
				"scenes/default.zip")), 1024, 1024);
		gui.scale().set(gui.sizeAsVector());

		Asset<BufferedImage> img = gui.assetManager().loadImage(
				"assets/images/solid.png", true);

		RenderedImage a = new RenderedImage(img).centered(true);
		RenderedImage b = new RenderedImage(img).centered(true);

		a.position().set(0.5f, 0.25f);
		a.scale().set(0.25f, 0.25f);

		
		b.position().set(0.5f, 0.75f);
		b.scale().set(0.25f, 0.25f);

		
		GraphicsEntity border = new GraphicsEntity() {
			
			boolean up = true;
			
			@Override
			protected void onRender(Graphics2D original, Graphics2D transformed) {
				super.onRender(original, transformed);

				
				transformed.setColor(Color.blue);
				transformed.translate(-0.5, -0.5);
				transformed.fillRect(0, 0, 1, 1);
			}
			
			@Override
			protected void onUpdate(float tpf) {
				super.onUpdate(tpf);
				
				if(findScene().isPressed(KeyEvent.VK_DOWN) && up) {
					position().y = 0.75f;
					up = false;
				} else if(findScene().isPressed(KeyEvent.VK_UP) && !up) {
					position().y = 0.25f;
					up = true;
				} else if(findScene().isPressed(KeyEvent.VK_ENTER) && !up) {
					try {
						findScene().processor().root(createGuiScene());
					} catch (Exception e) {
						System.out.println("Could load gui 2: ");
					}
				}
			}
		};

		border.scale().set(0.25f, 0.25f);
		border.position().set(0.5f, 0.25f);
		
		gui.attach(border);
		gui.attach(a);
		gui.attach(b);
		

		return gui;

	}
	
	
	public static Scene createGuiScene() throws Exception {

		final Scene gui = new Scene(new AssetManager(new File(
				"scenes/default.zip")), 1024, 1024);
		gui.scale().set(gui.sizeAsVector());

		Asset<BufferedImage> img = gui.assetManager().loadImage(
				"assets/images/solid.png", true);

		RenderedImage a = new RenderedImage(img).centered(true);
		RenderedImage b = new RenderedImage(img).centered(true);

		a.position().set(0.5f, 0.25f);
		a.scale().set(0.25f, 0.25f);

		
		b.position().set(0.5f, 0.75f);
		b.scale().set(0.25f, 0.25f);

		
		GraphicsEntity border = new GraphicsEntity() {
			
			boolean up = true;
			
			@Override
			protected void onRender(Graphics2D original, Graphics2D transformed) {
				super.onRender(original, transformed);

				
				transformed.setColor(Color.red);
				transformed.translate(-0.5, -0.5);
				transformed.fillRect(0, 0, 1, 1);
			}
			
			@Override
			protected void onUpdate(float tpf) {
				super.onUpdate(tpf);
				
				if(findScene().isPressed(KeyEvent.VK_DOWN) && up) {
					position().y = 0.75f;
					up = false;
				} else if(findScene().isPressed(KeyEvent.VK_UP) && !up) {
					position().y = 0.25f;
					up = true;
				} else if(findScene().isPressed(KeyEvent.VK_ENTER) && up) {
					try {
						findScene().processor().root(createGuiScene2());
					} catch (Exception e) {
						System.out.println("Could load gui 2: ");
					}
				}
			}
		};

		border.scale().set(0.25f, 0.25f);
		border.position().set(0.5f, 0.25f);
		
		gui.attach(border);
		gui.attach(a);
		gui.attach(b);
		

		return gui;

	}

}

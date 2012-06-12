package com.indyforge.twod.engine.graphics.rendering.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;
import com.indyforge.twod.engine.graphics.sprite.Animation;
import com.indyforge.twod.engine.graphics.sprite.Sprite;
import com.indyforge.twod.engine.resources.assets.AssetManager;
import com.indyforge.twod.engine.sound.SoundManager;

public class GuiManager {

	private Scene activeGui;

	private List<Gui> guiList;

	private AssetManager am;

	private Entity testE;

	private Gui main, multi;

	private SoundManager sm;

	private boolean introFinish;

	private Intro intro;

	public GuiManager() throws Exception {

		// A List of all needed Guis
		List<Gui> guiList = new ArrayList<Gui>();

		am = new AssetManager(new File("scenes/default.zip"));

		sm = new SoundManager(am);

		sm.putSound("back", "assets/sounds/menu2.wav");

		sm.playSound("back", true);

		main = this.createMain();

		multi = this.createMultiPlayer();

		intro = new Intro(am, new Runnable() {

			@Override
			public void run() {
				activeGui.processor().root(main);
				activeGui = main;

			}
		});

		activeGui = intro;

		// guiList.add(main);
		// guiList.add(multi);

		// activeGui = main;

	}

	public Gui createMultiPlayer() throws Exception {
		// Hier laden wir das boom sprite... 5 *5 bilder
		Sprite boom = new Sprite(am.loadImage(
				"assets/images/animated/boom.png", true), 5, 5);

		// Create new explosion entity
		Animation animation = boom
				.newAnimationFromRange("explosion", 33, 0, 0, 25).loop(false)
				.paused(false);

		final Vector2f[] fields = new Vector2f[] {
				new Vector2f(119 / 1024f, 364 / 1024f),
				new Vector2f(545 / 1024f, 640 / 1024f),
				new Vector2f(765 / 1024f, 971 / 1024f), };

		Gui multi = new Gui(am, 1024, 1024, animation,
				"assets/images/gui/multiplayer.jpg", "assets/images/bomb.png",
				fields);

		multi.actions(2).add(new Runnable() {

			@Override
			public void run() {

				activeGui.processor().root(main);
				activeGui = main;

			}

		});

		return multi;

	}

	public Gui createMain() throws Exception {

		// Hier laden wir das boom sprite... 5 *5 bilder
		Sprite boom = new Sprite(am.loadImage(
				"assets/images/animated/boom.png", true), 5, 5);

		// Create new explosion entity
		Animation animation = boom
				.newAnimationFromRange("explosion", 33, 0, 0, 25).loop(false)
				.paused(false);

		final Vector2f[] fields = new Vector2f[] {
				new Vector2f(157 / 1024f, 415 / 1024f),
				new Vector2f(548 / 1024f, 495 / 1024f),
				new Vector2f(457 / 1024f, 759 / 1024f),
				new Vector2f(772 / 1024f, 973 / 1024f) };

		Gui main = new Gui(am, 1024, 1024, animation,
				"assets/images/gui/main.jpg", "assets/images/bomb.png", fields);

		main.actions(1).add(new Runnable() {

			@Override
			public void run() {
				activeGui.processor().root(multi);
				activeGui = multi;

			}

		});

		main.actions(3).add(new Runnable() {

			@Override
			public void run() {

				System.exit(0);
			}
		});

		return main;

	}

	public Scene getScene() {

		return activeGui;
	}

}

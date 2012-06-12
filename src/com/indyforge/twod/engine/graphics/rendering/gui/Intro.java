package com.indyforge.twod.engine.graphics.rendering.gui;

import java.util.ArrayList;
import java.util.List;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.RenderedImage;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;
import com.indyforge.twod.engine.resources.assets.AssetManager;
import com.indyforge.twod.engine.sound.SoundManager;

public class Intro extends Scene {

	private List<RenderedImage> imgList;

	private float time;

	private float delay1, delay2, delay3;

	private boolean status, finished;

	private Runnable callback;

	private SoundManager sm;

	private int i, x;

	public Intro(AssetManager am, Runnable callback) throws Exception {
		super(am, 1024, 1024);

		imgList = new ArrayList<RenderedImage>();
		time = 0f;
		i = 0;
		delay1 = 2f;
		delay2 = 0.5f;
		delay3 = 2f;

		sm = new SoundManager(am);

		sm.putSound("1", "assets/sounds/exp.wav");

		x = 1;

		status = false;
		finished = false;
		imgList.add((RenderedImage) new RenderedImage(am.loadImage(
				"assets/images/gui/intro1.jpg", true)).centered(true).position(
				new Vector2f(0.5f, 0.5f)));
		imgList.add((RenderedImage) new RenderedImage(am.loadImage(
				"assets/images/gui/intro2.jpg", true)).centered(true).position(
				new Vector2f(0.5f, 0.5f)));
		imgList.add((RenderedImage) new RenderedImage(am.loadImage(
				"assets/images/gui/intro3.jpg", true)).centered(true).position(
				new Vector2f(0.5f, 0.5f)));
		imgList.add((RenderedImage) new RenderedImage(am.loadImage(
				"assets/images/gui/intro4.jpg", true)).centered(true).position(
				new Vector2f(0.5f, 0.5f)));
		imgList.add((RenderedImage) new RenderedImage(am.loadImage(
				"assets/images/gui/intro5.jpg", true)).centered(true).position(
				new Vector2f(0.5f, 0.5f)));
		imgList.add((RenderedImage) new RenderedImage(am.loadImage(
				"assets/images/gui/intro6.jpg", true)).centered(true).position(
				new Vector2f(0.5f, 0.5f)));

		scale(sizeAsVector());

		attach(imgList.get(0));
		System.out.println(imgList.size());
		this.callback = callback;
	}

	@Override
	protected void onUpdate(float tpf) {
		super.onUpdate(tpf);

		if (i < imgList.size()) {

			if (!status) {
				time += tpf;

				if (time >= delay1) {
					attach(imgList.get(i));
					System.out.println("Delay 1");
					i++;
					time = 0f;
				}
				if (i == 4) {
					sm.playSound("1", true);
					status = true;
					time = 0f;
				}
			} else {
				time += tpf;

				if (time >= delay2) {

					System.out.println("Delay 2");
					sm.playSound("1", true);

					attach(imgList.get(i));

					i++;
					time = 0f;
				}
			}

		} else {
			time += tpf;
			if (time >= delay3) {
				System.out.println("Delay 3");
				callback.run();
			}

		}

	}

}

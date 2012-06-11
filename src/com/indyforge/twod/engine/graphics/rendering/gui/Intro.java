package com.indyforge.twod.engine.graphics.rendering.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.RenderedImage;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;
import com.indyforge.twod.engine.resources.assets.AssetManager;

public class Intro extends Scene {

	private List<RenderedImage> imgList;

	private float time;

	private float delay1, delay2;

	private boolean status,finished;

	private int i;

	public Intro(AssetManager am) throws Exception {
		super(am, 1024, 1024);

		imgList = new ArrayList<RenderedImage>();
		time = 0f;
		i = 0;
		delay1 = 2f;
		delay2 = 0.5f;
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

	}

	@Override
	protected void onUpdate(float tpf) {
		super.onUpdate(tpf);

		if (i < imgList.size()) {

			if (!status) {
				time += tpf;

				if (time >= delay1) {
					attach(imgList.get(i));
					i++;
					time = 0f;
				}
				if (i == 4) {
					status = true;
				}
			} else {
				time += tpf;

				if (time >= delay2) {
					attach(imgList.get(i));
					i++;
					time = 0f;
				}
			}

		}else{
			finished = true;
		}

	}
	
	
	public boolean running(){
		
		return finished;
	}

}

package com.indyforge.twod.engine.graphics.rendering.gui.intro;

import java.awt.Font;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;

import com.indyforge.twod.engine.graphics.ImageDesc;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.RenderedImage;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.gui.Label;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.transform.Pause;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.transform.PositionPath;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.transform.PositionTarget;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.transform.ReachableQueue;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.transform.TransformMotor;
import com.indyforge.twod.engine.resources.Resource;
import com.indyforge.twod.engine.resources.TransientSystemFontResource;
import com.indyforge.twod.engine.resources.assets.Asset;
import com.indyforge.twod.engine.resources.assets.AssetManager;

public class IntroCreator {

	public static Scene createIntro() throws Exception {

		Scene scene = new Scene(
				new AssetManager(new File("scenes/default.zip")), 1024, 1024);

		scene.scale(scene.sizeAsVector());

		// Bis hierhin ist ja alles Standard....

		Asset<BufferedImage> selectedA = scene.assetManager().loadImage(
				"assets/images/gui/progui/button_selected.png", true);
		Asset<BufferedImage> deselectedA = scene.assetManager().loadImage(
				"assets/images/gui/progui/button.png", true);
		Asset<BufferedImage> selectedB = scene.assetManager().loadImage(
				"assets/images/gui/progui/button_2_selected.png", true);
		Asset<BufferedImage> deselectedB = scene.assetManager().loadImage(
				"assets/images/gui/progui/button_2.png", true);

		Asset<BufferedImage> backImage = scene.assetManager().loadImage(
				"assets/images/gui/progui/gui_back.jpg", true);

		RenderedImage background = new RenderedImage(backImage);
		scene.attach(background);

		final Resource<Font> font = new TransientSystemFontResource("Arial",
				Font.BOLD, 48);
		final Resource<Font> tinyFont = new TransientSystemFontResource(
				"Arial", Font.BOLD, 36);

		/*
		 * Used to initialize!
		 */
		ImageDesc desc1 = new ImageDesc().width(512).height(128)
				.transparency(Transparency.TRANSLUCENT);
		ImageDesc desc2 = new ImageDesc().width(256).height(96)
				.transparency(Transparency.TRANSLUCENT);

		Label hello = new Label(desc1, font);
		hello.text().text("Indy Forge presents");
		hello.attach(new TransformMotor());
		hello.scale().scaleLocal(1.3f);
		hello.position().x = 1.5f;
		hello.position().y = 0.5f;

		Label hello2 = new Label(desc1, font);
		hello2.text().text("the BEST remake of");
		hello2.attach(new TransformMotor());
		hello2.position().x = 0.5f;
		hello2.position().y = 1.5f;

		Label hello3 = new Label(desc1, font);
		hello3.text().text("Bomber Man!");
		hello3.attach(new TransformMotor());
		hello3.position().x = -1.5f;
		hello3.position().y = 0.5f;

		// The target queue
		ReachableQueue positions = new ReachableQueue();

		positions.reachables().offer(new Pause(2f));

		positions.reachables().offer(
				new PositionPath(hello, Vector2f.west().scaleLocal(1f), 3));

		// positions.reachables().offer(
		// new PositionTarget(hello, new Vector2f(0.5f, 0.5f), 1.5f));
		// positions.reachables().offer(new Pause(2f));
		// positions.reachables().offer(
		// new PositionTarget(hello, new Vector2f(0.5f, -1.5f), 3f));
		//
		// positions.reachables().offer(
		// new PositionTarget(hello2, new Vector2f(0.5f, 0.5f), 1.5f));
		// positions.reachables().offer(new Pause(2f));
		// positions.reachables().offer(
		// new PositionTarget(hello2, new Vector2f(1.5f, 0.5f), 3f));
		//
		// positions.reachables().offer(
		// new PositionTarget(hello3, new Vector2f(0.5f, 0.5f), 1.5f));
		// positions.reachables().offer(new Pause(2f));
		// positions.reachables().offer(
		// new PositionTarget(hello3, new Vector2f(0.5f, 1.5f), 3f));

		// Attach and return
		scene.attach(positions, hello, hello2, hello3);
		return scene;
	}
}

package propra2012.gruppe33;

import java.awt.Color;
import java.awt.Font;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;

import com.indyforge.twod.engine.graphics.ImageDesc;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.RenderedImage;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Text.Alignment;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.gui.Button;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.gui.MenuButton;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.gui.TextField;
import com.indyforge.twod.engine.resources.Resource;
import com.indyforge.twod.engine.resources.TransientSystemFontResource;
import com.indyforge.twod.engine.resources.assets.Asset;
import com.indyforge.twod.engine.resources.assets.AssetManager;

public class MenuExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		final SceneProcessor processor = new SceneProcessor("Gui Test", 800,
				600);

		final Scene scene = new Scene(new AssetManager(new File(
				"scenes/default.zip")), 1024, 1024);

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

		final Resource<Font> font = new TransientSystemFontResource(
				"Sans Serif", Font.BOLD, 48);
		final Resource<Font> tinyFont = new TransientSystemFontResource(
				"Sans Serif", Font.BOLD, 36);

		/*
		 * Used to initialize!
		 */
		ImageDesc desc2 = new ImageDesc().width(256).height(96)
				.transparency(Transparency.TRANSLUCENT);

		/*
		 * Der Options Button.
		 */
		Button rootButton = new MenuButton(selectedA, deselectedA, desc2, font,
				"Options", true);

		/*
		 * Der Back Button.
		 */
		Button backButton = new MenuButton(selectedA, deselectedA, desc2, font,
				"Back", false);

		// Create a new text field
		final TextField tf = new TextField(desc2, tinyFont);
		tf.background().imageResource(deselectedA);
		tf.text().alignment(Alignment.Center).textColor(Color.WHITE);
		tf.position().set(0, -0.6f);

		// Create a new text field
		final TextField tf2 = new TextField(desc2, tinyFont);
		tf2.background().imageResource(deselectedA);
		tf2.text().alignment(Alignment.Center).textColor(Color.WHITE);
		tf2.position().set(0, -1.2f);

		rootButton.container().attach(tf2, tf, backButton);
		rootButton.containerVisible(false);

		rootButton.scale().set(0.5f, 0.5f);
		rootButton.position().set(0.75f, 0.8f);
		rootButton.select();
		scene.attach(rootButton);

		processor.root(scene);
		processor.start(60);
	}
}

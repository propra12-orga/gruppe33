package propra2012.gruppe33;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.gui.Button;
import com.indyforge.twod.engine.resources.Resource;
import com.indyforge.twod.engine.resources.TransientDerivedFontResource;
import com.indyforge.twod.engine.resources.assets.Asset;
import com.indyforge.twod.engine.resources.assets.AssetManager;

public class GuiTestApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		SceneProcessor processor = new SceneProcessor("Gui Test", 800, 600);

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

		// The background
		// RenderedImage background = new RenderedImage(backImage);
		// scene.attach(background);

		// Load a font
		Resource<Font> font = new TransientDerivedFontResource(scene
				.assetManager().loadFont("assets/fonts/ALGERIA.TTF"),
				Font.PLAIN, 36);

		Button btn1 = new Button(selectedA, deselectedA);
		btn1.text().fontResource(font);
		btn1.text().text("HELLO 123");
		btn1.scale().set(0.5f, 0.5f);
		btn1.position().set(0.25f, 0.25f);

		Button btn2 = new Button(selectedB, deselectedB);
		btn2.select();
		btn2.scale().set(0.5f, 0.5f);
		btn2.position().set(0.75f, 0.75f);

		scene.attach(btn1, btn2);

		processor.root(scene);
		processor.start(60);
	}
}

package propra2012.gruppe33;

import java.awt.Color;
import java.awt.Font;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;

import com.indyforge.twod.engine.graphics.ImageDesc;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.RenderedImage;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Text.Alignment;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.gui.Button;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.gui.Label;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.gui.TextField;
import com.indyforge.twod.engine.resources.Resource;
import com.indyforge.twod.engine.resources.TransientSystemFontResource;
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

		RenderedImage background = new RenderedImage(backImage);
		scene.attach(background);

		Resource<Font> font = new TransientSystemFontResource("Sans Serif",
				Font.BOLD, 48);

		/*
		 * Used to initialize!
		 */
		ImageDesc desc1 = new ImageDesc().width(164).height(48)
				.transparency(Transparency.TRANSLUCENT);
		ImageDesc desc2 = new ImageDesc().width(256).height(96)
				.transparency(Transparency.TRANSLUCENT);

		// Here we store the labels
		Entity labels = new Entity();

		// Setup labels
		Label label1 = new Label(desc1, font);
		label1.background().imageResource(deselectedA);
		label1.text().text("Host/IP");

		Label label2 = new Label(desc1, font);
		label2.background().imageResource(deselectedA);
		label2.text().text("Port");

		// Adjust label scales
		label1.scale().scaleLocal(0.3f);
		label1.position().set(0.25f, 0.2f);
		label2.scale().scaleLocal(0.3f);
		label2.position().set(0.25f, 0.4f);

		// Attach both
		labels.attach(label1, label2);

		// Create a new text field
		TextField tf = new TextField(desc2, font);
		tf.text().alignment(Alignment.Center).textColor(Color.WHITE);
		tf.select();
		tf.scale().set(0.5f, 0.5f);
		tf.position().set(0.75f, 0.2f);

		// Create a new text field
		TextField tf2 = new TextField(desc2, font);
		tf2.text().alignment(Alignment.Center).textColor(Color.WHITE);
		tf2.scale().set(0.5f, 0.5f);
		tf2.position().set(0.75f, 0.4f);

		Button btn1 = new Button(selectedA, deselectedA, desc2, font, "Connect");

		btn1.scale().set(0.5f, 0.5f);
		btn1.position().set(0.75f, 0.75f);

		labels.attach(tf, tf2, btn1);

		scene.attach(labels);
		processor.root(scene);
		processor.start(60);
	}
}

package propra2012.gruppe33;

import java.awt.Color;
import java.awt.Font;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.RenderedImage;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Text.Alignment;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.gui.Button;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.gui.Label;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.gui.TextContext;
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

		// Load a font
		// Resource<Font> font = new TransientDerivedFontResource(scene
		// .assetManager().loadFont("assets/fonts/ALGERIA.TTF"),
		// Font.PLAIN, 36);

		RenderedImage background = new RenderedImage(backImage);
		scene.attach(background);

		Resource<Font> font = new TransientSystemFontResource("Sans Serif",
				Font.BOLD, 48);

		/*
		 * Used to initialize!
		 */
		TextContext labelCtx = new TextContext().width(164).height(48)
				.transparency(Transparency.TRANSLUCENT).fontResource(font);
		TextContext textFieldCtx = new TextContext().width(256).height(96)
				.transparency(Transparency.TRANSLUCENT).fontResource(font);

		// Here we store the labels
		Entity labels = new Entity();

		// Setup labels
		Label label1 = new Label(labelCtx);
		label1.background().imageResource(deselectedA);
		label1.text().text("Host/IP");

		Label label2 = new Label(labelCtx);
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
		TextField tf = new TextField(textFieldCtx);
		tf.text().alignment(Alignment.Center).textColor(Color.WHITE);
		tf.select();
		tf.scale().set(0.5f, 0.5f);
		tf.position().set(0.75f, 0.2f);

		// Create a new text field
		TextField tf2 = new TextField(textFieldCtx);
		tf2.text().alignment(Alignment.Center).textColor(Color.WHITE);
		tf2.scale().set(0.5f, 0.5f);
		tf2.position().set(0.75f, 0.4f);

		Button btn1 = new Button(selectedA, deselectedA, textFieldCtx,
				"Connect");

		btn1.scale().set(0.5f, 0.5f);
		btn1.position().set(0.75f, 0.75f);

		labels.attach(tf, tf2, btn1);

		scene.attach(labels);
		processor.root(scene);
		processor.start(60);
	}
}

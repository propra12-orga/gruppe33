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
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Text;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Text.Alignment;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.gui.Button;
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

		// Here we store the labels
		Entity labels = new Entity();

		// Setup labels

		RenderedImage lblBg1 = new RenderedImage(deselectedA);
		RenderedImage lblBg2 = new RenderedImage(deselectedA);

		Text hostLbl = new Text(256, 48, Transparency.TRANSLUCENT);
		hostLbl.fontResource(font).text("Host/IP").centered(false);
		Text portLbl = new Text(256, 48, Transparency.TRANSLUCENT);
		portLbl.fontResource(font).text("Port").centered(false);

		// Adjust label scales
		lblBg1.scale().scaleLocal(0.4f);
		lblBg2.scale().scaleLocal(0.4f);

		lblBg1.position().x = 0.05f;
		lblBg1.position().y = 0.05f;
		lblBg2.position().x = 0.05f;
		lblBg2.position().y = 0.35f;

		hostLbl.scale().x = 0.5f;
		portLbl.scale().x = 0.5f;
		hostLbl.position().x = 0.1f;
		hostLbl.position().y = 0.1f;
		portLbl.position().x = 0.1f;
		portLbl.position().y = 0.4f;

		// Attach both
		// labels.attach(lblBg1.useRatio(), hostLbl.useRatio(),
		// lblBg2.useRatio(),
		// portLbl.useRatio());

		// Create a new text field
		TextField tf = new TextField(256, 96, Transparency.TRANSLUCENT, font);
		tf.text().alignment(Alignment.Center).textColor(Color.WHITE);
		tf.select();
		tf.scale().set(0.5f, 0.5f);
		tf.position().set(0.75f, 0.15f);

		// Create a new text field
		TextField tf2 = new TextField(256, 96, Transparency.TRANSLUCENT, font);
		tf2.text().alignment(Alignment.Center).textColor(Color.WHITE);
		tf2.scale().set(0.5f, 0.5f);
		tf2.position().set(0.75f, 0.45f);

		// Create a new text field
		TextField tf3 = new TextField(256, 96, Transparency.TRANSLUCENT, font);
		tf3.text().alignment(Alignment.Center).textColor(Color.WHITE);
		tf3.scale().set(0.5f, 0.5f);
		tf3.select();
		tf3.position().set(0.75f, 0.55f);

		Button btn1 = new Button(selectedA, deselectedA, 256, 96,
				Transparency.TRANSLUCENT, font);
		btn1.text().text("Connect").alignment(Alignment.Center);
		btn1.scale().set(0.5f, 0.5f);
		btn1.position().set(0.75f, 0.75f);

		labels.attach(tf, tf2, tf3, btn1);

		scene.attach(labels);
		processor.root(scene);
		processor.start(60);
	}
}

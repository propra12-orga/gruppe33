package propra2012.gruppe33;

import java.awt.Color;
import java.awt.Font;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.InetSocketAddress;

import com.indyforge.foxnet.rmi.InvokerManager;
import com.indyforge.foxnet.rmi.pattern.change.Session;
import com.indyforge.foxnet.rmi.util.Future;
import com.indyforge.foxnet.rmi.util.FutureCallback;
import com.indyforge.twod.engine.graphics.ImageDesc;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.RenderedImage;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor.NetworkMode;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Text.Alignment;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.gui.Button;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.gui.Label;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.gui.MenuButton;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.gui.TextField;
import com.indyforge.twod.engine.resources.Resource;
import com.indyforge.twod.engine.resources.TransientDerivedFontResource;
import com.indyforge.twod.engine.resources.TransientSystemFontResource;
import com.indyforge.twod.engine.resources.assets.Asset;
import com.indyforge.twod.engine.resources.assets.AssetManager;
import com.indyforge.twod.engine.sound.SoundManager;

public class Gui {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		final SceneProcessor processor = new SceneProcessor("Gui Test", 800,
				600);

		final Scene scene = new Scene(new AssetManager(new File(
				"scenes/default.zip")), 1024, 1024);
		

		AssetManager am = new AssetManager(new File("scenes/default.zip"));

		
		SoundManager sm = new SoundManager(am);

		sm.putSound("back", "assets/sounds/menu2.wav");
		
		sm.playSound("back", true);

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
				"assets/images/gui/progui/back.jpg", true);

		RenderedImage background = new RenderedImage(backImage);
		scene.attach(background);

		final Resource<Font> font = new TransientSystemFontResource(
				"Sans Serif", Font.BOLD, 48);
		
		final Resource<Font> tinyFont = new TransientSystemFontResource(
				"Sans Serif", Font.BOLD, 32);

		
		final Resource<Font> extremTinyFont = new TransientSystemFontResource(
				"Sans Serif", Font.BOLD, 30);


		
		
		/*
		 * Used to initialize!
		 */
		ImageDesc desc1 = new ImageDesc().width(400).height(96)
				.transparency(Transparency.TRANSLUCENT);
		
		ImageDesc desc2 = new ImageDesc().width(256).height(96)
				.transparency(Transparency.TRANSLUCENT);
		
		ImageDesc desc3 = new ImageDesc().width(96).height(96)
				.transparency(Transparency.TRANSLUCENT);

		
		
		/*
		 * MAIN
		 */
		Button spB = new MenuButton(selectedA, deselectedA, desc2, tinyFont,
				"SINGLEPLAYER", true);
		
		Button mpB = new MenuButton(selectedA, deselectedA, desc2, tinyFont,
				"MULTIPLAYER", true);
		
		
		Button optB = new MenuButton(selectedA, deselectedA, desc2, font,
				"OPTIONS", true);
		
		Button quitB = new Button(selectedA, deselectedA, desc2, font, "QUIT") {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.indyforge.twod.engine.graphics.rendering.scenegraph.gui.Button
			 * #onButtonPressed()
			 */
			@Override
			protected void onButtonPressed() {
				super.onButtonPressed();
				System.exit(0);
				

			}
		};
		

		/*
		 * SINGLEPLAYER
		 */
		
		final TextField epTf = new TextField(desc3, tinyFont);
		epTf.background().imageResource(deselectedA);
		epTf.text().alignment(Alignment.Center).textColor(Color.WHITE);
		
		
		final TextField botSTf = new TextField(desc3, tinyFont);
		botSTf.background().imageResource(deselectedA);
		botSTf.text().alignment(Alignment.Center).textColor(Color.WHITE);
		
		Label epL = new Label(desc2, tinyFont);
		epL.background().imageResource(deselectedA);
		epL.text().text("Enemys(1-3)");
		
		Label botSTL = new Label(desc2, tinyFont);
		botSTL.background().imageResource(deselectedA);
		botSTL.text().text("Difficulty(1-3)");
		
		Button backMainSPB = new MenuButton(selectedA, deselectedA, desc2, font,
				"Back", false);
		
		
		Button startB = new Button(selectedA, deselectedA, desc2, font, "Start") {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.indyforge.twod.engine.graphics.rendering.scenegraph.gui.Button
			 * #onButtonPressed()
			 */
			@Override
			protected void onButtonPressed() {
				
			}
		};

		
		
		/*
		 * MULTIPLAYER
		 */
		final TextField ipTf = new TextField(desc1, font);
		ipTf.background().imageResource(deselectedA);
		ipTf.text().alignment(Alignment.Center).textColor(Color.WHITE);

		
		final TextField portTf = new TextField(desc1, font);
		portTf.background().imageResource(deselectedA);
		portTf.text().alignment(Alignment.Center).textColor(Color.WHITE);

		
		Button backMainMPB = new MenuButton(selectedA, deselectedA, desc2, font,
				"Back", false);
		
		Button connectB = new Button(selectedA, deselectedA, desc2, font, "Connect") {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.indyforge.twod.engine.graphics.rendering.scenegraph.gui.Button
			 * #onButtonPressed()
			 */
			@Override
			protected void onButtonPressed() {
				super.onButtonPressed();
				super.onButtonPressed();

				InetSocketAddress addr = new InetSocketAddress(
						ipTf.text().text(), Integer.parseInt(portTf.text().text()));

				// Connect the scene
				Session<SceneProcessor> session;
				try {
					processor.networkMode(NetworkMode.Client);
					session = processor.openClient(addr).linkClient("Kr0e");

					// Get the invoker manager
					InvokerManager man = InvokerManager.of(session);

					man.closeFuture().add(new FutureCallback() {

						@Override
						public void completed(Future future) throws Exception {

							processor.shutdownRequest(true);
						}
					});

					Label cont = new Label(new ImageDesc().width(256)
							.height(64).transparency(Transparency.TRANSLUCENT),
							font);

					cont.text().text("Connected!");

					scene.detachAll();
					scene.attach(cont);

					cont.position().set(0.5f, 0.5f);

				} catch (Exception e) {
					e.printStackTrace();
				}

				

			}
		};
		
		Label ipL = new Label(desc2, font);
		ipL.background().imageResource(deselectedA);
		ipL.text().text("Host/IP");

		Label portL = new Label(desc2, font);
		portL.background().imageResource(deselectedA);
		portL.text().text("Port");
		
		/*
		 * OPTIONS
		 */
		Button backMainOPTB = new MenuButton(selectedA, deselectedA, desc2, font,
				"Back", false);
		
		
		/*
		 * MAIN SETTINGS
		 */
				
		spB.scale().set(0.3f, 0.3f);
		spB.position().set(0.2f, 0.1f);
		spB.container().attach(epTf,botSTf,epL,botSTL,startB,backMainSPB);
		spB.guiContainerVisible(false);
		spB.select();	
		
		mpB.scale().set(0.3f, 0.3f);
		mpB.position().set(0.4f, 0.3f);		
		mpB.container().attach(ipTf,portTf,portL,ipL,connectB,backMainMPB);
		mpB.guiContainerVisible(false);
		
		optB.scale().set(0.3f, 0.3f);
		optB.position().set(0.6f, 0.5f);
		optB.container().attach(backMainOPTB);
		optB.guiContainerVisible(false);
		
		quitB.scale().set(0.3f, 0.3f);
		quitB.position().set(0.8f, 0.7f);

		/*
		 * SINGLEPLAYER SETTINGS
		 */

		epTf.position().set(1f,0.8f);
		epTf.scale().set(0.9f,0.9f);
		
		botSTf.position().set(1f,1.4f);
		botSTf.scale().set(0.9f,0.9f);
		
		epL.position().set(0f, 0.8f);
		epL.scale().set(0.9f,0.9f);
		
		botSTL.position().set(0f, 1.4f);
		botSTL.scale().set(0.9f,0.9f);
		
		startB.position().set(1f, 2f);
		startB.scale().set(0.9f,0.9f);
		
		backMainSPB.position().set(0f,2f);
		backMainSPB.scale().set(0.9f,0.9f);
		
		
		/*
		 * MULTIPLAYER SETTINGS
		 */
		ipTf.position().set(0.5f,0.1f);	
		ipTf.scale().set(0.9f,0.9f);
		
		portTf.position().set(0.5f,0.7f);
		portTf.scale().set(0.9f,0.9f);
		
		ipL.position().set(-0.6f, 0.1f);
		ipL.scale().set(0.9f,0.9f);
		
		portL.position().set(-0.6f, 0.7f);
		portL.scale().set(0.9f,0.9f);
		
		connectB.position().set(0.5f, 1.3f);
		connectB.scale().set(0.9f,0.9f);
		
		backMainMPB.position().set(-0.6f,1.3f);
		backMainMPB.scale().set(0.9f,0.9f);
		
		/*
		 * OPTIONS SETTINGS
		 */
		backMainOPTB.scale().set(0.9f,0.9f);
		backMainOPTB.position().set(0f,0f);

		
		
		GraphicsEntity guiRoot = new GraphicsEntity();
		guiRoot.attach(spB,mpB,optB,quitB);
		
		scene.attach(guiRoot);
		processor.root(scene);
		processor.start(60);
	}

}

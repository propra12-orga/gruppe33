package propra2012.gruppe33.networktest;

import java.awt.Frame;
import java.util.Iterator;
import java.util.UUID;

import javax.swing.JOptionPane;

import propra2012.gruppe33.PreMilestoneApp;

import com.foxnet.rmi.pattern.change.AdminSessionServer;
import com.indyforge.twod.engine.graphics.GraphicsRoutines;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor.NetworkMode;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.AbstractEntityChange;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.SceneChange;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.util.NameFilter;
import com.indyforge.twod.engine.util.FilteredIterator;

public class ServerApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		// Create a new scene as server
		SceneProcessor serverProcessor = new SceneProcessor(NetworkMode.Server);
		serverProcessor.onlyRenderWithFocus(false);

		// Open the server on 1337
		serverProcessor.openServer(1337);

		// Create peer
		Frame frame = GraphicsRoutines.createFrame(serverProcessor,
				"Bomberman SERVER", 800, 600);

		Scene scene = PreMilestoneApp.createDemoGame();
		Iterator<Entity> filter = new FilteredIterator<Entity>(new NameFilter(
				"Kr0e"), scene.childIterator(true, true));
		final UUID regKey = filter.next().registrationKey();

		JOptionPane.showMessageDialog(frame, "Wollen Sie das Spiel starten ?");

		AdminSessionServer<SceneProcessor> server = serverProcessor
				.adminSessionServer();

		// Apply the scene change
		server.combined().applyChange(new SceneChange(scene));
		System.out.println("Send change");

		Thread.sleep(1000);

		AbstractEntityChange<GraphicsEntity> movePlayer3 = new AbstractEntityChange<GraphicsEntity>(
				regKey) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void apply(GraphicsEntity entity) {
				entity.position().x += 3;
			}
		};

		server.combined().applyChange(movePlayer3);

		while (!serverProcessor.isShutdownRequested()) {

			// Process the world (the main game-loop)
			serverProcessor.process(60);
		}

		// Destroy
		frame.dispose();

		// Shutdown complete servers
		serverProcessor.releaseNetworkResources();
	}
}

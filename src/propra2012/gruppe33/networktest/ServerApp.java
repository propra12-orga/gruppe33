package propra2012.gruppe33.networktest;

import java.awt.Frame;
import java.util.Iterator;
import java.util.UUID;

import javax.swing.JOptionPane;

import propra2012.gruppe33.PreMilestoneApp;

import com.foxnet.rmi.pattern.change.AdminSessionServer;
import com.foxnet.rmi.pattern.change.Change;
import com.indyforge.twod.engine.graphics.GraphicsRoutines;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor.NetworkMode;
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

		// Set root
		serverProcessor.root(PreMilestoneApp.createDemoGame());

		Iterator<Entity> filter = new FilteredIterator<Entity>(new NameFilter(
				"Kr0e"), serverProcessor.root().childIterator(true, true));
		final UUID regKey = filter.next().registryKey();

		JOptionPane.showMessageDialog(frame, "Wollen Sie das Spiel starten ?");

		AdminSessionServer<SceneProcessor> server = serverProcessor
				.adminSessionServer();

		final Scene ss = serverProcessor.root();

		server.broadcastChange(new Change<SceneProcessor>() {

			@Override
			public void apply(SceneProcessor ctx) {
				ctx.root(ss);
			}
		});

		Thread.sleep(1000);
		System.out.println("Send change");

		Change<SceneProcessor> movePlayer3 = new Change<SceneProcessor>() {

			@Override
			public void apply(SceneProcessor sceneProcessor) {
				((GraphicsEntity) sceneProcessor.root().registry().get(regKey))
						.position().x += 3;
			}
		};

		server.applyChange(movePlayer3);
		server.broadcastChange(movePlayer3);

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

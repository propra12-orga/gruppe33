package propra2012.gruppe33.networktest;

import java.awt.Frame;
import java.util.Iterator;
import java.util.UUID;

import javax.swing.JOptionPane;

import propra2012.gruppe33.PreMilestoneApp;

import com.foxnet.rmi.transport.network.ConnectionManager;
import com.indyforge.twod.engine.graphics.GraphicsRoutines;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Change;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.DefaultSessionServer;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.util.NameFilter;
import com.indyforge.twod.engine.util.FilteredIterator;

public class ServerApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		ConnectionManager servers = new ConnectionManager(true);

		DefaultSessionServer server = new DefaultSessionServer();
		servers.staticReg().bind("server", server);
		servers.openServer(1337);

		// Create a new scene as server
		SceneProcessor serverProcessor = new SceneProcessor(servers);

		// Create peer
		Frame frame = GraphicsRoutines.createFrame(serverProcessor,
				"Bomberman SERVER", 800, 600);

		// Set root
		serverProcessor.root(PreMilestoneApp.createDemoGame());
		serverProcessor.processTasks();

		Iterator<Entity> filter = new FilteredIterator<Entity>(new NameFilter(
				"Kr0e"), serverProcessor.root().childIterator(true, true));
		final UUID regKey = filter.next().registryKey();

		JOptionPane.showMessageDialog(frame, "Wollen Sie das Spiel starten ?");

		server.broadcastRoot(serverProcessor.root());

		Thread.sleep(1000);
		System.out.println("Send change");

		Change movePlayer3 = new Change() {

			@Override
			public void apply(SceneProcessor sceneProcessor) {
				((GraphicsEntity) sceneProcessor.root().registry().get(regKey))
						.position().x += 3;
			}
		};

		movePlayer3.apply(serverProcessor);
		server.broadcastChange(movePlayer3);

		while (!serverProcessor.isShutdownRequested()) {

			// Process the world (the main game-loop)
			serverProcessor.process(60);
		}

		// Destroy
		frame.dispose();

		// Shutdown complete servers
		servers.shudown();
	}
}

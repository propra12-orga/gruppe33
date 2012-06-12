package propra2012.gruppe33.networktest;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import propra2012.gruppe33.PreMilestoneApp;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.input.InputActivator;

import com.indyforge.foxnet.rmi.pattern.change.AdminSessionServer;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor.NetworkMode;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.scene.ResetNetworkTimeChange;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.scene.SceneChange;

public class ServerApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		// Create a new scene as server
		SceneProcessor serverProcessor = new SceneProcessor(NetworkMode.Server)
				.openServer(1337);

		// Scanner from console
		Scanner input = new Scanner(System.in);

		// Wait for two players
		while (true) {
			System.out.println("Started ?");
			input.next();

			synchronized (serverProcessor.adminSessionServer()) {
				if (serverProcessor.adminSessionServer().sessionCount() <= 0
						|| serverProcessor.adminSessionServer().sessionCount() > 4) {
					System.out.println("Session count not 1 - 4");

				} else {
					serverProcessor.adminSessionServer().acceptingSessions(
							false);
					break;
				}
			}
		}

		final long[] longs = new long[serverProcessor.adminSessionServer()
				.sessionCount()];
		int i = 0;
		for (long l : serverProcessor.adminSessionServer().sessions().keySet()) {
			longs[i++] = l;
		}

		final List<UUID> refs = new LinkedList<UUID>();
		Scene scene = PreMilestoneApp.createDemoGame(refs, longs);
		System.out.println(refs);

		AdminSessionServer<SceneProcessor> server = serverProcessor
				.adminSessionServer();

		// Apply the scene change
		server.composite().applyChange(new SceneChange(scene));

		// Enable input on all clients !!
		for (i = 0; i < longs.length; i++) {
			server.session(longs[i]).client()
					.applyChange(new InputActivator(refs.get(i)));
		}

		// Reset the network time every where
		server.composite().applyChange(new ResetNetworkTimeChange());

		System.out.println("Game started!");

		// Start the processor
		serverProcessor.start(60);
	}
}

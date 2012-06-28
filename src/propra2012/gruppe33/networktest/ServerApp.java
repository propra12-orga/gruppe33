package propra2012.gruppe33.networktest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

import propra2012.gruppe33.bomberman.Game;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor.NetworkMode;

public class ServerApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		// Create a new scene as server
		SceneProcessor serverProcessor = new SceneProcessor(NetworkMode.Server)
				.openServer(1337);

//		// Start broadcasting the address
//		serverProcessor.openBroadcaster(1338, new InetSocketAddress("kr0e-pc",
//				1337));

		// Wait for two players
		while (true) {
			System.out.println("Started ?");

			Thread.sleep(1000);

			synchronized (serverProcessor.adminSessionServer()) {
				if (serverProcessor.adminSessionServer().sessionCount() == 2) {
					serverProcessor.adminSessionServer().acceptingSessions(
							false);
					break;
				}
			}
		}

		new Game().serverGame(serverProcessor);

		serverProcessor.start(60);
	}
}

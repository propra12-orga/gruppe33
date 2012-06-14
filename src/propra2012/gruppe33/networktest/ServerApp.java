package propra2012.gruppe33.networktest;

import java.util.Scanner;

import propra2012.gruppe33.PreMilestoneApp;

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

		// for (int i = 0; i < 1000; i++) {
		// System.out.println(i);
		PreMilestoneApp.createServerGame(serverProcessor);
		// }

		serverProcessor.start(60);
	}
}

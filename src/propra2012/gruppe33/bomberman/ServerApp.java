package propra2012.gruppe33.bomberman;


import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor.NetworkMode;

public class ServerApp extends Thread {

	// The number of players
	private final int players;

	// Create a new scene as server
	private final SceneProcessor serverProcessor = new SceneProcessor(
			NetworkMode.Server).openServer(1337);

	public ServerApp(int players) {
		if (players <= 0) {
			throw new IllegalArgumentException("players must be >= 1");
		}
		this.players = players;
	}

	public int players() {
		return players;
	}

	public SceneProcessor processor() {
		return serverProcessor;
	}

	@Override
	public void run() {
		try {
			// Wait for two players
			while (true) {
				System.out.println("Started ?");
				Thread.sleep(1000);
				synchronized (serverProcessor.adminSessionServer()) {
					if (serverProcessor.adminSessionServer().sessionCount() == players) {
						serverProcessor.adminSessionServer().acceptingSessions(
								false);
						break;
					}
				}
			}

			// Recreate the server game!
			new Game().players(players).serverGame(serverProcessor);

			// Start the server game
			serverProcessor.start(50);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new ServerApp(2).start();
	}
}

package propra2012.gruppe33;

import java.awt.Point;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridConstants;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridLoader;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridRoutines;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.input.InputActivator;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.transform.DeltaPositionBroadcaster;

import com.indyforge.foxnet.rmi.pattern.change.AdminSessionServer;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Grid;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.scene.ResetNetworkTimeChange;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.scene.SceneChange;
import com.indyforge.twod.engine.graphics.sprite.Sprite;
import com.indyforge.twod.engine.resources.assets.AssetManager;

/**
 * 
 * @author Christopher Probst
 */
public class PreMilestoneApp {

	public static SceneProcessor createServerGame(SceneProcessor sceneProcessor)
			throws Exception {
		if (!sceneProcessor.hasAdminSessionServer()) {
			throw new IllegalArgumentException("The scene processor "
					+ "must a server");
		}

		AdminSessionServer<SceneProcessor> server = sceneProcessor
				.adminSessionServer();

		// Copy ids
		long[] longs = new long[server.sessionCount()];
		int i = 0;
		for (long l : server.sessionMap().keySet()) {
			longs[i++] = l;
		}

		List<UUID> refs = new LinkedList<UUID>();
		Scene scene = PreMilestoneApp.createDemoGame(refs, longs);
		System.out.println(refs);

		// Apply the scene change
		server.composite().applyChange(new SceneChange(scene));

		// Enable input on all clients !!
		for (i = 0; i < longs.length; i++) {
			InputActivator inputAc = new InputActivator();
			inputAc.entities().add(refs.get(i));
			server.session(longs[i]).client().applyChange(inputAc);
		}

		// Reset the network time every where
		server.composite().applyChange(new ResetNetworkTimeChange());

		return sceneProcessor;
	}

	public static Scene createDemoGame(List<UUID> refs, long... ids)
			throws Exception {

		if (ids.length > 4) {
			throw new IllegalArgumentException("Too many players");
		}

		// Delete old refs...
		refs.clear();

		// Create new asset manager using the given zip file
		AssetManager assets = new AssetManager(new File("scenes/default.zip"));

		// Create new scene with the given assets
		Scene scene = new Scene(assets, 1024, 1024);

		// Load the char array
		char[][] map = assets.loadAsset("assets/maps/smallmap2.txt",
				GridLoader.LOADER, true).get();

		// Generate the map randomally
		GridLoader.generate(map, System.nanoTime());

		// Put the new sound
		scene.soundManager().putSound(GridConstants.EXP_SOUND_NAME,
				"assets/sounds/exp.wav");

		/*
		 * Register global resources.
		 */
		scene.addProp(GridConstants.BOMB_IMAGE,
				assets.loadImage("assets/images/bomb.png", true));
		scene.addProp(
				GridConstants.EXP_SPRITE,
				new Sprite(assets.loadImage("assets/images/animated/boom.png",
						true), 5, 5));

		// Parse and setup map
		GraphicsEntity grid = GridLoader.parse(map, scene);

		// The spawn points
		List<Point> spawnPoints = GridLoader.find(map, GridConstants.SPAWN);

		int i = 0;
		for (long id : ids) {
			// Create new player as knight
			GraphicsEntity player = GridRoutines.createLocalKnight(assets,
					"Session-" + id);

			refs.add(player.registrationKey());

			scene.prop(GridConstants.BROADCASTER_NAME,
					DeltaPositionBroadcaster.class)
					.entities()
					.put(player.registrationKey(),
							new Vector2f(spawnPoints.get(i)));

			// Place to spawn
			grid.childAt(grid.typeProp(Grid.class).index(spawnPoints.get(i++)))
					.attach(player);

		}

		scene.addProp("players", new LinkedList<UUID>(refs));
		scene.addProp("sessions", ids);

		return scene;
	}
}

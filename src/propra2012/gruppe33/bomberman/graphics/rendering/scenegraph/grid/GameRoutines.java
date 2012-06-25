package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid;

import java.awt.Point;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.transform.DeltaPositionBroadcaster;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Grid;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;
import com.indyforge.twod.engine.graphics.sprite.Sprite;
import com.indyforge.twod.engine.resources.assets.AssetManager;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class GameRoutines implements GridConstants {

	public static Scene serverGame(List<UUID> refs, float broadcastingTime,
			String assetBundle, String mapAssetPath, String mapPropAssetPath, int width, int height,
			long... ids) throws Exception {

		// Delete old refs...
		refs.clear();

		// Create new asset manager using the given zip file
		AssetManager assets = new AssetManager(new File(assetBundle));

		// Create new scene with the given assets
		Scene scene = new Scene(assets, width, height);

		// Use property file
		Properties properties = new Properties();
		
		// Try to load the given prop file
		properties.load(assets.open(mapPropAssetPath));
		
		// Load the char array
		char[][] map = assets.loadAsset(mapAssetPath, GridLoader.LOADER, true)
				.get();

		// Lookup max spawn points
		int maxSpawns = GridLoader.find(map, SPAWN).size();

		/*
		 * Compare max spawn points with the player count.
		 */
		if (ids.length > maxSpawns) {
			throw new IllegalArgumentException("Too many players");
		}

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
		GraphicsEntity grid = GridLoader.parse(map, scene, broadcastingTime);

		// The spawn points
		List<Point> spawnPoints = GridLoader.find(map, GridConstants.SPAWN);

		int i = 0;
		for (long id : ids) {
			// Create new player as knight
			GraphicsEntity player = GridRoutines.createRemoteDwarf(assets,
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

	// Should not be instantiated...
	private GameRoutines() {
	}
}

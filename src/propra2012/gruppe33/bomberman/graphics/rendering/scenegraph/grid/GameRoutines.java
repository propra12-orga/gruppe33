package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid;

import java.awt.Point;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

	public enum Char {
		Dwarf, Santa, Knight, Wizard
	}

	public static Scene serverGame(List<UUID> refs, float broadcastingTime,
			String assetBundle, String mapAssetPath, String mapPropAssetPath,
			Map<Long, Char> playerIds) throws Exception {

		// Delete old refs...
		refs.clear();

		// Create new asset manager using the given zip file
		AssetManager assets = new AssetManager(new File(assetBundle));

		// Use property file
		Properties properties = new Properties();

		// Try to load the given prop file
		properties.load(assets.open(mapPropAssetPath));

		// Parse the width and height
		int width = Integer.parseInt(properties.getProperty(MAP_WIDTH_PROP));
		int height = Integer.parseInt(properties.getProperty(MAP_HEIGHT_PROP));

		// Create new scene with the given assets
		Scene scene = new Scene(assets, width, height);

		// Load the char array
		char[][] map = assets.loadAsset(mapAssetPath, GridLoader.LOADER, true)
				.get();

		// Lookup max spawn points
		int maxSpawns = GridLoader.find(map, SPAWN).size();

		/*
		 * Compare max spawn points with the player count.
		 */
		if (playerIds.size() > maxSpawns) {
			throw new IllegalArgumentException("Too many players");
		}

		// Generate the map randomally
		GridLoader.generate(map, System.nanoTime());

		// Put the new sound
		scene.soundManager().putSound(EXP_SOUND_NAME,
				properties.getProperty(EXP_SOUND_NAME));

		// Register the global bomb image
		scene.addProp(BOMB_IMAGE,
				assets.loadImage(properties.getProperty(BOMB_IMAGE), true));

		// Parse the width and height of the sprite
		int spriteWidth = Integer.parseInt(properties
				.getProperty(EXP_SPRITE_WIDTH));
		int spriteHeight = Integer.parseInt(properties
				.getProperty(EXP_SPRITE_HEIGHT));

		// Register the global explosion content
		scene.addProp(
				EXP_SPRITE,
				new Sprite(assets.loadImage(properties.getProperty(EXP_SPRITE),
						true), spriteWidth, spriteHeight));

		// Parse and setup map
		GraphicsEntity grid = GridLoader.parse(map, scene, broadcastingTime);

		// The spawn points
		List<Point> spawnPoints = GridLoader.find(map, GridConstants.SPAWN);

		int i = 0;
		
		/*
		 * For all requested players.
		 */
		for (Entry<Long, Char> entry : playerIds.entrySet()) {

			// Create new player as knight
			GraphicsEntity player = GridRoutines.createRemoteDwarf(assets,
					"Session-" + id);

//			
//			switch(entry.getValue()) {
//			case Dwarf: break;
//			case Dwarf: break;
//			case Dwarf: break;
//			case Dwarf: break;
//			}

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

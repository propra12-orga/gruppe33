package propra2012.gruppe33.bomberman;

import java.awt.Point;
import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridLoader;
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
 * 
 */
public final class Game implements GameConstants, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum Char {
		Dwarf, Santa, Knight, Wizard
	}

	/*
	 * 
	 */
	private String assetBundle = "res/default.zip",
			mapPropAssetPath = "assets/maps/smallmap.prop";
	private float broadcastUpdateTime = 0.015f;

	public String assetBundle() {
		return assetBundle;
	}

	public Game assetBundle(String assetBundle) {
		this.assetBundle = assetBundle;
		return this;
	}

	public String mapPropAssetPath() {
		return mapPropAssetPath;
	}

	public Game mapPropAssetPath(String mapPropAssetPath) {
		this.mapPropAssetPath = mapPropAssetPath;
		return this;
	}

	/**
	 * @return the broadcastUpdateTime
	 */
	public float getBroadcastUpdateTime() {
		return broadcastUpdateTime;
	}

	/**
	 * @param broadcastUpdateTime
	 *            the broadcastUpdateTime to set
	 */
	public void setBroadcastUpdateTime(float broadcastUpdateTime) {
		this.broadcastUpdateTime = broadcastUpdateTime;
	}

	public SceneProcessor serverGame(SceneProcessor sceneProcessor)
			throws Exception {
		if (sceneProcessor == null) {
			throw new NullPointerException("sceneProcessor");
		}
		return serverGame(sceneProcessor, assetBundle, mapPropAssetPath,
				broadcastUpdateTime);
	}

	public SceneProcessor serverGame(SceneProcessor sceneProcessor,
			String assetBundle, String mapPropAssetPath,
			float broadcastUpdateTime) throws Exception {
		if (sceneProcessor == null) {
			throw new NullPointerException("sceneProcessor");
		}

		// Check network mode
		if (!sceneProcessor.hasAdminSessionServer()) {
			throw new IllegalArgumentException("The scene processor "
					+ "must a server");
		}

		// Get the server interface
		AdminSessionServer<SceneProcessor> server = sceneProcessor
				.adminSessionServer();

		// Here we put the choosen chars
		Map<Long, Char> charMap = new HashMap<Long, Char>();

		// Copy ids with chars
		for (long id : server.sessionMap().keySet()) {
			charMap.put(id, Char.Santa);
		}

		// The player refs will be stored here
		List<UUID> refs = new LinkedList<UUID>();

		/*
		 * Create a new server game scene!
		 */
		Scene scene = serverGameScene(refs, broadcastUpdateTime, assetBundle,
				mapPropAssetPath, charMap);

		// Apply the scene change
		server.composite().applyChange(new SceneChange(scene));

		// Enable input on all clients !!
		int ptr = 0;
		for (Long id : charMap.keySet()) {

			// Create a new input activator
			InputActivator inputAc = new InputActivator();

			// Use linear reference
			inputAc.entities().add(refs.get(ptr++));

			// Activate the client
			server.session(id).client().applyChange(inputAc);
		}

		// Reset the network time every where
		server.composite().applyChange(new ResetNetworkTimeChange());

		return sceneProcessor;
	}

	public Scene serverGameScene(List<UUID> refs, float broadcastUpdateTime,
			String assetBundle, String mapPropAssetPath,
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
		char[][] map = assets.loadAsset(properties.getProperty(MAP_NAME_KEY),
				GridLoader.LOADER, true).get();

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

		// Register the global bomb images
		scene.addProp(DEFAULT_BOMB_IMAGE, assets.loadImage(
				properties.getProperty(DEFAULT_BOMB_IMAGE), true));
		scene.addProp(DEFAULT_BOMB_BAG_IMAGE, assets.loadImage(
				properties.getProperty(DEFAULT_BOMB_BAG_IMAGE), true));

		scene.addProp(NUKE_BOMB_IMAGE,
				assets.loadImage(properties.getProperty(NUKE_BOMB_IMAGE), true));
		scene.addProp(NUKE_BOMB_BAG_IMAGE, assets.loadImage(
				properties.getProperty(NUKE_BOMB_BAG_IMAGE), true));

		scene.addProp(FAST_BOMB_IMAGE,
				assets.loadImage(properties.getProperty(FAST_BOMB_IMAGE), true));
		scene.addProp(FAST_BOMB_BAG_IMAGE, assets.loadImage(
				properties.getProperty(FAST_BOMB_BAG_IMAGE), true));

		scene.addProp(PALISADE_HORI_IMAGE, assets.loadImage(
				properties.getProperty(PALISADE_HORI_IMAGE), true));
		scene.addProp(PALISADE_VERT_IMAGE, assets.loadImage(
				properties.getProperty(PALISADE_VERT_IMAGE), true));
		scene.addProp(PALISADE_BAG_IMAGE, assets.loadImage(
				properties.getProperty(PALISADE_BAG_IMAGE), true));

		scene.addProp(SHIELD_IMAGE,
				assets.loadImage(properties.getProperty(SHIELD_IMAGE), true));
		scene.addProp(SHIELD_POTION_IMAGE, assets.loadImage(
				properties.getProperty(SHIELD_POTION_IMAGE), true));

		scene.addProp(SLOW_SHROOM_IMAGE, assets.loadImage(
				properties.getProperty(SLOW_SHROOM_IMAGE), true));
		scene.addProp(FAST_SHROOM_IMAGE, assets.loadImage(
				properties.getProperty(FAST_SHROOM_IMAGE), true));

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
		GraphicsEntity grid = GridLoader.parse(map, scene, broadcastUpdateTime,
				System.nanoTime(), 0.2f, 0.2f, 0.2f, 0.1f, 0.1f, 0.1f, 0.1f);

		// The spawn points
		List<Point> spawnPoints = GridLoader.find(map, GameConstants.SPAWN);

		int i = 0;

		/*
		 * For all requested players.
		 */
		for (Entry<Long, Char> entry : playerIds.entrySet()) {

			// Create new player as knight
			GraphicsEntity player = null;

			/*
			 * Create new char.
			 */
			switch (entry.getValue()) {
			case Dwarf:
				player = GameRoutines.createRemoteDwarf(assets,
						String.valueOf(entry.getKey()));
				break;
			case Knight:
				player = GameRoutines.createRemoteKnight(assets,
						String.valueOf(entry.getKey()));
				break;
			case Santa:
				player = GameRoutines.createRemoteSanta(assets,
						String.valueOf(entry.getKey()));
				break;
			case Wizard:
				player = GameRoutines.createRemoteWizard(assets,
						String.valueOf(entry.getKey()));
				break;
			default:
				throw new IllegalArgumentException("Unknown char");
			}

			// Add the registration key to list
			refs.add(player.registrationKey());

			/*
			 * Register to delta position broadcaster.
			 */
			scene.prop(GameConstants.BROADCASTER_NAME,
					DeltaPositionBroadcaster.class)
					.entities()
					.put(player.registrationKey(),
							new Vector2f(spawnPoints.get(i)));

			// Place to spawn
			grid.childAt(grid.typeProp(Grid.class).index(spawnPoints.get(i++)))
					.attach(player);

		}

		// Store the player refs
		scene.addProp(PLAYERS_KEY, new LinkedList<UUID>(refs));

		// Store the ids + chars
		scene.addProp(SESSIONS_KEY, playerIds);

		// Add the game creator
		scene.addTypeProp(this);

		return scene;
	}
}

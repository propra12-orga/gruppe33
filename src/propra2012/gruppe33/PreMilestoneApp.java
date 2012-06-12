package propra2012.gruppe33;

import java.awt.Point;
import java.io.File;
import java.util.List;
import java.util.UUID;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridConstants;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridLoader;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridRoutines;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Grid;
import com.indyforge.twod.engine.graphics.sprite.Sprite;
import com.indyforge.twod.engine.resources.assets.AssetManager;

/**
 * 
 * @author Christopher Probst
 */
public class PreMilestoneApp {

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
		char[][] map = assets.loadAsset("assets/maps/smallmap.txt",
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
		final GraphicsEntity grid = GridLoader.parse(map, scene);

		// The spawn points
		Point[] points = new Point[] { new Point(1, 1), new Point(9, 1),
				new Point(9, 9), new Point(1, 9) };

		int i = 0;
		for (long id : ids) {
			// Create new player as knight
			GraphicsEntity player = GridRoutines.createLocalKnight(assets,
					"Session-" + id);

			refs.add(player.registrationKey());

			// Place to spawn
			grid.childAt(grid.typeProp(Grid.class).index(points[i++])).attach(
					player);

		}

		return scene;
	}
}

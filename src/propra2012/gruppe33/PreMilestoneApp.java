package propra2012.gruppe33;

import java.io.File;
import java.util.List;
import java.util.UUID;

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

		// Create new asset manager using the given zip file
		AssetManager assets = new AssetManager(new File("scenes/default.zip"));

		// Create new scene with the given assets
		Scene scene = new Scene(assets, 1024, 1024);

		// Load the char array
		char[][] map = assets.loadAsset("assets/maps/smallmap.txt",
				GridLoader.LOADER, true).get();

		// Generate the map randomally
		// GridLoader.generate(map, System.nanoTime());

		// Put the new sound
		scene.soundManager().putSound("exp", "assets/sounds/exp.wav");

		// Load boom
		final Sprite boom = new Sprite(assets.loadImage(
				"assets/images/animated/boom.png", true), 5, 5);

		// Parse and setup map
		final GraphicsEntity grid = GridLoader.parse(map, scene);

		int i = 0;
		for (long id : ids) {
			// Create new player as knight
			GraphicsEntity player = GridRoutines.createLocalKnight(assets, ""
					+ id);

			refs.add(player.registrationKey());

			// Place to spawn
			grid.childAt(grid.typeProp(Grid.class).index(1 + i++, 1)).attach(
					player);

		}

		// player.attach(new Entity() {
		//
		// @Override
		// protected void onUpdate(float tpf) {
		// super.onUpdate(tpf);
		//
		// // Get scene
		// Scene scene = ((GraphicsEntity) parent()).findScene();
		//
		// GraphicsEntity node = ((GraphicsEntity) parent().parent());
		//
		// if (scene.isPressed(KeyEvent.VK_SPACE)
		// && !GridRoutines.hasFieldExplosion(node)) {
		//
		// GraphicsEntity bomb = GridRoutines
		// .createExplosion(boom, 33);
		//
		// scene.soundManager().playSound("exp", true);
		//
		// // Attach the bomb
		// grid.childAt(
		// grid.typeProp(Grid.class).index(node.position()))
		// .attach(bomb);
		//
		// }
		// }
		// });

		return scene;
	}
}

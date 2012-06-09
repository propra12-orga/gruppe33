package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.RenderedImage;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Grid;
import com.indyforge.twod.engine.resources.assets.Asset;
import com.indyforge.twod.engine.resources.assets.AssetLoader;
import com.indyforge.twod.engine.resources.assets.AssetManager;

/**
 * This class loads the map for the Game and generates randomly destructible
 * blocks on it. Is able to throw IOExceptions.
 * 
 * @author Malte Schmidt
 */
public final class GridLoader implements GridConstants {

	/**
	 * Parses the given char array to setup the scene.
	 * 
	 * @param map
	 * @param scene
	 * @return
	 * @throws Exception
	 */
	public static GraphicsEntity parse(char[][] map, Scene scene)
			throws Exception {

		// Calc the dim
		int rx = map[0].length, ry = map.length;

		// Create a new grid entity
		GraphicsEntity gridEntity = GridRoutines.createGridEntity(rx, ry);

		// Add shift
		gridEntity.position().x += 0.5f;
		gridEntity.position().y += 0.5f;

		// Lookup the grid
		Grid grid = gridEntity.typeProp(Grid.class);

		// Create a new grid holder
		GraphicsEntity gridHolder = new GraphicsEntity();

		// Set correct scale
		gridHolder.scale(scene.sizeAsVector().scale(
				grid.sizeAsVector().invertLocal()));

		// Attach grid to holder
		gridHolder.attach(gridEntity);

		/*
		 * The breakable image.
		 */
		Asset<BufferedImage> breakable = scene.assetManager().loadImage(
				"assets/images/break.png", true);

		/*
		 * The solid image.
		 */
		Asset<BufferedImage> solidImage = scene.assetManager().loadImage(
				"assets/images/solid.png", true);

		/*
		 * The ground image.
		 */
		Asset<BufferedImage> groundImage = scene.assetManager().loadImage(
				"assets/images/ground.jpg", true);

		/*
		 * Load all components!
		 */
		Asset<BufferedImage> wallUP = scene.assetManager().loadImage(
				"assets/images/walls/wallUP.png", true);
		Asset<BufferedImage> wallDOWN = scene.assetManager().loadImage(
				"assets/images/walls/wallDOWN.png", true);
		Asset<BufferedImage> wallLEFT = scene.assetManager().loadImage(
				"assets/images/walls/wallLEFT.png", true);
		Asset<BufferedImage> wallRIGHT = scene.assetManager().loadImage(
				"assets/images/walls/wallRIGHT.png", true);
		Asset<BufferedImage> ulc = scene.assetManager().loadImage(
				"assets/images/walls/cornerLU.png", true);
		Asset<BufferedImage> urc = scene.assetManager().loadImage(
				"assets/images/walls/cornerRU.png", true);
		Asset<BufferedImage> dlc = scene.assetManager().loadImage(
				"assets/images/walls/cornerLD.png", true);
		Asset<BufferedImage> drc = scene.assetManager().loadImage(
				"assets/images/walls/cornerRD.png", true);

		// The ground
		GraphicsEntity ground = new GraphicsEntity();

		// The barriers
		GraphicsEntity solids = new GraphicsEntity();

		for (int y = 0; y < ry; y++) {
			for (int x = 0; x < rx; x++) {

				/*
				 * PARSE FIELD!
				 */

				// Create and add tile
				RenderedImage tile = new RenderedImage(groundImage);
				tile.centered(false).position().set(x, y);
				ground.attach(tile);

				// Tmp barrier
				RenderedImage solid = null;

				// Switch
				switch (map[y][x]) {
				case DOWN_BARRIER:
					solid = new RenderedImage(wallDOWN);
					break;
				case UP_BARRIER:
					solid = new RenderedImage(wallUP);
					break;
				case RIGHT_BARRIER:
					solid = new RenderedImage(wallRIGHT);
					break;
				case LEFT_BARRIER:
					solid = new RenderedImage(wallLEFT);
					break;
				case DOWN_LEFT_CORNER:
					solid = new RenderedImage(dlc);
					break;
				case DOWN_RIGHT_CORNER:
					solid = new RenderedImage(drc);
					break;
				case UP_LEFT_CORNER:
					solid = new RenderedImage(ulc);
					break;
				case UP_RIGHT_CORNER:
					solid = new RenderedImage(urc);
					break;
				case SOLID:
					solid = new RenderedImage(solidImage);
					break;
				}

				// Add barrier if valid
				if (solid != null) {
					solid.position().set(x, y);
					solid.centered(false);
					solids.attach(solid);

				} else {
					// Get the field entity
					Entity fieldNode = gridEntity.childAt(grid.index(x, y));

					// Add speed float prop
					fieldNode.addTypeProp(3f);

					if (map[y][x] >= BREAKABLE_OFFSET) {
						// Not solid ? Well, maybe a breakable component ?
						fieldNode.attach(new RenderedImage(breakable)
								.centered(true));
					}
				}
			}
		}

		// Create a static root
		GraphicsEntity staticRoot = new GraphicsEntity();
		staticRoot.attach(ground);
		staticRoot.attach(solids);
		staticRoot.scale().set(gridHolder.scale());

		// Merge to rendered entity
		scene.attach(scene.renderedOpaqueEntity(Color.white, staticRoot));

		// Attach the grid holder
		scene.attach(gridHolder);

		return gridEntity;
	}

	/*
	 * The grid loader.
	 */
	public static final AssetLoader<char[][]> LOADER = new AssetLoader<char[][]>() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public char[][] loadAsset(AssetManager assetManager, String assetPath)
				throws Exception {
			return GridLoader.load(assetManager.open(assetPath));
		}
	};

	/**
	 * Berechnet die Map aus der Textdatei und gibt ein Array mit dem Inhalt der
	 * Map zurueck. Wirft IOException, wenn die Karte nicht geladen werden kann,
	 * z.B. wenn die File nicht geladen werden kann.
	 * 
	 * @param input
	 *            the stream which loads the map
	 * @return a 2D char array with the loaded map.
	 * @throws IOException
	 *             If the map is empty or not compatible (g.E. if the length of
	 *             the strings differs from each other)
	 */
	public static char[][] load(InputStream input) throws IOException {
		if (input == null) {
			throw new NullPointerException("input");
		}

		// Eine Zeile der Karte.
		String line;

		// Die map
		char[][] map;

		// Liste in der Zwischengespeichert wird
		List<String> cache = new LinkedList<String>();

		// Laden des files
		BufferedReader br = new BufferedReader(new InputStreamReader(input));

		try {
			// Lies Zeile fuer Zeile
			while ((line = br.readLine()) != null) {
				cache.add(line);
			}

			// Nichts zu lesen
			if (cache.isEmpty()) {
				throw new IOException("Empty grid");
			}
		} finally {
			// Schliesse den Stream
			br.close();
		}

		// Neues array erstellen
		map = new char[cache.size()][];

		// Alles in das char array kopieren
		int i = 0, len = -1;
		for (String row : cache) {
			if (len == -1) {
				len = row.length();
			} else if (len != row.length()) {
				throw new IOException("Zeilen nicht alle gleich lang!");
			}

			map[i++] = row.toCharArray();
		}
		return map;
	}

	// Muss nicht erstellbar sein.
	private GridLoader() {
	}

	/**
	 * Generates random destructible blocks through a seed. Gets the map as a 2D
	 * char array and the seed to create the map.
	 * 
	 * @param map
	 *            the array/map
	 * @param seed
	 *            the needed seed to create the destructible blocks
	 * @return the map with the changed values for destructible blocks
	 */
	public static char[][] generate(char[][] map, long seed) {
		// int blockcount = Math.round((map.length * map[0].length) * 0.8f);
		Random ran = new Random(seed);
		for (int y = 1; y < map.length - 1; y++) {
			for (int x = 1; x < map[0].length - 1; x++) {
				if (!nextTo(map, x, y, START) && map[y][x] != SOLID
						&& map[y][x] != START) {
					if (ran.nextInt(10 - nextToCount(map, x, y)) > 2) {
						map[y][x] += BREAKABLE_OFFSET;
					}
				}
			}
		}

		return map;
	}

	/**
	 * Get's one field of an array and controls whether their is a field of the
	 * expected type next to it.
	 * 
	 * @param map
	 *            the array
	 * @param x
	 *            x coordinate of the field
	 * @param y
	 *            y coordinate of the field
	 * @param typ
	 *            the expected type
	 * @return true, for their is a block next to it and false, for their is no
	 *         block of teh expected typ
	 */
	private static boolean nextTo(char[][] map, int x, int y, char typ) {
		if (map[y - 1][x] == typ) {
			return true;
		} else if (map[y][x + 1] == typ) {
			return true;
		} else if (map[y + 1][x] == typ) {
			return true;
		} else if (map[y][x - 1] == typ) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Get's one field of an array and counts the number of destructible blocks
	 * around it.
	 * 
	 * @param map
	 *            the array
	 * @param x
	 *            x coordinate of the field
	 * @param y
	 *            y coordinate of the field
	 * @return the number of destructible blocks around the field.
	 */
	private static int nextToCount(char[][] map, int x, int y) {
		int count = 0;
		if (map[y - 1][x] >= BREAKABLE_OFFSET) {
			count++;
		}
		if (map[y][x + 1] >= BREAKABLE_OFFSET) {
			count++;
		}
		if (map[y + 1][x] >= BREAKABLE_OFFSET) {
			count++;
		}
		if (map[y][x - 1] >= BREAKABLE_OFFSET) {
			count++;
		}
		return count;
	}
}

package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.GraphicsEntity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.RenderedImage;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Scene;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.math.Grid;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.math.Vector2f;
import propra2012.gruppe33.engine.resources.assets.Asset;
import propra2012.gruppe33.engine.resources.assets.AssetLoader;
import propra2012.gruppe33.engine.resources.assets.AssetManager;

/**
 * Diese Klasse laedt aus einer Textdatei Zeilenweise die Karte aus. Kann
 * IOExceptions werfen.
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

		// Load grid from file
		GraphicsEntity gridEntity = new GraphicsEntity();

		// Calc
		int rx = map[0].length, ry = map.length;

		// Create grid
		final Grid grid = new Grid(rx, ry);

		// Set correct scale
		gridEntity.scale(scene.sizeAsVector().scale(
				new Vector2f(rx, ry).invertLocal()));

		// Set grid
		gridEntity.addProp("grid", grid);

		// Attach all nodes
		for (int y = 0; y < ry; y++) {
			for (int x = 0; x < rx; x++) {
				// Create a new node
				GraphicsEntity node = new GraphicsEntity() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					/*
					 * (non-Javadoc)
					 * 
					 * @see
					 * propra2012.gruppe33.engine.graphics.rendering.scenegraph
					 * .Entity#onUpdate(float)
					 */
					@Override
					protected void onUpdate(float tpf) {
						super.onUpdate(tpf);

						// Iterate over all children
						for (Entity child : this) {

							if (child instanceof GraphicsEntity) {
								// Convert
								GraphicsEntity graphicsChild = (GraphicsEntity) child;

								// Get point
								Point a = grid.point(cacheIndex()), b = graphicsChild
										.position().round().point();

								Point old = new Point(a);
								
								// Check coords
								if (!b.equals(new Point(0, 0))) {
									// Move a!
									a.translate(b.x, b.y);

									// New point valid ??
									if (grid.inside(a)) {
										// Lookup other child
										Entity otherChild = parent().children()
												.get(grid.index(a));
										
										// Attach to it
										otherChild.attach(child);

										// Change position
										graphicsChild.position().subLocal(new Vector2f(b));
										
										System.out.println("Reattching "
												+ child + " from " + old + " to " + a);
									} else {
										System.out.println(child
												+ " moved out of grid: " + a);
									}
								}
							}
						}
					}
				};

				// Set position on grid
				node.position().set(x + 0.5f, y + 0.5f);

				// Not visible...
				node.visible(false);

				// Attach the node to the grid
				gridEntity.attach(node);
			}
		}

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

		for (int y = 0; y < map.length; y++) {
			for (int x = 0; x < map[y].length; x++) {

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

				}

				// else if (map[y][x] >= BREAKABLE_OFFSET) {
				/*
				 * Not solid ? Well, maybe a breakable component ?
				 */
				// RenderedImage breakableImg = new
				// RenderedImage(breakable);
				// breakableImg.position().set(x, y);
				//
				// // Finally attach to grid
				// grid.attach(breakableImg.centered(false).attach(
				// new GridPositionUpdater()));
				// }
			}
		}

		// Create a static root
		GraphicsEntity staticRoot = new GraphicsEntity();
		staticRoot.attach(ground);
		staticRoot.attach(solids);
		staticRoot.scale().set(gridEntity.scale());

		// Merge to rendered entity
		scene.attach(scene.renderedOpaqueEntity(Color.white, staticRoot));

		// Attach the grid
		scene.attach(gridEntity);

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
	 *            Die zu ladene Map.
	 * @return Gibt ein Char-array zurueck mit den geladenen Werten der Map.
	 * @throws IOException
	 *             Wenn zu ladene Karte leer oder nicht kompatibel (z.B. Wenn
	 *             die Strings unterschiedlich lang sind.
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

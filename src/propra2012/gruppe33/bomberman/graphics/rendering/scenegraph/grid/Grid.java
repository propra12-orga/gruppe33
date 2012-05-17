package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Scene;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.image.ImageController;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.math.Vector2f;
import propra2012.gruppe33.engine.resources.Resource;
import propra2012.gruppe33.engine.resources.TransientRenderedEntity;
import propra2012.gruppe33.engine.resources.assets.AssetLoader;
import propra2012.gruppe33.engine.resources.assets.AssetManager;

/**
 * This class represents a grid scene. It contains a char[][] array and some
 * other features.
 * 
 * @author Christopher Probst
 * @author Malte Schmidt
 */
public class Grid extends Scene {

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
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Validates a map.
	 * 
	 * @param mapData
	 *            The char[][] map data you want to validate.
	 * @return the same array which was passed.
	 */
	public static char[][] validateMapData(char[][] mapData) {
		if (mapData == null) {
			throw new NullPointerException("mapData cannot be null");
		} else if (mapData.length == 0) {
			throw new IllegalArgumentException(
					"mapData does not contain any rows");
		} else if (mapData[0].length == 0) {
			throw new IllegalArgumentException(
					"mapData does not contain any columns");
		}

		/*
		 * Since all rows must have the same length we can use the first length
		 * to compare the remaining rows. A row which has a different length
		 * will cause an error.
		 */
		int rowLength = mapData[0].length;

		// Check map data
		for (int i = 1; i < mapData.length; i++) {
			if (mapData[i].length != rowLength) {
				throw new IllegalArgumentException("Invalid map data. "
						+ "Every row must have the same length.");
			}
		}

		// Return the array...
		return mapData;
	}

	// The map data
	private final char[][] mapData;

	// The map raster
	private final int rasterX, rasterY;

	// The raster width & height
	private final float rasterWidth, rasterHeight;

	// Here we store all vectors of the world
	private final Vector2f[][] worldMap;

	// Here we store the max field velocities (Useful for areas where you can
	// only go slowly...)
	private final Map<Character, Float> maxFieldVelocities = new HashMap<Character, Float>();

	// Here we store default chars which do not affect line-of-sight
	private final Set<Character> defaultLineOfSightChars = new HashSet<Character>();

	// Here we store default chars which do not affect collection
	private final Set<Character> defaultCollectChars = new HashSet<Character>();

	/**
	 * Creates a new grid using the given parameters.
	 * 
	 * @param name
	 *            The name of the grid.
	 * @param assetManager
	 *            The asset manager of this grid. Cannot be null since the map
	 *            data is loaded directly from the manager.
	 * @param mapAssetPath
	 *            The map asset path used with the given asset manager.
	 * @param width
	 *            The width of the grid.
	 * @param height
	 *            The height of the grid.
	 * @throws Exception
	 *             If an exception occurs.
	 */
	public Grid(String name, AssetManager assetManager, String mapAssetPath,
			int width, int height) throws Exception {
		this(name, assetManager, assetManager.loadAsset(mapAssetPath, LOADER)
				.get(), width, height);
	}

	/**
	 * Creates a new grid using the given parameters.
	 * 
	 * @param name
	 *            The name of the grid.
	 * @param assetManager
	 *            The asset manager of this grid.
	 * @param mapData
	 *            The map data of the grid.
	 * @param width
	 *            The width of the grid.
	 * @param height
	 *            The height of the grid.
	 * @throws Exception
	 *             If an exception occurs.
	 */
	public Grid(String name, AssetManager assetManager, char[][] mapData,
			int width, int height) throws Exception {
		super(name, assetManager, width, height);

		if (mapData == null) {
			throw new NullPointerException("mapData");
		}

		// Validate the map data
		this.mapData = validateMapData(mapData);

		// Set raster x and y
		rasterY = mapData.length;
		rasterX = mapData[0].length;

		// Init the world vector map
		worldMap = new Vector2f[rasterY][rasterX];

		// Update the raster dimensions
		rasterWidth = width / (float) rasterX;
		rasterHeight = height / (float) rasterY;

		// Calc the world map
		for (int x = 0; x < rasterX; x++) {
			for (int y = 0; y < rasterY; y++) {
				worldMap[y][x] = gridToWorld(x, y);
			}
		}
	}

	/**
	 * Basically iterates over the map data and bundles proper chars which you
	 * can specify to images.
	 * 
	 * @param name
	 *            The name of the new root entity.
	 * @param chars2Images
	 *            The map where you can define which image belongs to which
	 *            char.
	 * @return an entity which contains all bundled images stored as child image
	 *         entities.
	 */
	public Entity bundle(String name,
			Map<Character, Resource<? extends Image>> chars2Images) {
		if (name == null) {
			throw new NullPointerException("name");
		} else if (chars2Images == null) {
			throw new NullPointerException("chars2Images");
		}

		// Create new root entity
		Entity root = new Entity(name);

		for (int x = 0; x < rasterX; x++) {
			for (int y = 0; y < rasterY; y++) {

				// Lookup tile image
				Resource<? extends Image> tile = chars2Images
						.get(mapData[y][x]);

				// If the char is valid...
				if (tile != null) {

					// Create child entity
					Entity child = new Entity(x + ", " + y);

					// Add to controllers
					child.putController(new ImageController(tile));

					// At first translate
					child.setPosition(vectorAt(x, y));

					// Scale
					child.getScale().set(rasterWidth, rasterHeight);

					// Attach to root
					root.attach(child);
				}
			}
		}

		return root;
	}

	/**
	 * @return the map which contains the max field velocities. Only chars
	 *         listed here represent valid fields on which you can move.
	 */
	public Map<Character, Float> getMaxFieldVelocities() {
		return maxFieldVelocities;
	}

	/**
	 * @return the default set of chars which do not affect line-of-sight.
	 */
	public Set<Character> getDefaultLineOfSightChars() {
		return defaultLineOfSightChars;
	}

	/**
	 * @return the default set of chars which do not affect collection.
	 */
	public Set<Character> getDefaultCollectChars() {
		return defaultCollectChars;
	}

	/**
	 * Basically iterates over the map data and bundles proper chars which you
	 * can specify to images and renders them into an image for fast rendering.
	 * 
	 * @param name
	 *            The name of the new root entity.
	 * @param chars2Images
	 *            The map where you can define which image belongs to which
	 *            char.
	 * @param transparency
	 *            The transparency level of the rendered image.
	 * @param background
	 *            The background color of the rendered image.
	 * @return an entity which contains a rendered image with all bundled
	 *         images.
	 */
	public Entity bundleToRenderedEntity(String name,
			Map<Character, Resource<? extends Image>> chars2Images,
			int transparency, Color background) {

		// Bundle to entity and store as rendered transient entity
		TransientRenderedEntity renderedEntity = new TransientRenderedEntity(
				this, transparency, background, bundle(name, chars2Images));

		// Create a new entity
		Entity entity = new Entity(name);

		// Put the rendered image into the entity
		entity.putController(new ImageController(renderedEntity));

		// Set position
		entity.getPosition().set(getWidth() * 0.5f, getHeight() * 0.5f);

		// Adjust scale
		entity.getScale().set(getWidth(), getHeight());

		return entity;
	}

	/**
	 * Translates grid coords to world coords.
	 * 
	 * @param location
	 *            The location in the array.
	 * @return a new vector containing the world coords.
	 */
	public Vector2f gridToWorld(Point location) {
		if (location == null) {
			throw new NullPointerException("location");
		}

		return gridToWorld(location.x, location.y);
	}

	/**
	 * Translates grid coords to world coords.
	 * 
	 * @param x
	 *            The column in the array.
	 * @param y
	 *            The row in the array.
	 * @return a new vector containing the world coords.
	 */
	public Vector2f gridToWorld(int x, int y) {
		return new Vector2f(rasterWidth * (x + 0.5f), rasterHeight * (y + 0.5f));
	}

	/**
	 * Translates world coords to the nearest point.
	 * 
	 * @param position
	 *            The world position you want to translate.
	 * @return a new point containing the nearest grid coords.
	 */
	public Point worldToNearestPoint(Vector2f position) {
		if (position == null) {
			throw new NullPointerException("position");
		}

		// Calc new x and y
		float x = (position.x - rasterWidth * 0.5f) / rasterWidth, y = (position.y - rasterHeight * 0.5f)
				/ rasterHeight;

		// Round and return new point
		return new Point(Math.round(x), Math.round(y));
	}

	/**
	 * Validates a location against the valid range.
	 * 
	 * @param location
	 *            The location you want to validate.
	 * @return the same location if location is valid, otherwise null.
	 */
	public Point validate(Point location) {
		if (location == null) {
			throw new NullPointerException("location");
		}

		return validate(location.x, location.y) ? location : null;
	}

	/**
	 * Validates a points against the valid range.
	 * 
	 * @param x
	 *            The x component you want to validate.
	 * @param y
	 *            The y component you want to validate.
	 * @return true if point is valid, otherwise false.
	 */
	public boolean validate(int x, int y) {
		return x < rasterX && x >= 0 && y < rasterY && y >= 0 ? true : false;
	}

	/**
	 * An enum which contains all four directions.
	 * 
	 * @author Christopher Probst
	 * 
	 */
	public enum Direction {
		North, South, East, West
	}

	/**
	 * This is one of the most important methods when dealing with movement. It
	 * calculates the line-of-sight using the given direction.
	 * 
	 * @param position
	 *            The world position.
	 * @param max
	 *            The max distance. Please note that the method runs faster if
	 *            you use a short distance since this value affects a loop.
	 * @param direction
	 *            The {@link Direction} of the test.
	 * @param validChars
	 *            A set of chars which will not affect the line-of-sight. If
	 *            null the default chars
	 *            {@link Grid#getDefaultLineOfSightChars()} are used.
	 * @return the line-of-sight in world space.
	 */
	public float lineOfSight(Vector2f position, float max, Direction direction,
			Set<Character> validChars) {

		if (validChars == null) {
			// Use default chars...
			validChars = defaultLineOfSightChars;
		}

		// Get the coords of the nearest point
		Point point = validate(worldToNearestPoint(position));

		// Position is outside the grid
		if (point == null) {
			throw new IllegalArgumentException("Position out of grid");
		}

		// Convert to world
		Vector2f nearest = vectorAt(point);

		// The return value and the increase value
		float distance, increase;

		// Used for check
		int offx, offy;

		switch (direction) {

		case North:
		case South:

			// Calc distance
			distance = direction == Direction.North ? position.y - nearest.y
					: nearest.y - position.y;

			// Setup the increase
			increase = rasterHeight;

			// Init
			offy = direction == Direction.North ? -1 : 1;
			offx = 0;

			break;

		case West:
		case East:

			// Calc distance
			distance = direction == Direction.West ? position.x - nearest.x
					: nearest.x - position.x;

			// Setup the increase
			increase = rasterWidth;

			// Init
			offy = 0;
			offx = direction == Direction.West ? -1 : 1;

			break;

		default:
			throw new IllegalArgumentException("Unknown enum type: "
					+ direction);
		}

		// Validate the new position and check for valid chars
		while (validate(point.x += offx, point.y += offy)
				&& validChars.contains(charAt(point)) && distance < max) {

			// Increase distance
			distance += increase;
		}

		// Limit the result
		if (distance > max) {
			distance = max;
		}

		return distance;
	}

	/**
	 * 
	 * @param location
	 *            The location in the array.
	 * @return the char at the specified location.
	 */
	public char charAt(Point location) {
		if (location == null) {
			throw new NullPointerException("location");
		}

		return charAt(location.x, location.y);
	}

	/**
	 * @param x
	 *            The column in the array.
	 * @param y
	 *            The row in the array.
	 * @return the char at the specified location.
	 */
	public char charAt(int x, int y) {
		return mapData[y][x];
	}

	/**
	 * @param location
	 *            The location in the array.
	 * @return the vector at the specified location.
	 */
	public Vector2f vectorAt(Point location) {
		if (location == null) {
			throw new NullPointerException("location");
		}
		return vectorAt(location.x, location.y);
	}

	/**
	 * @param x
	 *            The column in the array.
	 * @param y
	 *            The row in the array.
	 * @return the vector at the specified location.
	 */
	public Vector2f vectorAt(int x, int y) {
		return worldMap[y][x];
	}

	/**
	 * @param find
	 *            The char you want to find.
	 * @return a point containing the first location of the char.
	 */
	public Point find(char find) {
		return find(find, 0, 0);
	}

	/**
	 * 
	 * @param find
	 *            The char you want to find.
	 * @param after
	 *            The point after which the search should start.
	 * @return a point containg the first location of the char.
	 */
	public Point find(char find, Point after) {
		if (after == null) {
			throw new NullPointerException("after");
		}
		return find(find, after.x, after.y);
	}

	/**
	 * 
	 * @param find
	 *            The char you want to find.
	 * @param afterX
	 *            The column after which the search should start.
	 * @param afterY
	 *            The row after which the search should start.
	 * @return a point containg the first location of the char.
	 */
	public Point find(char find, int afterX, int afterY) {
		for (int x = afterX; x < rasterX; x++) {
			for (int y = afterY; y < rasterY; y++) {

				// Select and check for solid char
				if (charAt(x, y) == find) {
					return new Point(x, y);
				}
			}
		}
		return null;
	}

	/**
	 * @return the map data.
	 */
	public char[][] getMapData() {
		return mapData;
	}

	/**
	 * @return the world map.
	 */
	public Vector2f[][] getWorldMap() {
		return worldMap;
	}

	/**
	 * 
	 * @return the raster width.
	 */
	public float getRasterWidth() {
		return rasterWidth;
	}

	/**
	 * 
	 * @return the raster height.
	 */
	public float getRasterHeight() {
		return rasterHeight;
	}

	/**
	 * @return the rasterX.
	 */
	public int getRasterX() {
		return rasterX;
	}

	/**
	 * @return the rasterY.
	 */
	public int getRasterY() {
		return rasterY;
	}

	/**
	 * Returns all points which are affected by the given distance. The method
	 * will cast rays in all four direction and stops when hitting a char which
	 * is not in the given set.
	 * 
	 * @param location
	 *            The location of the center.
	 * @param distance
	 *            The max distance of collection.
	 * @param validChars
	 *            A set of chars which will not affect the collection. If null
	 *            the default chars {@link Grid#getDefaultCollectChars()} are
	 *            used.
	 * @return a list which contains all points.
	 */
	public List<Point> collectPoints(Point location, int distance,
			Set<Character> validChars) {
		return collectPoints(location.x, location.y, distance, validChars);
	}

	/**
	 * Returns all points which are affected by the given distance. The method
	 * will cast rays in all four direction and stops when hitting a char which
	 * is not in the given set.
	 * 
	 * @param x
	 *            The x center.
	 * @param y
	 *            The y center
	 * @param distance
	 *            The max distance of collection.
	 * @param validChars
	 *            A set of chars which will not affect the collection. If null
	 *            the default chars {@link Grid#getDefaultCollectChars()} are
	 *            used.
	 * @return a list which contains all points.
	 */
	public List<Point> collectPoints(int x, int y, int distance,
			Set<Character> validChars) {
		if (!validate(x, y)) {
			throw new IllegalArgumentException("Coords out of grid");
		}

		// Use default chars
		if (validChars == null) {
			validChars = defaultCollectChars;
		}

		// Create a new array list
		List<Point> points = new ArrayList<Point>();

		// Add local point
		points.add(new Point(x, y));

		// Tells whether or not the given dirs are valid
		boolean nv = true, sv = true, wv = true, ev = true;

		// Tmp var
		Point p;

		// Iterate
		for (int i = 1; i < distance; i++) {

			// North
			if ((p = validate(new Point(x, y - i))) != null
					&& validChars.contains(charAt(p)) && nv) {
				points.add(p);
			} else {
				nv = false;
			}

			// South
			if ((p = validate(new Point(x, y + i))) != null
					&& validChars.contains(charAt(p)) && sv) {
				points.add(p);
			} else {
				sv = false;
			}

			// West
			if ((p = validate(new Point(x - i, y))) != null
					&& validChars.contains(charAt(p)) && wv) {
				points.add(p);
			} else {
				wv = false;
			}

			// East
			if ((p = validate(new Point(x + i, y))) != null
					&& validChars.contains(charAt(p)) && ev) {
				points.add(p);
			} else {
				ev = false;
			}
		}

		return points;
	}
}

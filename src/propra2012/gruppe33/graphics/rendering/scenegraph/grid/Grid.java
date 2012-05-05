package propra2012.gruppe33.graphics.rendering.scenegraph.grid;

import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import propra2012.gruppe33.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.graphics.rendering.scenegraph.PictureController;
import propra2012.gruppe33.graphics.rendering.scenegraph.Scene;
import propra2012.gruppe33.graphics.rendering.scenegraph.Vector2f;

/**
 * This class represents a grid scene. It contains a char[][] array and some
 * other features.
 * 
 * @author Christopher Probst
 */
public class Grid extends Scene {

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

	/**
	 * Loads a grid.
	 * 
	 * @deprecated ONLY USED FOR TEST PURPOSES.
	 */
	public static char[][] loadGrid(String file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(
				new File(file)));
		try {
			List<String> lines = new LinkedList<String>();

			String line;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}

			char[][] arr = new char[lines.size()][lines.get(0).length()];
			int i = 0;
			for (String s : lines) {
				arr[i++] = s.toCharArray();
			}

			return arr;
		} finally {
			reader.close();
		}
	}

	/**
	 * Creates a compatible image for rendering the solid blocks.
	 * 
	 * @param width
	 *            The height of the image.
	 * @param height
	 *            The width of the image.
	 * @return a screen compatible image.
	 */
	public static BufferedImage createSolidBlockImage(int width, int height) {
		return GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getDefaultConfiguration()
				.createCompatibleImage(width, height, Transparency.BITMASK);
	}

	// The map data
	private final char[][] mapData;

	// The map raster
	private final int rasterX, rasterY;

	// The raster width & height
	private final float rasterWidth, rasterHeight;

	// Here we store all vectors of the world
	private final Vector2f[][] worldMap;

	/**
	 * Creates a new grid using the given parameters.
	 * 
	 * @param name
	 *            The name of the level.
	 * @param width
	 *            The width of the level viewport.
	 * @param height
	 *            The height of the level viewport.
	 * @param mapData
	 *            The char[][] array which contains the map data.
	 */
	public Grid(String name, int width, int height, char[][] mapData) {
		super(name, width, height);

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
	 * @return an entity which contains all bundled images stored as child
	 *         picture entities.
	 */
	public Entity bundle(String name, Map<Character, BufferedImage> chars2Images) {
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
				BufferedImage tile = chars2Images.get(mapData[y][x]);

				// If the char is valid...
				if (tile != null) {

					// Create child entity
					Entity child = new Entity(x + ", " + y);

					// Add to controllers
					child.putController(new PictureController(tile));

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
	 * Basically iterates over the map data and bundles proper chars which you
	 * can specify to images and renders them into a picture for fast rendering.
	 * 
	 * @param name
	 *            The name of the new root entity.
	 * @param chars2Images
	 *            The map where you can define which image belongs to which
	 *            char.
	 * @return a picture entity which contains a rendered image with all bundled
	 *         images.
	 */
	public Entity bundleAndRender(String name,
			Map<Character, BufferedImage> chars2Images) {

		// Create a new entity
		Entity entity = new Entity(name);

		// Render the entity to a picture component
		bundle(name, chars2Images).renderTo(entity, getWidth(), getHeight(),
				Transparency.BITMASK);

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
	 *            An array of chars which will not affect the line-of-sight.
	 * @return the line-of-sight in world space.
	 */
	public float lineOfSight(Vector2f position, float max, Direction direction,
			char... validChars) {
		Set<Character> set = new HashSet<Character>();
		for (char validChar : validChars) {
			set.add(validChar);
		}
		return lineOfSight(position, max, direction, set);
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
	 *            The set of chars which will not affect the line-of-sight.
	 * @return the line-of-sight in world space.
	 */
	public float lineOfSight(Vector2f position, float max, Direction direction,
			Set<Character> validChars) {

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
}

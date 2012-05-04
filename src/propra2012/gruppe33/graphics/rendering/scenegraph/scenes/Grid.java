package propra2012.gruppe33.graphics.rendering.scenegraph.scenes;

import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import propra2012.gruppe33.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.graphics.rendering.scenegraph.Picture;
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
	private float rasterWidth, rasterHeight;

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

		// Update the raster dimensions
		rasterWidth = width / (float) rasterX;
		rasterHeight = height / (float) rasterY;
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
	 * @return an entity which contains all bundled images.
	 */
	public Entity bundle(String name, Map<Character, BufferedImage> chars2Images) {
		Entity root = new Entity(name);

		for (int x = 0; x < rasterX; x++) {
			for (int y = 0; y < rasterY; y++) {

				// Lookup tile image
				BufferedImage tile = chars2Images.get(mapData[y][x]);

				// If the char is valid...
				if (tile != null) {

					// Create image entity
					Picture pic = new Picture(x + ", " + y, tile);

					// At first translate
					pic.setPosition(gridToWorld(x, y));

					// Scale
					pic.getScale().set(rasterWidth, rasterHeight);

					// Attach to root
					root.attach(pic);
				}
			}
		}

		return root;
	}

	/**
	 * Translates grid coords to world coords.
	 * 
	 * @param location
	 *            The location in the array.
	 * @return a new vector containing the world coords.
	 */
	public Vector2f gridToWorld(Point location) {
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
	 * Translates world coords to the nearest grid coords.
	 * 
	 * @param position
	 *            The world position you want to translate.
	 * @return a new point containing the nearest grid coords.
	 */
	public Point worldToNearestGrid(Vector2f position) {
		// Calc new x and y
		float x = (position.x - rasterWidth * 0.5f) / rasterWidth, y = (position.y - rasterHeight * 0.5f)
				/ rasterHeight;

		// Round and return new point
		return new Point(Math.round(x), Math.round(y));
	}

	/**
	 * Validates a points against the valid range.
	 * 
	 * @param point
	 *            The point you want to validate.
	 * @return the same point if point is valid, otherwise null.
	 */
	public Point validate(Point point) {
		return validate(point.x, point.y) ? point : null;
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
	 * 
	 * @param location
	 *            The location in the array.
	 * @return the char at the specified location.
	 */
	public char at(Point location) {
		return at(location.x, location.y);
	}

	/**
	 * @param x
	 *            The column in the array.
	 * @param y
	 *            The row in the array.
	 * @return the char at the specified location.
	 */
	public char at(int x, int y) {
		return mapData[y][x];
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
				if (at(x, y) == find) {
					return new Point(x, y);
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * propra2012.gruppe33.graphics.rendering.scenegraph.Scene#setWidth(int)
	 */
	@Override
	public void setWidth(int width) {
		super.setWidth(width);

		// Calc the raster width
		rasterWidth = width / (float) rasterX;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * propra2012.gruppe33.graphics.rendering.scenegraph.Scene#setHeight(int)
	 */
	@Override
	public void setHeight(int height) {
		super.setHeight(height);

		// Calc the raster height
		rasterHeight = height / (float) rasterY;
	}

	/**
	 * @return the map data.
	 */
	public char[][] getMapData() {
		return mapData;
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

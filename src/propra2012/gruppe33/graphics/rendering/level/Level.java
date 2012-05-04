package propra2012.gruppe33.graphics.rendering.level;

import java.awt.Color;
import java.awt.Graphics2D;
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
import java.util.Set;

import propra2012.gruppe33.graphics.rendering.scenegraph.Vector2f;

/**
 * This class represents a level. It contains a name, char[][] array and some
 * other features.
 * 
 * @author Christopher Probst
 */
public class Level {

	/**
	 * Loads a level.
	 * 
	 * @deprecated ONLY USED FOR TEST PURPOSES.
	 */
	public static char[][] loadMap(String file) throws IOException {
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

	// The name of the map
	private String name;

	// The map data
	private char[][] mapData;

	// The map raster data
	private int width, height, rasterX, rasterY;

	/**
	 * Creates a new level using the given parameters.
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
	public Level(String name, int width, int height, char[][] mapData) {
		setName(name);
		setWidth(width);
		setHeight(height);
		setMapData(mapData);
	}

	/**
	 * Creates an image for fast rendering which contains all solid blocks using
	 * the char[][] map data.
	 * 
	 * @param solidTile
	 *            The image which is used to render a solid block.
	 * @param solidChars
	 *            The chars which represent a solid block.
	 * @return a compatible, transparent and rendered image with all solid
	 *         blocks.
	 */
	public BufferedImage renderSolidBlocks(BufferedImage solidTile,
			char... solidChars) {
		Set<Character> set = new HashSet<Character>();
		for (char solidChar : solidChars) {
			set.add(solidChar);
		}
		return renderSolidBlocks(solidTile, set);
	}

	/**
	 * Creates an image for fast rendering which contains all solid blocks using
	 * the char[][] map data.
	 * 
	 * @param solidTile
	 *            The image which is used to render a solid block.
	 * @param solidChars
	 *            The chars which represent a solid block.
	 * @return a compatible, transparent and rendered image with all solid
	 *         blocks.
	 */
	public BufferedImage renderSolidBlocks(BufferedImage solidTile,
			Set<Character> solidChars) {
		// Create new image
		BufferedImage renderedImage = createSolidBlockImage(width, height);

		// Open rendering context
		Graphics2D g = renderedImage.createGraphics();

		try {

			// Clear with trans
			g.setBackground(new Color(0, 0, 0, 0));
			g.clearRect(0, 0, width, height);

			// Calc float values
			float rw = getRasterWidth(), rh = getRasterHeight();

			for (int x = 0; x < rasterX; x++) {
				for (int y = 0; y < rasterY; y++) {

					// Select and check for solid char
					if (solidChars.contains(mapData[y][x])) {

						// Create a simple copy
						Graphics2D copy = (Graphics2D) g.create();
						try {
							// At first translate
							copy.translate(x * rw, y * rh);

							// Scale
							copy.scale(rw, rh);

							// Render the sub image
							copy.drawImage(solidTile, 0, 0, 1, 1, null);
						} finally {
							copy.dispose();
						}
					}
				}
			}

			return renderedImage;
		} finally {
			// Dispose the rendering context
			g.dispose();
		}
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
		return new Vector2f(getRasterWidth() * (x + 0.5f), getRasterHeight()
				* (y + 0.5f));
	}

	/**
	 * Translates world coords to the nearest grid coords.
	 * 
	 * @param position
	 *            The world position you want to translate.
	 * @return a new point containing the nearest grid coords.
	 */
	public Point worldToNearestGrid(Vector2f position) {
		// Calc rw and rh
		float rw = getRasterWidth(), rh = getRasterHeight();

		// Calc new x and y
		float x = (position.x - rw * 0.5f) / rw, y = (position.y - rh * 0.5f)
				/ rh;

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

	/**
	 * @return the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name. Must be non null.
	 * 
	 * @param name
	 *            The new name of the level.
	 */
	public void setName(String name) {
		if (name == null) {
			throw new NullPointerException("name cannot be null");
		}
		this.name = name;
	}

	/**
	 * Sets the viewport width of the level.
	 * 
	 * @param width
	 *            The new viewport width.
	 */
	public void setWidth(int width) {
		if (width <= 0) {
			throw new IllegalArgumentException("width must be > 0");
		}
		this.width = width;
	}

	/**
	 * Sets the viewport height of the level.
	 * 
	 * @param height
	 *            The new viewport height-
	 */
	public void setHeight(int height) {
		if (height <= 0) {
			throw new IllegalArgumentException("height must be > 0");
		}

		this.height = height;
	}

	/**
	 * @return the map data.
	 */
	public char[][] getMapData() {
		return mapData;
	}

	/**
	 * Sets the map data and verifies the format.
	 * 
	 * @param mapData
	 *            The new char[][] map data you want to use.
	 */
	public void setMapData(char[][] mapData) {
		if (mapData == null) {
			throw new NullPointerException("mapData cannot be null");
		} else if (mapData.length == 0) {
			throw new NullPointerException("mapData does not contain any rows");
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

		// Set raster x and y
		rasterY = mapData.length;
		rasterX = rowLength;

		// Finally safe
		this.mapData = mapData;
	}

	/**
	 * @return the width.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * 
	 * @return the raster width.
	 */
	public float getRasterWidth() {
		return width / (float) rasterX;
	}

	/**
	 * @return the height.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * 
	 * @return the raster height.
	 */
	public float getRasterHeight() {
		return height / (float) rasterY;
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

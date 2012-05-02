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
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class Level {

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
	 * @param w
	 * @param h
	 * @return
	 */
	public static BufferedImage createSolidBlockImage(int w, int h) {
		return GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getDefaultConfiguration()
				.createCompatibleImage(w, h, Transparency.BITMASK);
	}

	// The name of the map
	private String name;

	// The map data
	private char[][] mapData;

	// The map raster data
	private int width, height, rasterX, rasterY;

	public Level(String name, int width, int height, char[][] mapData) {
		setName(name);
		setWidth(width);
		setHeight(height);
		setMapData(mapData);
	}

	public BufferedImage renderSolidBlocks(BufferedImage solidTile,
			char solidChar) {
		// Create new image
		BufferedImage renderedImage = createSolidBlockImage(width, height);

		// Open rendering context
		Graphics2D g = renderedImage.createGraphics();

		try {

			// Clear with trans
			g.setBackground(new Color(0, 0, 0, 0));
			g.clearRect(0, 0, width, height);

			for (int x = 0; x < rasterX; x++) {
				for (int y = 0; y < rasterY; y++) {

					// Select and check for solid char
					if (mapData[y][x] == solidChar) {

						// Render the sub image
						g.drawImage(solidTile, x * getRasterWidth(), y
								* getRasterHeight(), getRasterWidth(),
								getRasterHeight(), null);
					}
				}
			}

			return renderedImage;
		} finally {
			// Dispose the rendering context
			g.dispose();
		}
	}

	public char at(int x, int y) {
		return mapData[y][x];
	}

	public Point find(char find) {
		return find(find, 0, 0);
	}

	public Point find(char find, Point after) {
		return find(find, after.x, after.y);
	}

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
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name == null) {
			throw new NullPointerException("name cannot be null");
		}
		this.name = name;
	}

	public void setWidth(int width) {
		if (width <= 0) {
			throw new IllegalArgumentException("width must be > 0");
		}
		this.width = width;
	}

	public void setHeight(int height) {
		if (height <= 0) {
			throw new IllegalArgumentException("height must be > 0");
		}

		this.height = height;
	}

	/**
	 * 
	 * @return the map data
	 */
	public char[][] getMapData() {
		return mapData;
	}

	/**
	 * Sets the map data and verifies the format.
	 * 
	 * @param mapData
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
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * 
	 * @return the raster width
	 */
	public int getRasterWidth() {
		return width / rasterX;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * 
	 * @return the raster height
	 */
	public int getRasterHeight() {
		return height / rasterY;
	}

	/**
	 * @return the rasterX
	 */
	public int getRasterX() {
		return rasterX;
	}

	/**
	 * @return the rasterY
	 */
	public int getRasterY() {
		return rasterY;
	}
}

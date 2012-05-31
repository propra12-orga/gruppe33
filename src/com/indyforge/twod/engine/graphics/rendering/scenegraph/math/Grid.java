package com.indyforge.twod.engine.graphics.rendering.scenegraph.math;

import java.awt.Point;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class Grid {

	// The width, height and size of the raster
	private final int width, height, size;

	/**
	 * Creates a new grid.
	 * 
	 * @param width
	 * @param height
	 */
	public Grid(int width, int height) {
		if (width <= 0) {
			throw new IllegalArgumentException("width must be > 0");
		} else if (height <= 0) {
			throw new IllegalArgumentException("height must be > 0");
		}

		this.width = width;
		this.height = height;
		size = width * height;
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}

	public int size() {
		return size;
	}

	public float ratio() {
		return width / (float) height;
	}

	public Point sizeAsPoint() {
		return new Point(width, height);
	}

	public Vector2f sizeAsVector() {
		return new Vector2f(width, height);
	}

	public boolean inside(Vector2f vector) {
		if (vector == null) {
			throw new NullPointerException("vector");
		}
		return inside(vector.round().point());
	}

	public boolean inside(Point point) {
		if (point == null) {
			throw new NullPointerException("point");
		}
		return inside(point.x, point.y);
	}

	public boolean inside(Point point, int dx, int dy) {
		if (point == null) {
			throw new NullPointerException("point");
		}
		point.translate(dx, dy);
		return inside(point);
	}

	public boolean inside(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	public boolean inside(int index) {
		return index >= 0 && index < size;
	}

	public int index(Vector2f vector) {
		if (vector == null) {
			throw new NullPointerException("vector");
		}
		return index(vector.round().point());
	}

	public int index(Point point) {
		if (point == null) {
			throw new NullPointerException("point");
		}
		return index(point.x, point.y);
	}

	public int index(int x, int y) {
		return width * y + x;
	}

	public Point point(int index) {
		if (!inside(index)) {
			throw new IndexOutOfBoundsException("Index out of grid");
		}
		return new Point(index % width, index / width);
	}

	public Vector2f vector(int index) {
		if (!inside(index)) {
			throw new IndexOutOfBoundsException("Index out of grid");
		}
		return new Vector2f(index % width, index / width);
	}
}

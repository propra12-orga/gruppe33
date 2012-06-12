package com.indyforge.twod.engine.graphics.rendering.scenegraph.math;

import java.io.Serializable;

/**
 * A simple line.
 * 
 * @author Christopher Probst
 * 
 */
public final class Line2f implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
	 * The origin and the destination.
	 */
	private Vector2f origin, destination;

	/**
	 * Creates a new line.
	 * 
	 * @param origin
	 *            The origin.
	 * @param destination
	 *            The destination.
	 */
	public Line2f(Vector2f origin, Vector2f destination) {
		origin(origin);
		destination(destination);
	}

	/**
	 * @see Vector2f#lerp(Vector2f, Vector2f, float)
	 */
	public Vector2f lerp(float time) {
		return Vector2f.lerp(origin, destination, time);
	}

	/**
	 * Sets the origin.
	 * 
	 * @param origin
	 *            The origin.
	 */
	public void origin(Vector2f origin) {
		if (origin == null) {
			throw new NullPointerException("origin");
		}
		this.origin = origin;
	}

	/**
	 * Sets the destination.
	 * 
	 * @param destination
	 *            The destination.
	 */
	public void destination(Vector2f destination) {
		if (destination == null) {
			throw new NullPointerException("destination");
		}
		this.destination = destination;
	}

	/**
	 * @return the origin.
	 */
	public Vector2f origin() {
		return origin;
	}

	/**
	 * @return the destination.
	 */
	public Vector2f destination() {
		return destination;
	}
}

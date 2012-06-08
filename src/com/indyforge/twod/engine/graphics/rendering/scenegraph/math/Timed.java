package com.indyforge.twod.engine.graphics.rendering.scenegraph.math;

import java.io.Serializable;

/**
 * 
 * @author Christopher Probst
 * 
 * @param <T>
 *            The value type.
 */
public final class Timed<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The value
	private final T value;

	// The time
	private final float time;

	/**
	 * Create a new timed component.
	 * 
	 * @param value
	 *            The value.
	 * @param time
	 *            The time.
	 */
	public Timed(T value, float time) {
		if (value == null) {
			throw new NullPointerException("value");
		} else if (time < 0.0f) {
			throw new IllegalArgumentException("time must be >= 0");
		}
		this.value = value;
		this.time = time;
	}

	/**
	 * @return the time.
	 */
	public float time() {
		return time;
	}

	/**
	 * @return the value.
	 */
	public T value() {
		return value;
	}
}

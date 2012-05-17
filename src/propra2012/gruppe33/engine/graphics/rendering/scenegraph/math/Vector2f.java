package propra2012.gruppe33.engine.graphics.rendering.scenegraph.math;

import java.io.Serializable;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;

/**
 * This is a simple 2d vector implementation. It contains the most basic
 * features.
 * 
 * @author Christopher Probst
 * @see Entity
 */
public final class Vector2f implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Lerps two vectors (Linear interpolation). This means you provide two
	 * vectors (start, end) and a float between 0 and 1. This method calculates
	 * a new vector which is between start and end using the given float value.
	 * 0 = start und 1 = end.
	 * 
	 * @param start
	 *            The start vector.
	 * @param end
	 *            The end vector
	 * @param time
	 *            A float value between 0 and 1.
	 * @return a vector which is lerped between start and end.
	 */
	public Vector2f lerp(Vector2f start, Vector2f end, float time) {
		if (start == null) {
			throw new NullPointerException("start");
		} else if (end == null) {
			throw new NullPointerException("end");
		}

		// Return the lerp vector
		return start.add(end.sub(start).scale(Mathf.clamp(time, 0f, 1f)));
	}

	/**
	 * Moves the start vector to the end vector but never exceeds the max delta
	 * value.
	 * 
	 * @param start
	 *            The start vector.
	 * @param end
	 *            The end vector
	 * @param maxDelta
	 *            The start vector never gets closer to the end vector as max
	 *            delta.
	 * @return the transformed start vector.
	 */
	public static Vector2f moveTowards(Vector2f start, Vector2f end,
			float maxDelta) {
		if (start == null) {
			throw new NullPointerException("start");
		} else if (end == null) {
			throw new NullPointerException("end");
		}

		// Calc distance between the vectors
		Vector2f dist = end.sub(start);

		// Calc the time to move
		float time = maxDelta / dist.length();

		// Return a lerped value
		return start.add(dist.scale(Mathf.clamp(time, 0f, 1f)));
	}

	/**
	 * @return a new vector initialized with 0,0
	 */
	public static Vector2f zero() {
		return new Vector2f();
	}

	/**
	 * @return a new vector initialized with 0,1
	 */
	public static Vector2f forward() {
		return new Vector2f(0, 1);
	}

	/**
	 * @return a new vector initialized with 1,0
	 */
	public static Vector2f right() {
		return new Vector2f(1, 0);
	}

	/**
	 * @return a new vector initialized with -1,0
	 */
	public static Vector2f left() {
		return new Vector2f(-1, 0);
	}

	/**
	 * @return a new vector initialized with 0,-1
	 */
	public static Vector2f back() {
		return new Vector2f(0, -1);
	}

	/*
	 * The x and y components of this vector.
	 */
	public float x, y;

	/**
	 * Creates a copy of a given vector.
	 * 
	 * @param other
	 *            The vector you want to clone.
	 */
	public Vector2f(Vector2f other) {
		this(other.x, other.y);
	}

	/**
	 * Creates a new vector with 0,0.
	 */
	public Vector2f() {
		this(0, 0);
	}

	/**
	 * Creates a new vector using the given values.
	 * 
	 * @param x
	 *            The x component of the new vector.
	 * @param y
	 *            The y component of the new vector.
	 */
	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Sets the components of this vector.
	 * 
	 * @param x
	 *            The x component of this vector.
	 * @param y
	 *            The y component of this vector.
	 * @return this.
	 */
	public Vector2f set(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}

	/**
	 * Sets the components of this vector.
	 * 
	 * @param other
	 *            The vector which you want to copy.
	 * @return this.
	 */
	public Vector2f set(Vector2f other) {
		if (other == null) {
			throw new NullPointerException("other");
		}
		this.x = other.x;
		this.y = other.y;
		return this;
	}

	/**
	 * @return the length of this vector.
	 */
	public float length() {
		return (float) Math.sqrt(x * x + y * y);
	}

	/**
	 * Sets this vector to 0,0.
	 * 
	 * @return this.
	 */
	public Vector2f setZero() {
		x = y = 0;
		return this;
	}

	/**
	 * Adds an other vector to this vector.
	 * 
	 * @param other
	 *            The vector you want to add to this.
	 * @return this.
	 */
	public Vector2f addLocal(Vector2f other) {
		if (other == null) {
			throw new NullPointerException("other");
		}

		x += other.x;
		y += other.y;
		return this;
	}

	/**
	 * Subtracts an other from this vector.
	 * 
	 * @param other
	 *            The vector you want to subtract.
	 * @return this.
	 */
	public Vector2f subLocal(Vector2f other) {
		if (other == null) {
			throw new NullPointerException("other");
		}

		x -= other.x;
		y -= other.y;
		return this;
	}

	/**
	 * Scales this vector by multiplying it with a scalar.
	 * 
	 * @param scalar
	 *            The scale factor.
	 * @return this.
	 */
	public Vector2f scaleLocal(float scalar) {
		x *= scalar;
		y *= scalar;
		return this;
	}

	/**
	 * Multiplies this vector with a scalar and returns a new vector which
	 * contains the result.
	 * 
	 * @param scalar
	 *            The scale factor.
	 * @return a new result vector.
	 */
	public Vector2f scale(float scalar) {
		return new Vector2f(this).scaleLocal(scalar);
	}

	/**
	 * Adds this vector with an other vector and returns a new vector which
	 * contains the result.
	 * 
	 * @param other
	 *            The vector you want to add.
	 * @return a new result vector.
	 */
	public Vector2f add(Vector2f other) {
		if (other == null) {
			throw new NullPointerException("other");
		}

		return new Vector2f(this).addLocal(other);
	}

	/**
	 * Substracts an other vector from this vector and returns a new vector
	 * which contains the result.
	 * 
	 * @param other
	 *            The vector you want to subtract.
	 * @return a new result vector.
	 */
	public Vector2f sub(Vector2f other) {
		if (other == null) {
			throw new NullPointerException("other");
		}

		return new Vector2f(this).subLocal(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		return result;
	}

	/**
	 * @param v
	 *            The value you want to compare with the x component.
	 * @param threshold
	 * @return true if |x-v| is smaller than the given threshold, otherwise
	 *         false.
	 */
	public boolean xThreshold(float v, float threshold) {
		return Math.abs(x - v) < Math.abs(threshold);
	}

	/**
	 * @param v
	 *            The value you want to compare with the y component.
	 * @param threshold
	 * @return true if |y-v| is smaller than the given threshold, otherwise
	 *         false.
	 */
	public boolean yThreshold(float v, float threshold) {
		return Math.abs(y - v) < Math.abs(threshold);
	}

	/**
	 * @param other
	 *            The vector you want to compare with this vector.
	 * @param threshold
	 * @return true if xThreshold and yThreshold succeeds, otherwise false.
	 */
	public boolean threshold(Vector2f other, Vector2f threshold) {
		if (other == null) {
			throw new NullPointerException("other");
		} else if (threshold == null) {
			throw new NullPointerException("threshold");
		}

		return xThreshold(other.x, threshold.x)
				&& yThreshold(other.y, threshold.y);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Vector2f)) {
			return false;
		}
		Vector2f other = (Vector2f) obj;

		if (!xThreshold(other.x, Mathf.kEpsilon)) {
			return false;
		}

		if (!yThreshold(other.y, Mathf.kEpsilon)) {
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Vector2f[x = " + x + ", y = " + y + "]";
	}
}

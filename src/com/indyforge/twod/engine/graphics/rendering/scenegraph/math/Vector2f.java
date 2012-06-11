package com.indyforge.twod.engine.graphics.rendering.scenegraph.math;

import java.awt.Point;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;

/**
 * This is a simple 2d vector implementation. It contains the most basic
 * features.
 * 
 * @author Christopher Probst
 * @see Entity
 */
public final class Vector2f implements Externalizable {

	/**
	 * 
	 * @author Christopher Probst
	 * 
	 */
	public enum Direction {
		North, South, East, West, Undefined;

		public Vector2f vector() {
			switch (this) {
			case North:
				return Vector2f.north();
			case South:
				return Vector2f.south();
			case West:
				return Vector2f.west();
			case East:
				return Vector2f.east();
			case Undefined:
				return Vector2f.zero();
			default:
				throw new UnsupportedOperationException(this
						+ " not implemented");
			}
		}
	}

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
	public static Vector2f lerp(Vector2f start, Vector2f end, float time) {
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
	public static Vector2f south() {
		return new Vector2f(0, 1);
	}

	/**
	 * @return a new vector initialized with 1,0
	 */
	public static Vector2f east() {
		return new Vector2f(1, 0);
	}

	/**
	 * @return a new vector initialized with -1,0
	 */
	public static Vector2f west() {
		return new Vector2f(-1, 0);
	}

	/**
	 * @return a new vector initialized with 0,-1
	 */
	public static Vector2f north() {
		return new Vector2f(0, -1);
	}

	/*
	 * The x and y components of this vector.
	 */
	public float x, y;

	/**
	 * Creates a copy of the given point.
	 * 
	 * @param point
	 *            The point you want to copy.
	 */
	public Vector2f(Point point) {
		this(point.x, point.y);
	}

	/**
	 * Creates a copy of the given vector.
	 * 
	 * @param other
	 *            The vector you want to copy.
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		// Read both components
		x = in.readFloat();
		y = in.readFloat();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		// Write both components
		out.writeFloat(x);
		out.writeFloat(y);
	}

	/**
	 * Sets the components of this vector.
	 * 
	 * @param x
	 *            The x component of this vector.
	 * @param y
	 *            The y component of this vector.
	 * @return this for chaining.
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
	 * @return this for chaining.
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
	 * @return this for chaining.
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
	 * @return this for chaining.
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
	 * @return this for chaining.
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
	 * @return this for chaining.
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
	 * Scales this vector by multiplying each component with each other
	 * component of the other vector.
	 * 
	 * @param other
	 *            The scale vector.
	 * @return this for chaining.
	 */
	public Vector2f scaleLocal(Vector2f other) {
		if (other == null) {
			throw new NullPointerException("other");
		}

		x *= other.x;
		y *= other.y;
		return this;
	}

	/**
	 * Multiplies each component of this vector with each component of the other
	 * vector and returns a new vector which contains the result.
	 * 
	 * @param other
	 *            The scale vector.
	 * @return the new result vector.
	 */
	public Vector2f scale(Vector2f other) {
		return new Vector2f(this).scaleLocal(other);
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

	/**
	 * Inverts this vector.
	 * 
	 * @return this for chaining.
	 */
	public Vector2f invertLocal() {
		x = 1f / x;
		y = 1f / y;
		return this;
	}

	/**
	 * @return a new vector which contains the inverse of this vector.
	 */
	public Vector2f invert() {
		return new Vector2f(this).invert();
	}

	/**
	 * Normalizes this vector.
	 * 
	 * @return this for chaining.
	 */
	public Vector2f normalizeLocal() {
		float length = length();
		x /= length;
		y /= length;
		return this;
	}

	/**
	 * @return a new vector which contains normalized components.
	 */
	public Vector2f normalize() {
		return new Vector2f(this).normalizeLocal();
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
	 * Rounds this entity.
	 * 
	 * @return this for chaining.
	 */
	public Vector2f roundLocal() {
		x = Math.round(x);
		y = Math.round(y);
		return this;
	}

	/**
	 * @return a new rounded vector.
	 */
	public Vector2f round() {
		return new Vector2f(this).roundLocal();
	}

	/**
	 * @return the vector as point. The float components will not be rounded but
	 *         converted to integers.
	 */
	public Point point() {
		return new Point((int) x, (int) y);
	}

	/**
	 * @return the nearest direction of this vector.
	 */
	public Direction nearestDirection() {

		if (Mathf.equals(x, y)) {
			return Direction.Undefined;
		} else if (Math.abs(x) > Math.abs(y)) {
			return x > 0.0f ? Direction.East : Direction.West;
		} else {
			return y > 0.0f ? Direction.South : Direction.North;
		}
	}

	/**
	 * Check whether or not this vector is inside the given rectangle.
	 * 
	 * @param leftUp
	 *            The left-up corner.
	 * @param rightDown
	 *            The right-down corner.
	 * @return true if this vector is inside the rectangle, otherwise false.
	 */
	public boolean inside(Vector2f leftUp, Vector2f rightDown) {
		if (leftUp == null) {
			throw new NullPointerException("leftUp");
		} else if (rightDown == null) {
			throw new NullPointerException("rightDown");
		}

		return x >= leftUp.x && x <= rightDown.x && y >= leftUp.y
				&& y <= rightDown.y;
	}

	/**
	 * @param other
	 *            The vector you want to compare with this vector.
	 * @param threshold
	 *            The threshold.
	 * @return true if this vector equals the other vector (using the
	 *         threshold), otherwise false.
	 */
	public boolean equals(Vector2f other, Vector2f threshold) {
		if (other == null) {
			throw new NullPointerException("other");
		} else if (threshold == null) {
			throw new NullPointerException("threshold");
		}

		return Mathf.equals(x, other.x, threshold.x)
				&& Mathf.equals(y, other.y, threshold.y);
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

		if (!Mathf.equals(x, other.x)) {
			return false;
		}

		if (!Mathf.equals(y, other.y)) {
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

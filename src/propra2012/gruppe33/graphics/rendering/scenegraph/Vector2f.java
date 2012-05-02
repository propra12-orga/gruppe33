package propra2012.gruppe33.graphics.rendering.scenegraph;

import java.io.Serializable;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class Vector2f implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final float kEpsilon = 1E-6f;

	public Vector2f lerp(Vector2f start, Vector2f end, float t) {

		// Clamp...
		if (t < 0) {
			t = 0;
		} else if (t > 1) {
			t = 1;
		}

		// Return the lerp vector
		return start.add(end.sub(start).scale(t));
	}

	public static Vector2f zero() {
		return new Vector2f();
	}

	public static Vector2f forward() {
		return new Vector2f(0, 1);
	}

	public static Vector2f right() {
		return new Vector2f(1, 0);
	}

	public static Vector2f left() {
		return new Vector2f(-1, 0);
	}

	public static Vector2f back() {
		return new Vector2f(0, -1);
	}

	public float x, y;

	public Vector2f(Vector2f other) {
		this(other.x, other.y);
	}

	public Vector2f() {
		this(0, 0);
	}

	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vector2f set(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public float length() {
		return (float) Math.sqrt(x * x + y * y);
	}

	public Vector2f setZero() {
		x = y = 0;
		return this;
	}

	public Vector2f addLocal(Vector2f other) {
		x += other.x;
		y += other.y;
		return this;
	}

	public Vector2f subLocal(Vector2f other) {
		x -= other.x;
		y -= other.y;
		return this;
	}

	public Vector2f scaleLocal(float scalar) {
		x *= scalar;
		y *= scalar;
		return this;
	}

	public Vector2f scale(float scalar) {
		return new Vector2f(this).scaleLocal(scalar);
	}

	public Vector2f add(Vector2f other) {
		return new Vector2f(other).addLocal(this);
	}

	public Vector2f sub(Vector2f other) {
		return new Vector2f(other).subLocal(this);
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

		if (Math.abs(x - other.x) > kEpsilon) {
			return false;
		}

		if (Math.abs(y - other.y) > kEpsilon) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return "Vector2f[x = " + x + ", y = " + y + "]";
	}
}

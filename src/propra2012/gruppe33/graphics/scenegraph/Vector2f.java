package propra2012.gruppe33.graphics.scenegraph;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class Vector2f {

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

	public Vector2f add(Vector2f other) {
		return new Vector2f(other).addLocal(this);
	}

	public Vector2f sub(Vector2f other) {
		return new Vector2f(other).subLocal(this);
	}
}

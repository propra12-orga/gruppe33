package propra2012.gruppe33.graphics.rendering.scenegraph.entities;

import propra2012.gruppe33.graphics.rendering.scenegraph.Entity;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class InterpolatedEntity extends Entity {

	public static class Pair<A, B> {

		private final A a;
		private final B b;

		public Pair(A a, B b) {
			this.a = a;
			this.b = b;
		}

		public A getA() {
			return a;
		}

		public B getB() {
			return b;
		}
	}

	/*
	 * This list contains stored positions linked to timestamps. These positions
	 * will be used to interpolate this entity.
	 */
	// private final List<Pair<Vector2f, Float>> storedPositions = new
	// LinkedList<Pair<Vector2f, Float>>();

	public InterpolatedEntity(String id) {
		super(id);
	}

}

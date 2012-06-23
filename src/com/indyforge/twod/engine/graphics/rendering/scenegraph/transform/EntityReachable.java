package com.indyforge.twod.engine.graphics.rendering.scenegraph.transform;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;

/**
 * 
 * @author Christopher Probst
 * 
 * @param <E>
 */
public abstract class EntityReachable<D, E extends Entity> implements Reachable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * The controlled entity.
	 */
	private final E controlled;

	/*
	 * The reached flag.
	 */
	protected boolean reached = false;

	/*
	 * The destination.
	 */
	private final D destination;

	/*
	 * The veloctiy which is used to reach the reachable.
	 */
	private final float veloctiy;

	/**
	 * @see EntityReachable#reach(float)
	 */
	protected abstract boolean reachTarget(float tpf);

	/**
	 * Adds the given state to the controlled entity state.
	 * 
	 * @param state
	 *            The state you want to add.
	 */
	protected abstract void addToState(D state);

	public EntityReachable(E controlled, D destination, float velocity) {
		if (controlled == null) {
			throw new NullPointerException("controlled");
		} else if (destination == null) {
			throw new NullPointerException("destination");
		} else if (velocity <= 0f) {
			throw new IllegalArgumentException("Velocity must be > 0");
		}

		this.controlled = controlled;
		this.destination = destination;
		this.veloctiy = velocity;
	}

	/**
	 * @return the controlled entity.
	 */
	public E controlled() {
		return controlled;
	}

	/**
	 * @return the destination.
	 */
	public D destination() {
		return destination;
	}

	/**
	 * @return the velocity.
	 */
	public float veloctiy() {
		return veloctiy;
	}

	/**
	 * @return the true if the reachable is reached, otherwise false.
	 */
	public boolean isReached() {
		return reached;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.transform.Reachable
	 * #reach(float)
	 */
	@Override
	public boolean reach(float tpf) {
		return !reached ? reached = reachTarget(tpf) : reached;
	}
}

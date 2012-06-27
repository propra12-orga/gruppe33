package com.indyforge.twod.engine.graphics.rendering.scenegraph.transform;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.util.task.CancellableTask;

/**
 * 
 * @author Christopher Probst
 * 
 * @param <E>
 */
public abstract class EntityTask<D, E extends Entity> implements
		CancellableTask {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * The controlled entity.
	 */
	private final E controlled;

	/*
	 * The finished flag.
	 */
	protected boolean finished = false;

	/*
	 * The destination.
	 */
	private final D destination;

	/*
	 * The veloctiy which is used to update the task.
	 */
	private final float veloctiy;

	/**
	 * @see EntityTask#update(float)
	 */
	protected abstract boolean updateTask(float tpf);

	/**
	 * Adds the given state to the controlled entity state.
	 * 
	 * @param state
	 *            The state you want to add.
	 */
	protected abstract void addToState(D state);

	public EntityTask(E controlled, D destination, float velocity) {
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
	 * @return the true if the task is finished, otherwise false.
	 */
	public boolean isFinished() {
		return finished;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.twod.engine.util.task.Task#update(float)
	 */
	@Override
	public boolean update(float tpf) {
		return !finished ? finished = updateTask(tpf) : finished;
	}
}

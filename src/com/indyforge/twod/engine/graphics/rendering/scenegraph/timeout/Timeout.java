package com.indyforge.twod.engine.graphics.rendering.scenegraph.timeout;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;

/**
 * This simple controller handles timeouts.
 * 
 * @author Christopher Probst
 * @see Entity
 */
public class Timeout extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum TimeoutEvent {
		Timeout
	}

	// The timeout in seconds.
	private float timeout;

	@Override
	protected void onEvent(Entity source, Object event, Object... params) {
		super.onEvent(source, event, params);

		if (event instanceof TimeoutEvent) {
			switch ((TimeoutEvent) event) {
			case Timeout:
				onTimeout((Timeout) source);
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity#onUpdate
	 * (float)
	 */
	@Override
	protected void onUpdate(float tpf) {
		super.onUpdate(tpf);

		// Reduce the timeout
		timeout -= tpf;

		// Timeout happend
		if (timeout <= 0) {
			// Fire event
			fireEvent(TimeoutEvent.Timeout);
		}
	}

	/**
	 * OVERRIDE FOR CUSTOM RENDER BEHAVIOUR.
	 * 
	 * This method gets called when the timeout has reached.
	 * 
	 * @param timeout
	 *            The timeout entity.
	 */
	protected void onTimeout(Timeout timeout) {
	}

	public Timeout() {
		events().put(TimeoutEvent.Timeout, iterableChildren(true, true));
	}

	/**
	 * Creates a new timeout controller.
	 * 
	 * @param timeout
	 *            The timeout in seconds.
	 */
	public Timeout(float timeout) {
		timeout(timeout);
	}

	/**
	 * Sets the timeout in seconds.
	 * 
	 * @param timeout
	 *            The new timeout in seconds.
	 * @return this for chaining.
	 */
	public Timeout timeout(float timeout) {
		if (timeout < 0) {
			throw new IllegalArgumentException("Timeout must be >= 0");
		}
		this.timeout = timeout;
		return this;
	}

	/**
	 * @return the specified timeout in seconds.
	 */
	public float timeout() {
		return timeout;
	}
}

package propra2012.gruppe33.engine.graphics.rendering.scenegraph.timeout;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.util.SiblingIterator;

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
	protected void onEvent(Object event, Object... params) {
		super.onEvent(event, params);

		if (event instanceof TimeoutEvent) {
			switch ((TimeoutEvent) event) {
			case Timeout:
				onTimeout((Timeout) params[0]);
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity#onUpdate
	 * (float)
	 */
	@Override
	protected void onUpdate(float tpf) {

		// Reduce the timeout
		timeout -= tpf;

		// Timeout happend
		if (timeout <= 0) {
			// Fire event
			fireEvent(new SiblingIterator(this, true), TimeoutEvent.Timeout,
					this);
		}
	}

	protected void onTimeout(Timeout timeout) {
	}

	/**
	 * Creates a new timeout controller.
	 * 
	 * @param timeout
	 *            The timeout in seconds.
	 */
	public Timeout(float timeout) {
		setTimeout(timeout);
	}

	/**
	 * Sets the timeout in seconds.
	 * 
	 * @param timeout
	 *            The new timeout in seconds.
	 */
	public void setTimeout(float timeout) {
		if (timeout < 0) {
			throw new IllegalArgumentException("Timeout must be >= 0");
		}
		this.timeout = timeout;
	}

	/**
	 * @return the specified timeout in seconds.
	 */
	public float getTimeout() {
		return timeout;
	}
}

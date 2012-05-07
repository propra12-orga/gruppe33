package propra2012.gruppe33.graphics.rendering.scenegraph.timeout;

import java.awt.Graphics2D;

import propra2012.gruppe33.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.graphics.rendering.scenegraph.EntityController;

/**
 * This simple controller handles timeouts.
 * 
 * @author Christopher Probst
 * @see EntityController
 */
public class TimeoutController implements EntityController {

	// The timeout in seconds.
	private float timeout;

	/**
	 * Creates a new timeout controller.
	 * 
	 * @param timeout
	 *            The timeout in seconds.
	 */
	public TimeoutController(float timeout) {
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

	/**
	 * Invoked when the timeout occured.
	 * 
	 * @param entity
	 *            The entity which has this controller.
	 */
	protected void onTimeout(Entity entity) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * propra2012.gruppe33.graphics.rendering.scenegraph.EntityController#doRender
	 * (propra2012.gruppe33.graphics.rendering.scenegraph.Entity,
	 * java.awt.Graphics2D, java.awt.Graphics2D)
	 */
	@Override
	public void doRender(Entity entity, Graphics2D original,
			Graphics2D transformed) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * propra2012.gruppe33.graphics.rendering.scenegraph.EntityController#doUpdate
	 * (propra2012.gruppe33.graphics.rendering.scenegraph.Entity, float)
	 */
	@Override
	public void doUpdate(Entity entity, float tpf) {

		// Reduce the timeout
		timeout -= tpf;

		// Timeout happend
		if (timeout <= 0) {
			onTimeout(entity);
		}
	}
}

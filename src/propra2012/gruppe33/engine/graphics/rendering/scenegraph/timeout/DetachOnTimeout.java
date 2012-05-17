package propra2012.gruppe33.engine.graphics.rendering.scenegraph.timeout;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;

/**
 * Simple detaches the entity when timeout is reached.
 * 
 * @author Christopher Probst
 * 
 */
public final class DetachOnTimeout extends TimeoutController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DetachOnTimeout(float timeout) {
		super(timeout);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see propra2012.gruppe33.engine.graphics.rendering.scenegraph.timeout.
	 * TimeoutController
	 * #onTimeout(propra2012.gruppe33.engine.graphics.rendering.
	 * scenegraph.Entity)
	 */
	@Override
	protected void onTimeout(Entity entity) {
		entity.detach();
	}
}

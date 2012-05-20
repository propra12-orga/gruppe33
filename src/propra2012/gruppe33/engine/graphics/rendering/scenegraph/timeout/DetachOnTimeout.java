package propra2012.gruppe33.engine.graphics.rendering.scenegraph.timeout;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.timeout.Timeout.TimeoutEvent;

/**
 * Simply detaches the entity when timeout is reached.
 * 
 * @author Christopher Probst
 * 
 */
public class DetachOnTimeout extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void onEvent(Object event, Object... params) {
		super.onEvent(event, params);

		if (event instanceof TimeoutEvent) {
			switch ((TimeoutEvent) event) {
			case Timeout:

				// Detach the parent
				Entity entity = ((Entity) params[0]).parent();
				if (entity != null) {
					entity.detach();
				}
				break;
			}
		}
	}
}

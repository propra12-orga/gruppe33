package com.indyforge.twod.engine.graphics.rendering.scenegraph.timeout;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.timeout.Timeout.TimeoutEvent;

/**
 * Simply detaches the parent of the timeout entity when the timeout is reached.
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
	protected void onEvent(Entity source, Object event, Object... params) {
		super.onEvent(source, event, params);

		if (event instanceof TimeoutEvent) {
			switch ((TimeoutEvent) event) {
			case Timeout:

				// Detach the parent
				Entity entity = ((Entity) source).parent();

				if (entity != null) {
					entity.detach();
				}
				break;
			}
		}
	}
}

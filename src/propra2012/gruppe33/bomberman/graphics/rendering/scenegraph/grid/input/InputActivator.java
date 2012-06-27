package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.input;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity.Many;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class InputActivator extends Many<Entity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void apply(Entity entity) {
		// Attach the remote view!
		entity.attach(new GridRemoteInput(entity.registrationKey()));
	}
}

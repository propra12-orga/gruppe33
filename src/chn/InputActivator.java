package chn;

import java.util.UUID;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridRemoteInput;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.AbstractEntityChange;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class InputActivator extends AbstractEntityChange<Entity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InputActivator() {
		super();
	}

	public InputActivator(UUID registrationKey) {
		super(registrationKey);
	}

	@Override
	protected void apply(Entity entity) {
		// Attach the remote view!
		entity.attach(new GridRemoteInput(entity.registrationKey()));
	}
}

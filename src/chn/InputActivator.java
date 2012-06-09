package chn;

import java.util.UUID;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.BombSpawner;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridController;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridRemoteController;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.RemoteBombSpawner;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.AbstractEntityChange;
import com.indyforge.twod.engine.util.IterationRoutines;
import com.indyforge.twod.engine.util.TypeFilter;

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
		// Lookup grid controller
		GridController gc = (GridController) IterationRoutines.filterNext(
				new TypeFilter(GridController.class, true),
				entity.childIterator(true, true));

		// Attach the remote view!
		entity.attach(new GridRemoteController(gc.registrationKey()));

		// Lookup bomb controller
		BombSpawner bs = (BombSpawner) IterationRoutines.filterNext(
				new TypeFilter(BombSpawner.class, true),
				entity.childIterator(true, true));

		// Attach the remote view!
		entity.attach(new RemoteBombSpawner(bs.registrationKey()));
	}
}

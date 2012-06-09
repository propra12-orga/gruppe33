package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid;

import java.util.UUID;

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

	public InputActivator(UUID registrationKey) {
		super(registrationKey);
	}

	@Override
	protected void apply(Entity entity) {
		InputUploader iu = (InputUploader) IterationRoutines.filterNext(
				new TypeFilter(InputUploader.class, true),
				entity.childIterator(true, true));
		iu.active = true;
	}
}

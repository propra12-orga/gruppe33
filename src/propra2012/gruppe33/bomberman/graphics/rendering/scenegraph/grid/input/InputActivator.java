package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.input;

import propra2012.gruppe33.bomberman.GameConstants;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.RenderedImage;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity.Many;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class InputActivator extends Many<Entity> implements GameConstants {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void apply(Entity entity) {

		// Create new grid remote inpute
		GridRemoteInput gri = new GridRemoteInput(entity.registrationKey());

		// Attach the remote view!
		entity.attach(gri);

		RenderedImage ri = new RenderedImage(gri.findScene().imageProp(
				ACTIVE_FLARE_IMAGE)).centered(true);

		// Attach the flare
		entity.attach(ri.index(ITEM_INDEX));

		// Attach as type prop
		entity.addTypeProp(gri);
	}
}

package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.transform;

import java.util.Map;
import java.util.UUID;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridRoutines;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity.ManyToMany;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class ManyPositionsToMany extends
		ManyToMany<GraphicsEntity, Vector2f> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void apply(final GraphicsEntity entity, Vector2f value) {

		// Simply add the delta position
		entity.position().addLocal(value);

		// Rearragne
		GridRoutines.rearrangeGridNode((GraphicsEntity) entity.parent());
	}

	public ManyPositionsToMany(Map<UUID, Vector2f> entityMap) {
		super(entityMap);
	}

	public ManyPositionsToMany() {
	}
}

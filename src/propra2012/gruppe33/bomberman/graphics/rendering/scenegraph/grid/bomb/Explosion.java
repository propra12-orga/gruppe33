package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.bomb;

import java.util.List;
import java.util.UUID;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridConstants;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridRoutines;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity.Many;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class Explosion extends Many<GraphicsEntity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void apply(GraphicsEntity entity) {
		// Create a new explosion
		GraphicsEntity explosion = GridRoutines.createExplosion(entity
				.findScene().spriteProp(GridConstants.EXP_SPRITE), 33);

		// Simply attach
		entity.attach(explosion);
	}

	public Explosion() {
	}

	public Explosion(List<UUID> registrationKeys) {
		super(registrationKeys);
	}
}

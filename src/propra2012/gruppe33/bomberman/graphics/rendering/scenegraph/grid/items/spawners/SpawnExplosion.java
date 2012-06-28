package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.items.spawners;

import java.util.List;
import java.util.UUID;

import propra2012.gruppe33.bomberman.GameConstants;
import propra2012.gruppe33.bomberman.GameRoutines;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity.Many;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class SpawnExplosion extends Many<GraphicsEntity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void apply(GraphicsEntity entity) {
		// Create a new explosion
		GraphicsEntity explosion = GameRoutines.createExplosion(entity
				.findScene().spriteProp(GameConstants.EXP_SPRITE), 33);

		// Simply attach
		entity.attach(explosion);
	}

	public SpawnExplosion() {
	}

	public SpawnExplosion(List<UUID> registrationKeys) {
		super(registrationKeys);
	}
}

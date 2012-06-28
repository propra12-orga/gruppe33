package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.items.spawners;

import java.util.List;
import java.util.UUID;

import propra2012.gruppe33.bomberman.GameRoutines;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity.OneToMany;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class SpawnPalisade extends
		OneToMany<GraphicsEntity, PalisadeDesc> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity
	 * .OneToMany
	 * #apply(com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity,
	 * java.lang.Object)
	 */
	@Override
	protected void apply(GraphicsEntity entity, PalisadeDesc value) {

		// Attach the palisade
		entity.attach(GameRoutines.createPalisade(entity.findScene(),
				value.direction()).registrationKey(value.itemEntity()));
	}

	public SpawnPalisade() {
	}

	public SpawnPalisade(List<UUID> registrationKeys) {
		super(registrationKeys);
	}
}

package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.items.spawners;

import java.util.List;
import java.util.UUID;

import propra2012.gruppe33.bomberman.GameConstants;
import propra2012.gruppe33.bomberman.GameRoutines;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.items.ItemDesc;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity.OneToMany;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class SpawnShield extends OneToMany<GraphicsEntity, ItemDesc> implements
		GameConstants {

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
	protected void apply(GraphicsEntity entity, ItemDesc value) {
		entity.attach(GameRoutines.createShield(entity.findScene())
				.registrationKey(value.itemEntity()));

		// Play only on client side
		if (entity.findSceneProcessor().hasSession()) {
			entity.findScene().soundManager().playSound(SHIELD_ON_SOUND, true);
		}
	}

	public SpawnShield() {
	}

	public SpawnShield(List<UUID> registrationKeys) {
		super(registrationKeys);
	}
}

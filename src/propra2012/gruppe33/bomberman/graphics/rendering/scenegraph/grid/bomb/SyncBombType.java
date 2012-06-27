package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.bomb;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity.ManyToMany;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class SyncBombType extends ManyToMany<BombSpawner, BombType> {

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
	protected void apply(BombSpawner entity, BombType value) {
		entity.activeBombType(value);
	}
}

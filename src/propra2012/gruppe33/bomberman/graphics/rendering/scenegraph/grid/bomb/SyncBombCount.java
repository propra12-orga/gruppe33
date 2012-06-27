package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.bomb;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity.Many;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class SyncBombCount extends Many<BombSpawner> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The bomb type
	private BombType bombType;

	// The count of the bombs
	private int count;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity
	 * .Many
	 * #apply(com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity)
	 */
	@Override
	protected void apply(BombSpawner entity) {
		entity.bombs().put(bombType, count);
	}

	public BombType bombType() {
		return bombType;
	}

	public SyncBombCount bombType(BombType bombType) {
		this.bombType = bombType;
		return this;
	}

	public SyncBombCount count(int count) {
		this.count = count;
		return this;
	}

	public int count() {
		return count;
	}
}

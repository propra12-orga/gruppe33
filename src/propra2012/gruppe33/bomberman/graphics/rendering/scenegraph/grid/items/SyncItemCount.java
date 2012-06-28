package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.items;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity.Many;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class SyncItemCount extends Many<ItemSpawner> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The item
	private CollectableItem item;

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
	protected void apply(ItemSpawner entity) {
		entity.items().put(item, count);
	}

	public CollectableItem item() {
		return item;
	}

	public SyncItemCount item(CollectableItem item) {
		this.item = item;
		return this;
	}

	public SyncItemCount count(int count) {
		this.count = count;
		return this;
	}

	public int count() {
		return count;
	}
}

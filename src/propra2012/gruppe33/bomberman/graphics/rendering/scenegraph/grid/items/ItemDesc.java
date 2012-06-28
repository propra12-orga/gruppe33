package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.items;

import java.io.Serializable;
import java.util.UUID;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class ItemDesc implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The item and the uuid
	private CollectableItem item;
	private UUID itemEntity;

	public CollectableItem item() {
		return item;
	}

	public ItemDesc item(CollectableItem item) {
		this.item = item;
		return this;
	}

	public ItemDesc randomItemEntity() {
		itemEntity = UUID.randomUUID();
		return this;
	}

	public UUID itemEntity() {
		return itemEntity;
	}

	public ItemDesc itemEntity(UUID itemEntity) {
		this.itemEntity = itemEntity;
		return this;
	}
}

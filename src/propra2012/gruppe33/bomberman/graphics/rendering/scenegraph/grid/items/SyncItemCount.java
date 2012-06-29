package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.items;

import propra2012.gruppe33.bomberman.GameConstants;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity.Many;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class SyncItemCount extends Many<ItemSpawner> implements
		GameConstants {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The item
	private CollectableItem item;

	// The count of the bombs
	private int count;

	// Shoud a sound be played ?
	private boolean playSound;

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
		// Did we lost something ?
		boolean down = count < entity.items().get(item);

		// Sync!
		entity.items().put(item, count);

		if (playSound) {
			String name;

			if (!down) {

				switch (item) {
				case Speed:
					name = EAT_SOUND;
					break;
				case ShieldPotion:
					name = GLASS_SOUND;
					break;
				default:
					name = PICKUP_SOUND;
					break;
				}

				// Pickup/Eat sound
				entity.findScene().soundManager().playSound(name, true);
			} else if (item != CollectableItem.Speed) {

				// Which sound should be played ???
				name = item == CollectableItem.ShieldPotion ? SHIELD_ON_SOUND
						: PLACE_SOUND;

				// PLACE sound
				entity.findScene().soundManager().playSound(name, true);
			}
		}
	}

	public CollectableItem item() {
		return item;
	}

	public SyncItemCount item(CollectableItem item) {
		this.item = item;
		return this;
	}

	public boolean isPlaySound() {
		return playSound;
	}

	public SyncItemCount playSound(boolean playSound) {
		this.playSound = playSound;
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

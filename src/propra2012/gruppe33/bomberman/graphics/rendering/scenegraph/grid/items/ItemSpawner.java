package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.items;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.EnumMap;
import java.util.Map;

import propra2012.gruppe33.bomberman.GameRoutines;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.input.GridRemoteInput;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.input.Input;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.items.bomb.Bomb;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.items.bomb.BombDesc;

import com.indyforge.foxnet.rmi.pattern.change.Session;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.input.InputChange;
import com.indyforge.twod.engine.util.iteration.TypeFilter;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class ItemSpawner extends GraphicsEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The collectable items
	private final Map<CollectableItem, Integer> items = new EnumMap<CollectableItem, Integer>(
			CollectableItem.class);

	// The collectable spawn items
	private final Map<CollectableItem, Boolean> spawnItems = new EnumMap<CollectableItem, Boolean>(
			CollectableItem.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity#onEvent
	 * (com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity,
	 * java.lang.Object, java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void onEvent(Entity source, Object event, Object... params) {
		super.onEvent(source, event, params);
		if (event == InputChange.class) {
			// The input map
			Map<Input, Boolean> input = (Map<Input, Boolean>) params[0];

			// Parse the input
			spawnItems.put(CollectableItem.DefaultBomb,
					input.get(Input.PlaceDefaultBomb));
			spawnItems.put(CollectableItem.NukeBomb,
					input.get(Input.PlaceNukeBomb));
			spawnItems.put(CollectableItem.FastBomb,
					input.get(Input.PlaceFastBomb));
			spawnItems.put(CollectableItem.Palisade,
					input.get(Input.PlacePalisade));
			spawnItems.put(CollectableItem.ShieldPotion,
					input.get(Input.ActivateShield));
		}
	}

	public ItemSpawner() {
		// Set item counters
		items.put(CollectableItem.DefaultBomb, 1);
		items.put(CollectableItem.NukeBomb, 0);
		items.put(CollectableItem.FastBomb, 0);
		items.put(CollectableItem.Palisade, 0);
		items.put(CollectableItem.ShieldPotion, 0);
		items.put(CollectableItem.SlowShroom, 0);
		items.put(CollectableItem.FastShroom, 0);

		spawnItems.put(CollectableItem.DefaultBomb, Boolean.FALSE);
		spawnItems.put(CollectableItem.NukeBomb, Boolean.FALSE);
		spawnItems.put(CollectableItem.FastBomb, Boolean.FALSE);
		spawnItems.put(CollectableItem.Palisade, Boolean.FALSE);
		spawnItems.put(CollectableItem.ShieldPotion, Boolean.FALSE);
		spawnItems.put(CollectableItem.SlowShroom, Boolean.FALSE);
		spawnItems.put(CollectableItem.FastShroom, Boolean.FALSE);
	}

	/**
	 * @return the item map.
	 */
	public Map<CollectableItem, Integer> items() {
		return items;
	}

	public ItemSpawner removeItems(CollectableItem item, int count) {
		return addItems(item, -count);
	}

	public ItemSpawner addItems(final CollectableItem item, final int count) {
		int value = items.get(item) + count;
		items.put(item, value < 0 ? 0 : value);

		// Find the scene processor
		SceneProcessor proc = findSceneProcessor();

		/*
		 * Server running ??
		 */
		if (proc.hasAdminSessionServer()) {

			// Create item count sync
			SyncItemCount syncItemCount = new SyncItemCount();
			syncItemCount.item(item).count(items.get(item)).entities()
					.add(registrationKey());

			// Get session
			Session<SceneProcessor> session = proc.adminSessionServer()
					.session(Long.parseLong(parent().name()));

			// If session exists...
			if (session != null) {
				session.client().applyChange(syncItemCount);
			}
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity
	 * #onRender(java.awt.Graphics2D, java.awt.Graphics2D)
	 */
	@Override
	protected void onRender(Graphics2D original, Graphics2D transformed) {
		super.onRender(original, transformed);

		if (findSibling(new TypeFilter(GridRemoteInput.class), false) != null) {
			original.setFont(new Font("Arial", Font.BOLD, 36));
			original.setColor(Color.black);

			int offset = 30;
			for (CollectableItem item : CollectableItem.values()) {
				original.drawString(item + "'s: ", 0, offset);
				original.drawString(String.valueOf(items().get(item)), 280,
						offset);
				offset += 30;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity#onUpdate
	 * (float)
	 */
	@Override
	protected void onUpdate(float tpf) {
		super.onUpdate(tpf);

		// Get parent
		GraphicsEntity node = ((GraphicsEntity) parent().parent());

		CollectableItem item = CollectableItem.DefaultBomb;

		// Bombable ?
		if (GameRoutines.isFieldBombable(node)
		// Spawn ?
				&& spawnItems.get(item)
				// Enough bombs ?
				&& items.get(item) > 0) {

			// Create bomb for the given field
			Bomb bomb = new Bomb();
			bomb.entities().add(node.registrationKey());
			bomb.value(new BombDesc().delay(GameRoutines.bombDelay(item))
					.range(GameRoutines.bombRange(item)).bomb(item)
					.randomBombImage().player(parent().registrationKey()));

			// Remove a bomb
			removeItems(item, 1);

			// Apply global change
			node.findSceneProcessor().adminSessionServer().composite()
					.queueChange(bomb, true);

			// Deactivate spawning
			spawnItems.put(CollectableItem.DefaultBomb, false);
		}
	}
}

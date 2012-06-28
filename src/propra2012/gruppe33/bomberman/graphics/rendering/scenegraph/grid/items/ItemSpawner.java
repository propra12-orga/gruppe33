package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.items;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Deque;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;

import propra2012.gruppe33.bomberman.GameRoutines;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.input.GridRemoteInput;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.input.Input;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.items.bomb.Bomb;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.items.bomb.BombDesc;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.items.spawners.PalisadeDesc;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.items.spawners.SpawnPalisade;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.items.spawners.SpawnShield;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.movement.GridMovement;

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

	// The input queue!
	private final Deque<Map<Input, Boolean>> inputQueue = new LinkedList<Map<Input, Boolean>>();

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

			// Offer next input
			inputQueue.offer(input);
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

		// Poll next
		Map<Input, Boolean> input = inputQueue.poll();

		// Process NOW
		if (input != null) {

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

		// Get parent
		GraphicsEntity node = ((GraphicsEntity) parent().parent());

		// Collect spawn requests
		int defBomb = spawnItems.get(CollectableItem.DefaultBomb) ? 1 : 0, nukeBomb = spawnItems
				.get(CollectableItem.NukeBomb) ? 1 : 0, fastBomb = spawnItems
				.get(CollectableItem.FastBomb) ? 1 : 0, pali = spawnItems
				.get(CollectableItem.Palisade) ? 1 : 0;

		// The item...
		CollectableItem item;

		if (defBomb + nukeBomb + fastBomb + pali == 1) {

			if (pali == 0) {
				if (nukeBomb == 1) {
					item = CollectableItem.NukeBomb;
				} else if (fastBomb == 1) {
					item = CollectableItem.FastBomb;
				} else {
					item = CollectableItem.DefaultBomb;
				}

				// Bombable ?
				if (GameRoutines.isFieldBombable(node)
				// Spawn ?
						&& spawnItems.get(item)
						// Enough bombs ?
						&& items.get(item) > 0) {

					// Create bomb for the given field
					Bomb bomb = new Bomb();
					bomb.entities().add(node.registrationKey());
					bomb.value((BombDesc) new BombDesc()
							.player(parent().registrationKey())
							.delay(GameRoutines.bombDelay(item))
							.range(GameRoutines.bombRange(item)).item(item)
							.randomItemEntity());

					// Apply global change
					node.findSceneProcessor().adminSessionServer().composite()
							.queueChange(bomb, true);

					// Clean up
					removeItems(item, 1);
					spawnItems.put(item, false);
				}
			} else {
				item = CollectableItem.Palisade;

				// Bombable (Pali...) ?
				if (GameRoutines.isFieldBombable(node)
				// Spawn ?
						&& spawnItems.get(item)
						// Enough items ?
						&& items.get(item) > 0) {

					// Create new spawn-paliside request
					SpawnPalisade sp = new SpawnPalisade();
					sp.entities().add(node.registrationKey());

					// Set the nearest direction
					sp.value((PalisadeDesc) new PalisadeDesc()
							.direction(
									parent().typeProp(GridMovement.class)
											.direction().vector().swapLocal()
											.nearestDirection()).item(item)
							.randomItemEntity());

					// Apply global change
					node.findSceneProcessor().adminSessionServer().composite()
							.queueChange(sp, true);

					// Clean up
					removeItems(item, 1);
					spawnItems.put(item, false);
				}
			}
		}

		/*
		 * The shield potion.
		 */
		if (spawnItems.get(CollectableItem.ShieldPotion)
				&& items.get(CollectableItem.ShieldPotion) > 0) {

			// Add the node
			SpawnShield ss = new SpawnShield();
			ss.entities().add(parent().registrationKey());

			// Register the item
			ss.value(new ItemDesc().randomItemEntity().item(
					CollectableItem.ShieldPotion));

			// Apply global change
			node.findSceneProcessor().adminSessionServer().composite()
					.queueChange(ss, true);

			// Clean up
			removeItems(CollectableItem.ShieldPotion, 1);
			spawnItems.put(CollectableItem.ShieldPotion, false);
		}

		// Get both shroom states
		int fastShrooms = items.get(CollectableItem.FastShroom), slowShrooms = items
				.get(CollectableItem.SlowShroom);

		// Anything changed ?
		if (fastShrooms != 0 || slowShrooms != 0) {

			// Calc the total shroom
			int totalShroom = fastShrooms - slowShrooms;

			// Get the movement
			GridMovement movement = parent().typeProp(GridMovement.class);

			// Get the old value
			float vm = movement.velocityMultiplier();

			// Increase the value
			vm += totalShroom * 0.2f;

			// Write back
			movement.velocityMultiplier(vm);

			// Update!
			removeItems(CollectableItem.FastShroom, Integer.MIN_VALUE);
			removeItems(CollectableItem.SlowShroom, Integer.MIN_VALUE);
		}
	}
}

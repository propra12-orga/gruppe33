package propra2012.gruppe33.bomberman;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.items.CollectableItem;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.items.ItemSpawner;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.movement.GridMovement;
import propra2012.gruppe33.bomberman.graphics.sprite.AnimationRoutines;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.EntityFilter;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.RenderedImage;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.animation.RenderedAnimation;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.animation.RenderedAnimation.AnimationEvent;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Grid;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f.Direction;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity.DetachEntityChange;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.input.InputChange;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.timeout.Timeout;
import com.indyforge.twod.engine.graphics.sprite.Animation;
import com.indyforge.twod.engine.graphics.sprite.AnimationBundle;
import com.indyforge.twod.engine.graphics.sprite.Sprite;
import com.indyforge.twod.engine.resources.assets.AssetManager;

/**
 * 
 * @author Christopher Probst
 * @author Malte Schmidt
 * @author Matthias Hesse
 * 
 */
public final class GameRoutines implements GameConstants {

	public static final EntityFilter EXP_RANGE_FILTER = new EntityFilter() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/*
		 * 
		 */
		@Override
		public boolean accept(Entity element) {
			return element.typeProp(Float.class) != null;
		}
	};

	/**
	 * This filter only accepts free nodes which have a velocity.
	 */
	public static final EntityFilter VELOCITY_NODE_FILTER = new EntityFilter() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/*
		 * 
		 */
		@Override
		public boolean accept(Entity element) {
			return isFieldFree(element)
					&& element.typeProp(Float.class) != null;
		}
	};

	/**
	 * Collects all entity node position which are in range.
	 * 
	 * @param node
	 *            The node.
	 * @param entityFilter
	 *            The entity filter.
	 * @param range
	 *            The range.
	 * @return a list with all points.
	 */
	public static List<Point> bombRange(Entity node, EntityFilter entityFilter,
			int range) {
		if (node == null) {
			throw new NullPointerException("node");
		} else if (!(node.parent() instanceof GraphicsEntity)) {
			throw new IllegalArgumentException("The node parent is not a "
					+ "graphics entity");
		} else if (entityFilter != null && !entityFilter.accept(node)) {
			throw new IllegalStateException("The given node is not accepted");
		}

		// Convert parent
		GraphicsEntity gridEntity = (GraphicsEntity) node.parent();

		// Convert the node
		GraphicsEntity graphicsNode = (GraphicsEntity) node;

		// Calc origin
		Vector2f origin = graphicsNode.position().round();

		// Create new array list
		List<Point> points = new ArrayList<Point>(range * 4);

		// The origin is quite valid!
		points.add(origin.point());

		/*
		 * Cast rays into all directions. Enums ftw!
		 */
		for (Direction dir : Direction.values()) {

			// Ignore the undefined direction!
			if (dir == Direction.Undefined) {
				continue;
			}

			// Calc the dir range
			int dirRange = (int) GameRoutines.lineOfSight(gridEntity,
					entityFilter, origin, range, dir);

			if (dirRange >= 1) {
				// Add points
				for (int i = 1; i <= dirRange; i++) {

					// Calc active point point and add
					points.add(origin.add(dir.vector().scaleLocal(i)).point());
				}
			}
		}

		return points;
	}

	/**
	 * Calculates the line-of-sight starting at the given position into the
	 * given direction. If you provide an entity filter you can specify which
	 * nodes are valid.
	 * 
	 * @param gridEntity
	 *            The grid entity.
	 * @param entityFilter
	 *            The entity filter.
	 * @param position
	 *            The position.
	 * @param max
	 *            The max distance.
	 * @param direction
	 *            The direction.
	 * @return the valid line-of-sight as float.
	 */
	public static float lineOfSight(Entity gridEntity,
			EntityFilter entityFilter, Vector2f position, float max,
			Direction direction) {

		if (gridEntity == null) {
			throw new NullPointerException("gridEntity");
		} else if (position == null) {
			throw new NullPointerException("position");
		} else if (direction == null) {
			throw new NullPointerException("direction");
		}

		// Convert to nearest vector
		Vector2f nearest = position.round();

		// Get the coords of the nearest vector
		Point point = nearest.point();

		// Get the grid
		Grid grid = gridEntity.typeProp(Grid.class);

		// Position is outside the grid
		if (!grid.inside(point)) {
			throw new IllegalArgumentException("Position out of grid");
		}

		// The return value
		float distance;

		// Used for check
		int offx, offy;

		switch (direction) {

		case North:
		case South:

			// Calc distance
			distance = direction == Direction.North ? position.y - nearest.y
					: nearest.y - position.y;

			// Init
			offy = direction == Direction.North ? -1 : 1;
			offx = 0;

			break;

		case West:
		case East:

			// Calc distance
			distance = direction == Direction.West ? position.x - nearest.x
					: nearest.x - position.x;

			// Init
			offy = 0;
			offx = direction == Direction.West ? -1 : 1;

			break;

		default:
			throw new IllegalArgumentException("Unknown enum type: "
					+ direction);
		}

		/*
		 * While the next point is inside the grid and the entity filter accepts
		 * the next node entity (if null the filter is ignored) and the distance
		 * has not reached the max yet.
		 */
		while (grid.inside(point, offx, offy)
				&& (entityFilter == null || entityFilter.accept(gridEntity
						.childAt(grid.index(point)))) && distance < max) {

			// Increase distance
			distance += 1.0f;
		}

		// Limit the result
		if (distance > max) {
			distance = max;
		}

		return distance;
	}

	public static RenderedImage createShield(final Scene scene) {
		if (scene == null) {
			throw new NullPointerException("scene");
		}

		// The shield
		final RenderedImage shield = new RenderedImage().centered(true)
				.imageResource(scene.imageProp(SHIELD_IMAGE));

		// Set index + tag
		shield.index(SHIELD_INDEX).tag(SHIELD_TAG);

		// Register delete procedure!
		if (scene.processor().hasAdminSessionServer()) {
			shield.attach(new Timeout(10) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void onTimeout(Timeout timeout) {
					super.onTimeout(timeout);

					DetachEntityChange dec = new DetachEntityChange();
					dec.entities().add(shield.registrationKey());

					scene.processor().adminSessionServer().composite()
							.queueChange(dec, true);
				}
			});
		}

		return shield;
	}

	public static RenderedImage createPalisade(Scene scene, Direction direction) {
		if (scene == null) {
			throw new NullPointerException("scene");
		}

		// The new palisade
		RenderedImage palisade = new RenderedImage().centered(true);

		// A palisade is breakable!
		palisade.tag(BREAKABLE_TAG);

		// Switch direction
		switch (direction) {
		case North:
		case South:
			palisade.imageResource(scene.imageProp(PALISADE_VERT_IMAGE));
			break;

		case East:
		case West:
			palisade.imageResource(scene.imageProp(PALISADE_HORI_IMAGE));
			break;

		default:
			throw new IllegalArgumentException(direction + " not supported");
		}

		return palisade;
	}

	public static RenderedAnimation createExplosion(Sprite explosion,
			long timePerImage) {

		// Create new explosion entity
		Animation animation = explosion
				.newAnimationFromRange("explosion", timePerImage, 0, 0, 25)
				.loop(false).paused(false);

		// Create an entity using the animation
		RenderedAnimation renderedAnimation = new RenderedAnimation();
		renderedAnimation.tag(FREE_TAG).tag(EXPLOSION_TAG)
				.index(EXPLOSION_INDEX);
		renderedAnimation.animationBundle().add(animation);
		renderedAnimation.animationName("explosion");

		// Delete this entity when the animation is finished
		renderedAnimation.attach(new Entity() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(Entity source, Object event,
					Object... params) {
				super.onEvent(source, event, params);

				if (event instanceof AnimationEvent) {
					switch ((AnimationEvent) event) {
					case AnimationFinished:

						// Detach the rendered animation
						source.detach();

						break;
					}
				}
			}
		});

		return renderedAnimation;
	}

	/**
	 * Creates a new remote dwarf player.
	 * 
	 * @param assetManager
	 *            The asset manager to load the dwarf.
	 * @param name
	 *            The name of the player.
	 * @return the created dwarf.
	 * @throws Exception
	 *             If an error occurs.
	 */
	public static GraphicsEntity createRemoteDwarf(AssetManager assetManager,
			String name) throws Exception {
		return createRemoteGridPlayer(name,
				AnimationRoutines.createDwarf(assetManager, 35, 35));
	}

	/**
	 * Creates a new remote wizard player.
	 * 
	 * @param assetManager
	 *            The asset manager to load the wizard.
	 * @param name
	 *            The name of the player.
	 * @return the created wizard.
	 * @throws Exception
	 *             If an error occurs.
	 */
	public static GraphicsEntity createRemoteWizard(AssetManager assetManager,
			String name) throws Exception {
		return createRemoteGridPlayer(name,
				AnimationRoutines.createWizard(assetManager, 35, 35));
	}

	/**
	 * Creates a new remote santa player.
	 * 
	 * @param assetManager
	 *            The asset manager to load the santa.
	 * @param name
	 *            The name of the player.
	 * @return the created santa.
	 * @throws Exception
	 *             If an error occurs.
	 */
	public static GraphicsEntity createRemoteSanta(AssetManager assetManager,
			String name) throws Exception {
		return createRemoteGridPlayer(name,
				AnimationRoutines.createSanta(assetManager, 35, 35));
	}

	/**
	 * Creates a new remote knight player.
	 * 
	 * @param assetManager
	 *            The asset manager to load the knight.
	 * @param name
	 *            The name of the player.
	 * @return the created knight.
	 * @throws Exception
	 *             If an error occurs.
	 */
	public static GraphicsEntity createRemoteKnight(AssetManager assetManager,
			String name) throws Exception {
		return createRemoteGridPlayer(name,
				AnimationRoutines.createKnight(assetManager, 35, 35));
	}

	/**
	 * Translates the bomb to its range.
	 * 
	 * @param bomb
	 *            The bomb.
	 * @return the range or -1.
	 */
	public static int bombRange(CollectableItem bomb) {
		if (bomb == null) {
			throw new NullPointerException("bomb");
		}
		switch (bomb) {
		case DefaultBomb:
			return DEFAULT_BOMB_RANGE;
		case NukeBomb:
			return NUKE_BOMB_RANGE;
		case FastBomb:
			return FAST_BOMB_RANGE;
		default:
			return -1;
		}
	}

	/**
	 * Translates the bomb to its delay.
	 * 
	 * @param bomb
	 *            The bomb.
	 * @return the delay or -1.
	 */
	public static float bombDelay(CollectableItem bomb) {
		if (bomb == null) {
			throw new NullPointerException("bomb");
		}
		switch (bomb) {
		case DefaultBomb:
			return DEFAULT_BOMB_DELAY;
		case NukeBomb:
			return NUKE_BOMB_DELAY;
		case FastBomb:
			return FAST_BOMB_DELAY;
		default:
			return -1;
		}
	}

	/**
	 * Translates the bomb to an asset path.
	 * 
	 * @param bomb
	 *            The bomb.
	 * @return the asset path or null.
	 */
	public static String bombToAsset(CollectableItem bomb) {
		if (bomb == null) {
			throw new NullPointerException("bomb");
		}
		switch (bomb) {
		case DefaultBomb:
			return DEFAULT_BOMB_IMAGE;
		case NukeBomb:
			return NUKE_BOMB_IMAGE;
		case FastBomb:
			return FAST_BOMB_IMAGE;
		default:
			return null;
		}
	}

	/**
	 * Translates the bomb bag to an asset path.
	 * 
	 * @param bombBag
	 *            The bomb bag.
	 * @return the asset path or null.
	 */
	public static String bombBagToAsset(CollectableItem bombBag) {
		if (bombBag == null) {
			throw new NullPointerException("bombBag");
		}
		switch (bombBag) {
		case DefaultBomb:
			return DEFAULT_BOMB_BAG_IMAGE;
		case NukeBomb:
			return NUKE_BOMB_BAG_IMAGE;
		case FastBomb:
			return FAST_BOMB_BAG_IMAGE;
		default:
			return null;
		}
	}

	/**
	 * Translates the item to a bag asset path.
	 * 
	 * @param item
	 *            The item.
	 * @return the asset path or null.
	 */
	public static String itemToBagAsset(CollectableItem item) {
		if (item == null) {
			throw new NullPointerException("item");
		}
		switch (item) {
		case DefaultBomb:
			return DEFAULT_BOMB_BAG_IMAGE;
		case NukeBomb:
			return NUKE_BOMB_BAG_IMAGE;
		case FastBomb:
			return FAST_BOMB_BAG_IMAGE;
		case Palisade:
			return PALISADE_BAG_IMAGE;
		case ShieldPotion:
			return SHIELD_POTION_IMAGE;
		case SlowShroom:
			return SLOW_SHROOM_IMAGE;
		case FastShroom:
			return FAST_SHROOM_IMAGE;
		default:
			return null;
		}
	}

	/**
	 * Hides a bomb behind the first breakable entity at the given node.
	 * 
	 * @param node
	 *            The node.
	 * @param item
	 *            The item.
	 */
	public static void createItem(GraphicsEntity node,
			final CollectableItem item) {
		if (node == null) {
			throw new NullPointerException("node");
		} else if (item == null) {
			throw new NullPointerException("item");
		} else if (node.children().size() <= 0) {
			throw new IllegalStateException("The given node does not "
					+ "contain any children");
		} else if (!node.childAt(0).tagged(BREAKABLE_TAG)) {
			throw new IllegalStateException("The first entity at this "
					+ "point is not tagged as breakable");
		}

		// Lookup bag asset
		final String bagAsset = itemToBagAsset(item);

		// Check the asset!
		if (bagAsset == null) {
			throw new IllegalArgumentException("Unknown bag");
		}

		// There MUST BE a breakable child!
		node.childAt(0).attach(new Entity() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			private final UUID uuid = UUID.randomUUID();

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity #
			 * onParentDetached(com.indyforge.twod.engine.graphics.rendering
			 * .scenegraph.Entity, com.indyforge.twod.engine.graphics.rendering
			 * .scenegraph.Entity)
			 */
			@Override
			protected void onParentDetached(final Entity parent, Entity child) {
				super.onParentDetached(parent, child);

				// Create a new item image
				RenderedImage itemImage = new RenderedImage(
						((GraphicsEntity) parent).findScene().imageProp(
								bagAsset)) {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					/*
					 * (non-Javadoc)
					 * 
					 * @see
					 * com.indyforge.twod.engine.graphics.rendering.scenegraph
					 * .Entity
					 * #onEntityAttached(com.indyforge.twod.engine.graphics
					 * .rendering.scenegraph.Entity,
					 * com.indyforge.twod.engine.graphics
					 * .rendering.scenegraph.Entity)
					 */
					@Override
					protected void onEntityAttached(Entity p, Entity c) {

						// Find the scene
						Scene scene = findScene();

						/*
						 * Sibling added ?
						 */
						if (parent == p
								&& scene.processor().hasAdminSessionServer()) {
							if (c.tagged(PLAYER_TAG)) {

								// Get item spawner object
								ItemSpawner sp = c.typeProp(ItemSpawner.class);

								// Increase the item count
								sp.addItems(item, 1);

								// Remove the item bag
								DetachEntityChange dec = new DetachEntityChange();
								dec.entities().add(registrationKey());

								// Queue the change
								scene.processor().adminSessionServer()
										.broadcast().queueChange(dec, true);

								// Apply direct on server side !
								scene.processor().adminSessionServer().local()
										.applyChange(dec);
							}
						}
					}

				}.centered(true);

				// Use the same reg key
				itemImage.registrationKey(uuid);

				// Scale a bit
				itemImage.scale().set(ITEM_SCALE);

				// Set item order + tag
				itemImage.index(ITEM_INDEX).tag(FREE_TAG).tag(BREAKABLE_TAG);

				// Attach to the node
				parent.attach(itemImage);
			}
		});
	}

	/**
	 * Creates a new local grid player.
	 * 
	 * @param name
	 *            The name of the player.
	 * @param charAniBundle
	 *            The char-animation bundle.
	 * @return the created player entity.
	 */
	public static GraphicsEntity createRemoteGridPlayer(String name,
			AnimationBundle charAniBundle) {
		if (name == null) {
			throw new NullPointerException("name");
		}

		// Create new player
		GraphicsEntity player = new GraphicsEntity();

		// Register the input change event
		player.events().put(InputChange.class,
				player.iterableChildren(true, true));

		// Set player name
		player.name(name);

		// Give player tag
		player.tag(PLAYER_TAG);

		// Create animation
		RenderedAnimation charAni = new RenderedAnimation(charAniBundle);

		// Look north by default
		charAni.animationName(AnimationRoutines.RUN_PREFIX
				+ Direction.North.toString().toLowerCase());

		// Create grid movement
		GridMovement movement = new GridMovement();

		// Do the animation!
		movement.attach(AnimationRoutines
				.createGridControllerAnimationHandler(charAni));

		// Create new item spawner
		ItemSpawner itemSpawner = new ItemSpawner();

		// Attach remaining stuff
		player.attach(charAni, movement, itemSpawner);

		// Add the animation
		player.addTypeProp(charAni);

		// Add the item spawner
		player.addTypeProp(itemSpawner);

		// Add the movement!
		player.addTypeProp(movement);

		// Set scale
		player.scale().set(PLAYER_SCALE);

		return player;
	}

	/**
	 * Checks a given field for explosions.
	 * 
	 * @param nodeEntity
	 *            The node.
	 * @return true if the given node contains an explosion-tagged entity,
	 *         otherwise false.
	 */
	public static boolean hasFieldExplosion(Entity nodeEntity) {
		if (nodeEntity == null) {
			throw new NullPointerException("nodeEntity");
		}
		for (Entity child : nodeEntity) {
			if (child.tagged(EXPLOSION_TAG)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks the child node of the grid entity at the given point to be free.
	 * 
	 * @param gridEntity
	 *            The grid entity.
	 * @param point
	 *            The point of the node.
	 * @return true if the field is free, otherwise false.
	 */
	public static boolean isFieldFree(Entity gridEntity, Point point) {
		if (gridEntity == null) {
			throw new NullPointerException("gridEntity");
		} else if (point == null) {
			throw new NullPointerException("point");
		}

		// Get the grid
		Grid grid = gridEntity.typeProp(Grid.class);

		// Position is outside the grid
		if (!grid.inside(point)) {
			throw new IllegalArgumentException("Position out of grid");
		}

		return isFieldFree(gridEntity.childAt(grid.index(point)));
	}

	/**
	 * Checks a single node entity to be bombable.
	 * 
	 * @param nodeEntity
	 *            The node entity you want to check.
	 * @return true if the field is bombable, otherwise false.
	 */
	public static boolean isFieldBombable(Entity nodeEntity) {
		if (nodeEntity == null) {
			throw new NullPointerException("nodeEntity");
		}
		for (Entity child : nodeEntity) {
			if (!child.tagged(FREE_TAG) && !child.tagged(PLAYER_TAG)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks a single node entity to be free.
	 * 
	 * @param nodeEntity
	 *            The node entity you want to check.
	 * @return true if the field is free, otherwise false.
	 */
	public static boolean isFieldFree(Entity nodeEntity) {
		if (nodeEntity == null) {
			throw new NullPointerException("nodeEntity");
		}
		for (Entity child : nodeEntity) {
			if (!child.tagged(FREE_TAG)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Rearranges the children of the given node entity.
	 * <p>
	 * Please note that this method is NOT called automatically so you should
	 * call this method when the position of the node-children have been
	 * changed.
	 * 
	 * @param node
	 *            The node entity.
	 * @return the node for chaining.
	 */
	public static GraphicsEntity rearrangeGridNode(GraphicsEntity node) {
		if (node == null) {
			throw new NullPointerException("node");
		} else if (!(node.parent() instanceof GraphicsEntity)) {
			throw new IllegalArgumentException("node does not have "
					+ "a graphics entity parent");
		}

		// Lookup parent
		GraphicsEntity graphicsParent = (GraphicsEntity) node.parent();

		// Lookup grip
		Grid grid = graphicsParent.typeProp(Grid.class);

		// Check the grid
		if (grid == null) {
			throw new IllegalStateException("Parent does not have a "
					+ "grid property");
		}

		/*
		 * The following code is very important. It reattaches children when
		 * they leave the nodes.
		 */

		// Iterate over all children
		for (Entity child : node) {

			if (child instanceof GraphicsEntity) {
				// Convert
				GraphicsEntity graphicsChild = (GraphicsEntity) child;

				// Get points
				Point absolute = node.position().point(), relative = graphicsChild
						.position().round().point(), newAbsolute = new Point(
						relative);

				// Translate the new absolute point
				newAbsolute.translate(absolute.x, absolute.y);

				// Check coords
				if (!absolute.equals(newAbsolute)) {

					// New point valid ??
					if (grid.inside(newAbsolute)) {
						// Lookup other child
						Entity otherChild = graphicsParent.childAt(grid
								.index(newAbsolute));

						// Attach to it
						otherChild.attach(child);

						// Change position
						graphicsChild.position().x -= relative.x;
						graphicsChild.position().y -= relative.y;
					}
				}
			}
		}

		return node;
	}

	/**
	 * Creates a new grid entity which contains all nodes.
	 * 
	 * @param gridWidth
	 *            The width of the grid.
	 * @param gridHeight
	 *            The height of the grid.
	 * @return the new grid entity.
	 */
	public static GraphicsEntity createGridEntity(int gridWidth, int gridHeight) {

		// Create grid
		final Grid grid = new Grid(gridWidth, gridHeight);

		// Create a new grid entity
		GraphicsEntity gridEntity = new GraphicsEntity();

		// Add as type property
		gridEntity.addTypeProp(grid);

		// Create nodes for each coords
		for (int y = 0; y < gridHeight; y++) {
			for (int x = 0; x < gridWidth; x++) {

				// Create a new node
				GraphicsEntity node = new GraphicsEntity();

				// Set position on grid
				node.position().set(x, y);

				// Not visible...
				node.visible(false);

				// Attach the node to the grid
				gridEntity.attach(node);
			}
		}
		return gridEntity;
	}

	// Should not be instantiated...
	private GameRoutines() {
	}
}

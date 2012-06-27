package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.bomb.BombSpawner;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.bomb.BombType;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.input.GridController;
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
public final class GridRoutines implements GridConstants {

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
			int dirRange = (int) GridRoutines.lineOfSight(gridEntity,
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

	public static GraphicsEntity createExplosion(Sprite explosion,
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
	 * Translates the bomb type to an asset path.
	 * 
	 * @param bombType
	 *            The bomb type.
	 * @return the asset path.
	 */
	public static String bombTypeToAsset(BombType bombType) {
		if (bombType == null) {
			throw new NullPointerException("bombType");
		}
		switch (bombType) {
		case Default:
			return DEFAULT_BOMB_IMAGE;
		case Nuke:
			return NUKE_BOMB_IMAGE;
		case Time:
			return TIME_BOMB_IMAGE;
		default:
			throw new IllegalStateException("Unknown bomb state: " + bombType);
		}
	}

	/**
	 * Translates the bomb bag type to an asset path.
	 * 
	 * @param bombType
	 *            The bomb type.
	 * @return the asset path.
	 */
	public static String bombBagTypeToAsset(BombType bombType) {
		if (bombType == null) {
			throw new NullPointerException("bombType");
		}
		switch (bombType) {
		case Default:
			return DEFAULT_BOMB_BAG_IMAGE;
		case Nuke:
			return NUKE_BOMB_BAG_IMAGE;
		case Time:
			return TIME_BOMB_BAG_IMAGE;
		default:
			throw new IllegalStateException("Unknown bomb state: " + bombType);
		}
	}

	/**
	 * Hides a bomb behind the first breakable entity at the given node.
	 * 
	 * @param node
	 *            The node.
	 * @param bombType
	 *            The bomb type.
	 */
	public static void createBombItem(GraphicsEntity node,
			final BombType bombType) {
		if (node == null) {
			throw new NullPointerException("node");
		} else if (bombType == null) {
			throw new NullPointerException("bombType");
		} else if (node.children().size() <= 0) {
			throw new IllegalStateException("The given node does not "
					+ "contain any children");
		} else if (!node.childAt(0).tagged(BREAKABLE_TAG)) {
			throw new IllegalStateException("The first entity at this "
					+ "point is not tagged as breakable");
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

				// Create a new bomb image
				RenderedImage bombImage = new RenderedImage(
						((GraphicsEntity) parent).findScene().imageProp(
								bombBagTypeToAsset(bombType))) {

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

								// Get bomb spawner object
								BombSpawner sp = c
										.prop("BS", BombSpawner.class);

								// Increase the bomb count
								sp.addBombs(bombType, 1);

								// Remove the bomb bag
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
				bombImage.registrationKey(uuid);

				// Scale a bit
				bombImage.scale().set(ITEM_SCALE);

				// Set bomb order + tag
				bombImage.index(ITEM_INDEX).tag(FREE_TAG);

				// Attach to the node
				parent.attach(bombImage);
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

		// Create local grid controller
		Entity movement = new GridController().attach(AnimationRoutines
				.createGridControllerAnimationHandler(charAni));

		// The animation
		player.addProp("ANI", charAni);

		// Create new bomb spawner
		BombSpawner bs = new BombSpawner();

		// Attach remaining stuff
		player.attach(charAni, movement, bs);

		player.addProp("BS", bs);

		// Set scale
		player.scale().scaleLocal(1.5f);

		return player;
	}

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
	private GridRoutines() {
	}
}

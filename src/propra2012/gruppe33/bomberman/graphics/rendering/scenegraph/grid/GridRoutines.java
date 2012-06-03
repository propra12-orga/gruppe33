package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid;

import java.awt.Point;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.EntityFilter;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.animation.RenderedAnimation;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.animation.RenderedAnimation.AnimationEvent;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Grid;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f.Direction;
import com.indyforge.twod.engine.graphics.sprite.Animation;
import com.indyforge.twod.engine.graphics.sprite.AnimationBundle;
import com.indyforge.twod.engine.graphics.sprite.Sprite;
import com.indyforge.twod.engine.resources.assets.AssetManager;

import propra2012.gruppe33.bomberman.graphics.sprite.AnimationRoutines;

/**
 * 
 * @author Christopher Probst
 * @author Malte Schmidt
 * @author Matthias Hesse
 * 
 */
public final class GridRoutines implements GridConstants {

	/**
	 * This filter only accepts free nodes which have a velocity.
	 */
	public static final EntityFilter VELOCITY_NODE_FILTER = new EntityFilter() {

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
				.index(EXPLOSION_ORDER);
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
	 * Creates a new local knight player.
	 * 
	 * @param assetManager
	 *            The asset manager to load the knight.
	 * @param name
	 *            The name of the player.
	 * @return the created knight.
	 * @throws Exception
	 *             If an error occurs.
	 */
	public static GraphicsEntity createLocalKnight(AssetManager assetManager,
			String name) throws Exception {
		return createLocalGridPlayer(name,
				AnimationRoutines.createKnight(assetManager, 35, 35));
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
	public static GraphicsEntity createLocalGridPlayer(String name,
			AnimationBundle charAniBundle) {
		if (name == null) {
			throw new NullPointerException("name");
		}

		// Create new player
		GraphicsEntity player = new GraphicsEntity();

		// Set player name
		player.name(name);

		// Create animation
		RenderedAnimation charAni = new RenderedAnimation(charAniBundle);

		// Look north by default
		charAni.animationName(AnimationRoutines.RUN_PREFIX
				+ Direction.North.toString().toLowerCase());

		// Attach char ani
		player.attach(charAni).attach(
				new GridController().attach(AnimationRoutines
						.createGridControllerAnimationHandler(charAni)));

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
	 * Checks a single node entity to be free.
	 * 
	 * @param nodeEntity
	 *            the node entity you want to check.
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
				GraphicsEntity node = new GraphicsEntity() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					/*
					 * (non-Javadoc)
					 * 
					 * @see
					 * com.indyforge.twod.engine.graphics.rendering.scenegraph
					 * .Entity#onUpdate(float)
					 */
					@Override
					protected void onUpdate(float tpf) {
						super.onUpdate(tpf);

						/*
						 * The following code is very important. It reattaches
						 * children when they leave the nodes.
						 */

						// Iterate over all children
						for (Entity child : this) {

							if (child instanceof GraphicsEntity) {
								// Convert
								GraphicsEntity graphicsChild = (GraphicsEntity) child;

								// Get points
								Point absolute = position().point(), relative = graphicsChild
										.position().round().point(), newAbsolute = new Point(
										relative);

								// Translate the new absolute point
								newAbsolute.translate(absolute.x, absolute.y);

								// Check coords
								if (!absolute.equals(newAbsolute)) {

									// New point valid ??
									if (grid.inside(newAbsolute)) {
										// Lookup other child
										Entity otherChild = parent().childAt(
												grid.index(newAbsolute));

										// Attach to it
										otherChild.attach(child);

										// Change position
										graphicsChild.position().x -= relative.x;
										graphicsChild.position().y -= relative.y;
									}
								}
							}
						}
					}
				};

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

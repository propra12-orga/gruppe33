package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid;

import java.awt.Point;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Grid;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Mathf;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f.Direction;
import com.indyforge.twod.engine.util.FilteredIterator;

/**
 * This class manages the grid movement. If you attach this entity to an entity
 * the entity will react (process movement) when the given keys are pressed.
 * 
 * @author Christopher Probst
 * @author Malte Schmidt
 * @author Matthias Hesse
 */
public final class GridController extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The "equals"-threshold.
	 */
	public static final float THRESHOLD = 0.01f;

	/*
	 * Important state variables. Used to decide when to change the key
	 * priority.
	 */
	private float lv = 0, lh = 0;

	/*
	 * Used to change the velocity per entity.
	 */
	private float velocityMultiplier = 1.0f;

	// A local movement var
	private final Vector2f movement = Vector2f.zero();

	// Stores the moving state of this entity
	private boolean moving = false;

	// Stores the last direction (useful for sprite animation)
	private Direction direction = Direction.North;

	// The input map
	private final Map<Direction, Boolean> inputMap = new EnumMap<Direction, Boolean>(
			Direction.class);

	/**
	 * @return the input map of this controller.
	 */
	public Map<Direction, Boolean> inputMap() {
		return inputMap;
	}

	/**
	 * @param dir
	 *            The direction.
	 * @return true if the input map is set for the given direction, otherwise
	 *         false.
	 */
	public boolean hasInputFor(Direction dir) {
		Boolean value = inputMap.get(dir);
		return value != null ? value : false;
	}

	/**
	 * @return the last direction of the entity.
	 */
	public Direction direction() {
		return direction;
	}

	/**
	 * @return the moving flag.
	 */
	public boolean isMoving() {
		return moving;
	}

	/**
	 * @return the velocity multiplier.
	 */
	public float velocityMultiplier() {
		return velocityMultiplier;
	}

	/**
	 * Sets the velocity multiplier.
	 * 
	 * @param velocityMultiplier
	 *            The new multiplier you want to set.
	 */
	public void velocityMultiplier(float velocityMultiplier) {
		this.velocityMultiplier = velocityMultiplier;
	}

	private void processMovement(boolean negative, boolean vertical,
			GraphicsEntity gridEntity, GraphicsEntity node,
			GraphicsEntity graphicsEntity, Vector2f pos, float maxSpeed,
			Vector2f dest, float tpf) {

		// Get center vector
		Vector2f center = pos.round();

		// Get point
		Point nearest = center.point();

		// Calc dominant site
		boolean negativeDominant = (vertical ? center.x - pos.x : center.y
				- pos.y) > 0;

		// Get the grid
		Grid grid = gridEntity.typeProp(Grid.class);

		// Calc direction
		int dir = negative ? -1 : 1;

		// Calc the max velocity using tpf, maxSpeed and the local multiplier
		float maxMovement = tpf * maxSpeed * velocityMultiplier,

		// Calc the movement based on the grid
		movement = dir
				* GridRoutines.lineOfSight(gridEntity,
						GridRoutines.VELOCITY_NODE_FILTER, pos, maxMovement,
						vertical ? (negative ? Direction.North
								: Direction.South) : (negative ? Direction.West
								: Direction.East));

		// Is the entity already centered ?
		boolean isCentered = vertical ? pos.xThreshold(center.x, THRESHOLD)
				: pos.yThreshold(center.y, THRESHOLD);

		if (isCentered) {

			// Simply apply the movement
			if (vertical) {
				dest.y += movement;
			} else {
				dest.x += movement;
			}
		} else {

			// The offset
			int offset = 0;

			/*
			 * Test the border-fields to be free.
			 */
			Point a = new Point(nearest.x + (vertical ? offset : dir),
					nearest.y + (vertical ? dir : offset));

			if (!GridRoutines.VELOCITY_NODE_FILTER.accept(gridEntity
					.childAt(grid.index(a)))) {

				// Recalc the offset
				offset += negativeDominant ? -1 : 1;

				a = new Point(nearest.x + (vertical ? offset : dir), nearest.y
						+ (vertical ? dir : offset));

				// Still no luck =(
				if (!GridRoutines.VELOCITY_NODE_FILTER.accept(gridEntity
						.childAt(grid.index(a)))) {
					return;
				}
			}

			// Calc the field of interest
			Vector2f fieldOfInterest = new Vector2f(nearest.x
					+ (vertical ? offset : 0), nearest.y
					+ (vertical ? 0 : offset));

			// Recalc the distance
			float centerDistance = vertical ? fieldOfInterest.x - pos.x
					: fieldOfInterest.y - pos.y;

			// Calc the orthogonal movement
			float orthogonalMovement = (centerDistance >= 0 ? 1f : -1f)
					* maxMovement;

			// Limit the speed...
			if (Math.abs(centerDistance) < tpf * maxSpeed) {
				orthogonalMovement = centerDistance;
			}

			// Move orthogonal
			if (vertical) {
				dest.x += orthogonalMovement;
			} else {
				dest.y += orthogonalMovement;
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

		/*
		 * LOOKUP THE CONTROLLED PARENT, THE NODE AND THE GRID ENTITY!
		 */

		// Get and filter parents...
		Iterator<Entity> parents = new FilteredIterator<Entity>(
				GraphicsEntity.TYPE_FILTER, parentIterator(false));

		if (!parents.hasNext()) {
			return;
		}

		// Convert parent
		GraphicsEntity controlledParent = (GraphicsEntity) parents.next();

		if (!parents.hasNext()) {
			return;
		}

		// Convert node
		GraphicsEntity node = (GraphicsEntity) parents.next();

		if (!parents.hasNext()) {
			return;
		}

		// Convert grid entity
		GraphicsEntity gridEntity = (GraphicsEntity) parents.next();

		/*
		 * PROCESS THE MOVEMENT!
		 */

		// The node position
		Vector2f offset = node.position();

		// The position of the controlled parent
		Vector2f position = controlledParent.position();

		// The absolute position
		Vector2f absolutePosition = offset.add(position);

		// Look up the speed on the given field
		Float maxSpeedObj = node.typeProp(Float.class);

		// Check for speed property
		if (maxSpeedObj == null) {
			throw new IllegalStateException("The node " + node
					+ " does not have a speed property.");
		}

		// Reduce boxing...
		float maxSpeed = maxSpeedObj.floatValue();

		// Init the input flags
		boolean north = hasInputFor(Direction.North), south = hasInputFor(Direction.South), west = hasInputFor(Direction.West), east = hasInputFor(Direction.East);

		// Set zero
		movement.setZero();

		// Check both axes
		boolean vertical = north != south, horizontal = west != east;

		/*
		 * Never process two axes at the same time.
		 */
		if (vertical != horizontal) {

			if (vertical) {
				// Calc vertical movement
				processMovement(north, true, gridEntity, node,
						controlledParent, absolutePosition, maxSpeed, movement,
						tpf);
			} else {
				// Calc horizontal movement
				processMovement(west, false, gridEntity, node,
						controlledParent, absolutePosition, maxSpeed, movement,
						tpf);
			}
		} else if (vertical) {

			/*
			 * Well, both axes at the same time is complicated. To provide a
			 * nice gameplay we should work with priorities. We do this with
			 * checking the last direction of our entity. This direction has
			 * higher priority as long as the entity has not walked one raster
			 * size. After this the other axes gets higher priority. It is not
			 * perfect but a beginning.
			 */
			switch (direction) {
			case North:
			case South:

				if (lv >= 1.0f) {

					// Calc horizontal movement
					processMovement(west, false, gridEntity, node,
							controlledParent, absolutePosition, maxSpeed,
							movement, tpf);
				} else {
					// Calc vertical movement
					processMovement(north, true, gridEntity, node,
							controlledParent, absolutePosition, maxSpeed,
							movement, tpf);
				}

				break;

			case West:
			case East:

				if (lh >= 1.0f) {

					// Calc vertical movement
					processMovement(north, true, gridEntity, node,
							controlledParent, absolutePosition, maxSpeed,
							movement, tpf);

				} else {
					// Calc horizontal movement
					processMovement(west, false, gridEntity, node,
							controlledParent, absolutePosition, maxSpeed,
							movement, tpf);
				}

				break;
			}
		}

		// Limit the x velocity
		movement.x = Mathf.absClamp(movement.x, 0f, maxSpeed * tpf);

		// Limit the y veloctiy
		movement.y = Mathf.absClamp(movement.y, 0f, maxSpeed * tpf);

		// Calc thresholds
		boolean xT = movement.xThreshold(0, THRESHOLD), yT = movement
				.yThreshold(0, THRESHOLD);

		// Look if at least one component is 0
		if (xT || yT) {

			// Set to true
			moving = true;

			if (!xT) {
				if (movement.x >= 0) {
					direction = Direction.East;
				} else {
					direction = Direction.West;
				}

				// Add raster distance
				lv = 0;
				lh += Math.abs(movement.x);
			} else if (!yT) {
				if (movement.y >= 0) {
					direction = Direction.South;
				} else {
					direction = Direction.North;
				}

				// Add raster distance
				lh = 0;
				lv += Math.abs(movement.y);
			} else {
				// This entity is not moving anymore
				moving = false;
			}

			// Finally add the new movement component
			controlledParent.position().addLocal(movement);
		}
	}
}

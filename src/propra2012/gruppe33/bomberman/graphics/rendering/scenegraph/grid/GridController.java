package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.Set;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.Grid.Direction;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.GraphicsEntity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Mathf;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Vector2f;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.util.ParentIterator;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.util.SiblingIterator;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.util.TypeFilter;
import propra2012.gruppe33.engine.util.FilteredIterator;

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
	private boolean moving = false, lastMoving = false;

	// Stores the last direction (useful for sprite animation)
	private Direction direction = Direction.North;

	// Stores the last direction of the grid controller
	private Direction lastDirection = null;

	// The cached grid instance
	private Grid grid;

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

	/**
	 * Processes the given input to calculate the next move.
	 * 
	 * @param negative
	 *            The direction.
	 * @param vertical
	 *            The axis.
	 * @param graphicsEntity
	 *            The entity.
	 * @param grid
	 *            The grid.
	 * @param nearest
	 *            The nearest field.
	 * @param maxSpeed
	 *            The max speed on this field.
	 * @param dest
	 *            The vector in which the results are written.
	 * @param tpf
	 *            The time passed since last frame.
	 * @param movementLineOfSight
	 *            The valid movement line-of-sight char-set.
	 */
	private void processMovement(boolean negative, boolean vertical,
			GraphicsEntity graphicsEntity, Grid grid, Point nearest,
			float maxSpeed, Vector2f dest, float tpf,
			Set<Character> movementLineOfSight) {

		// Get center vector
		Vector2f center = grid.vectorAt(nearest);

		// Calc dominant site
		boolean negativeDominant = (vertical ? center.x
				- graphicsEntity.position().x : center.y
				- graphicsEntity.position().y) > 0;

		// Calc direction
		int dir = negative ? -1 : 1;

		// Calc the max velocity using tpf, maxSpeed and the local multiplier
		float maxMovement = tpf * maxSpeed * velocityMultiplier,

		// Calc the movement based on the grid
		movement = dir
				* grid.lineOfSight(graphicsEntity.position(), maxMovement,
						vertical ? (negative ? Direction.North
								: Direction.South) : (negative ? Direction.West
								: Direction.East), movementLineOfSight);

		// Is the entity already centered ?
		boolean isCentered = vertical ? graphicsEntity.position().xThreshold(
				center.x, THRESHOLD) : graphicsEntity.position().yThreshold(
				center.y, THRESHOLD);

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
			 * Test the upper, left-upper, right-upper field to be free.
			 */
			if (!movementLineOfSight.contains(grid.charAt(nearest.x
					+ (vertical ? offset : dir), nearest.y
					+ (vertical ? dir : offset)))) {

				// Recalc the offset
				offset += negativeDominant ? -1 : 1;

				// Still no luck =(
				if (!movementLineOfSight.contains(grid.charAt(nearest.x
						+ (vertical ? offset : dir), nearest.y
						+ (vertical ? dir : offset)))) {
					return;
				}
			}

			// Calc the field of interest
			Vector2f fieldOfInterest = grid.vectorAt(nearest.x
					+ (vertical ? offset : 0), nearest.y
					+ (vertical ? 0 : offset));

			// Recalc the distance
			float centerDistance = vertical ? fieldOfInterest.x
					- graphicsEntity.position().x : fieldOfInterest.y
					- graphicsEntity.position().y;

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

	public Grid grid() {
		return grid;
	}

	public void grid(Grid grid) {
		this.grid = grid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity#onUpdate
	 * (float)
	 */
	@Override
	protected void onUpdate(float tpf) {

		// Nothing to process...
		if (parent() == null || !(parent() instanceof GraphicsEntity)) {
			return;
		}

		// Convert parent
		GraphicsEntity controlledParent = (GraphicsEntity) parent();

		// Find the grid instance
		if (grid == null) {
			Iterator<Entity> itr = new FilteredIterator<Entity>(new TypeFilter(
					Grid.class, false), new ParentIterator(controlledParent,
					true));

			if (!itr.hasNext()) {
				return;
			}

			// Get next entity
			grid = (Grid) itr.next();
		}

		// Find nearest grid
		Point nearest = grid.validate(grid.worldToNearestPoint(controlledParent
				.position()));

		// Are we inside the grid ?
		if (nearest == null) {
			throw new IllegalStateException("Entity grid position invalid: "
					+ "Entity position out of grid.");
		}

		// Lookup the max speed on this field
		Float maxSpeedObj = grid.getMaxFieldVelocities().get(
				grid.charAt(nearest));

		// Check
		if (maxSpeedObj == null) {
			throw new IllegalStateException("Entity grid position invalid: "
					+ "Field char does not have a velocity.");
		}

		// Reduce boxing...
		float maxSpeed = maxSpeedObj.floatValue();

		// Init the input flags
		boolean north = grid.isPressed(KeyEvent.VK_UP), south = grid
				.isPressed(KeyEvent.VK_DOWN), west = grid
				.isPressed(KeyEvent.VK_LEFT), east = grid
				.isPressed(KeyEvent.VK_RIGHT);

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
				processMovement(north, true, controlledParent, grid, nearest,
						maxSpeed, movement, tpf,
						grid.getDefaultLineOfSightChars());
			} else {

				// Calc horizontal movement
				processMovement(west, false, controlledParent, grid, nearest,
						maxSpeed, movement, tpf,
						grid.getDefaultLineOfSightChars());
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

				if (lv >= grid.getRasterHeight()) {

					// Calc horizontal movement
					processMovement(west, false, controlledParent, grid,
							nearest, maxSpeed, movement, tpf,
							grid.getDefaultLineOfSightChars());
				} else {
					// Calc vertical movement
					processMovement(north, true, controlledParent, grid,
							nearest, maxSpeed, movement, tpf,
							grid.getDefaultLineOfSightChars());
				}

				break;

			case West:
			case East:

				if (lh >= grid.getRasterHeight()) {

					// Calc vertical movement
					processMovement(north, true, controlledParent, grid,
							nearest, maxSpeed, movement, tpf,
							grid.getDefaultLineOfSightChars());

				} else {
					// Calc horizontal movement
					processMovement(west, false, controlledParent, grid,
							nearest, maxSpeed, movement, tpf,
							grid.getDefaultLineOfSightChars());
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

		// Does the direction changed ?
		if (lastDirection != direction) {

			// Save the change
			lastDirection = direction;

			// Fire event
			fireEvent(new SiblingIterator(controlledParent, true),
					Event.DirectionChanged, this);
		}

		// Does the moving changed
		if (lastMoving != moving) {
			// Save the change
			lastMoving = moving;

			// Fire event
			fireEvent(new SiblingIterator(controlledParent, true),
					Event.MovingChanged, this);
		}
	}

	public enum Event {
		DirectionChanged, MovingChanged
	}
}

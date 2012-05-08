package propra2012.gruppe33.graphics.rendering.scenegraph.grid;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.Set;

import propra2012.gruppe33.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.graphics.rendering.scenegraph.EntityControllerAdapter;
import propra2012.gruppe33.graphics.rendering.scenegraph.Mathf;
import propra2012.gruppe33.graphics.rendering.scenegraph.Vector2f;
import propra2012.gruppe33.graphics.rendering.scenegraph.animation.AnimationController;
import propra2012.gruppe33.graphics.rendering.scenegraph.grid.Grid.Direction;
import propra2012.gruppe33.graphics.sprite.Animation;

/**
 * This class manages the grid movement. If you attach this controller to an
 * entity the entity will react (process movement) when the given keys are
 * pressed.
 * 
 * @author Christopher Probst
 * 
 */
public final class GridController extends EntityControllerAdapter {

	public static final float THRESHOLD = 0.01f;

	public static final String RUNNING_ANIMATION = "run_";

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

	/**
	 * @return the last direction of the entity.
	 */
	public Direction getDirection() {
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
	public float getVelocityMultiplier() {
		return velocityMultiplier;
	}

	/**
	 * Sets the velocity multiplier.
	 * 
	 * @param velocityMultiplier
	 *            The new multiplier you want to set.
	 */
	public void setVelocityMultiplier(float velocityMultiplier) {
		this.velocityMultiplier = velocityMultiplier;
	}

	/**
	 * Processes the given input to calculate the next move.
	 * 
	 * @param negative
	 *            The direction.
	 * @param vertical
	 *            The axis.
	 * @param entity
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
	 */
	private void processMovement(boolean negative, boolean vertical,
			Entity entity, Grid grid, Point nearest, float maxSpeed,
			Vector2f dest, float tpf) {

		// Lookup all valid chars
		Set<Character> validChars = grid.getMaxFieldVelocities().keySet();

		// Get center vector
		Vector2f center = grid.vectorAt(nearest);

		// Calc dominant site
		boolean negativeDominant = (vertical ? center.x
				- entity.getPosition().x : center.y - entity.getPosition().y) > 0;

		// Calc direction
		int dir = negative ? -1 : 1;

		// Calc the max velocity using tpf, maxSpeed and the local multiplier
		float maxMovement = tpf * maxSpeed * velocityMultiplier,

		// Calc the movement based on the grid
		movement = dir
				* grid.lineOfSight(entity.getPosition(), maxMovement,
						vertical ? (negative ? Direction.North
								: Direction.South) : (negative ? Direction.West
								: Direction.East), validChars);

		// Is the entity already centered ?
		boolean isCentered = vertical ? entity.getPosition().xThreshold(
				center.x, THRESHOLD) : entity.getPosition().yThreshold(
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
			if (!validChars.contains(grid.charAt(nearest.x
					+ (vertical ? offset : dir), nearest.y
					+ (vertical ? dir : offset)))) {

				// Recalc the offset
				offset += negativeDominant ? -1 : 1;

				// Still no luck =(
				if (!validChars.contains(grid.charAt(nearest.x
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
					- entity.getPosition().x : fieldOfInterest.y
					- entity.getPosition().y;

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
	 * propra2012.gruppe33.graphics.rendering.scenegraph.EntityController#doUpdate
	 * (propra2012.gruppe33.graphics.rendering.scenegraph.Entity, float)
	 */
	@Override
	public void doUpdate(Entity entity, float tpf) {

		// Get upper grid
		Grid grid = entity.findParentByClass(Grid.class);

		// Find nearest grid
		Point nearest = grid.validate(grid.worldToNearestPoint(entity
				.getPosition()));

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
					+ "Field char does not represent a "
					+ "field on which you can go.");
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
				processMovement(north, true, entity, grid, nearest, maxSpeed,
						movement, tpf);
			} else {

				// Calc horizontal movement
				processMovement(west, false, entity, grid, nearest, maxSpeed,
						movement, tpf);
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
					processMovement(west, false, entity, grid, nearest,
							maxSpeed, movement, tpf);
				} else {
					// Calc vertical movement
					processMovement(north, true, entity, grid, nearest,
							maxSpeed, movement, tpf);
				}

				break;

			case West:
			case East:

				if (lh >= grid.getRasterHeight()) {

					// Calc vertical movement
					processMovement(north, true, entity, grid, nearest,
							maxSpeed, movement, tpf);

				} else {
					// Calc horizontal movement
					processMovement(west, false, entity, grid, nearest,
							maxSpeed, movement, tpf);
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
			entity.getPosition().addLocal(movement);
		}

		/*
		 * If this entity has an animation controller let us update the image.
		 */
		AnimationController ani = entity
				.getController(AnimationController.class);

		// Is there an animation controller ?
		if (ani != null) {
			if (last != direction) {
				last = direction;

				// Set active animation
				ani.setAnimation(RUNNING_ANIMATION
						+ last.toString().toLowerCase(), true);
			}

			// Lookup the animation
			Animation running = ani.getAnimationMap().get(ani.getAnimation());

			// Update running state
			if (running != null) {
				running.setPaused(!moving);
			}
		}
	}

	private Direction last = null;
}

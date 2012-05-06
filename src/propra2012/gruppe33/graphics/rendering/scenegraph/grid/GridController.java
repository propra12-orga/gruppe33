package propra2012.gruppe33.graphics.rendering.scenegraph.grid;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;

import propra2012.gruppe33.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.graphics.rendering.scenegraph.EntityController;
import propra2012.gruppe33.graphics.rendering.scenegraph.Vector2f;
import propra2012.gruppe33.graphics.rendering.scenegraph.grid.Grid.Direction;

/**
 * 
 * TODO: Doc.
 * 
 * @author Christopher Probst
 * 
 */
public class GridController implements EntityController {

	public static final float THRESHOLD = 0.001f;

	private Vector2f velocity;
	private final Vector2f movement = Vector2f.zero();
	private boolean moving = false;
	private Direction direction = Direction.North;

	public GridController(Vector2f velocity) {
		setVelocity(velocity);
	}

	public Vector2f getVelocity() {
		return velocity;
	}

	public Direction getDirection() {
		return direction;
	}

	public boolean isMoving() {
		return moving;
	}

	public void setVelocity(Vector2f velocity) {
		if (velocity == null) {
			throw new NullPointerException("velocity");
		}
		this.velocity = velocity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * propra2012.gruppe33.graphics.rendering.scenegraph.EntityController#doRender
	 * (propra2012.gruppe33.graphics.rendering.scenegraph.Entity,
	 * java.awt.Graphics2D, java.awt.Graphics2D)
	 */
	@Override
	public void doRender(Entity entity, Graphics2D original,
			Graphics2D transformed) {

	}

	protected void processMovement(boolean negative, boolean vertical,
			Entity entity, Grid grid, Point nearest, Vector2f dest,
			Vector2f maxVelocity, float tpf) {

		// Get center vector
		Vector2f center = grid.vectorAt(nearest);

		// Calc dominant site
		boolean negativeDominant = (vertical ? center.x
				- entity.getPosition().x : center.y - entity.getPosition().y) > 0;

		// Calc direction
		int dir = negative ? -1 : 1;

		// Calc desired velocity
		float maxMovement = tpf * (vertical ? maxVelocity.y : maxVelocity.x), movement = dir
				* grid.lineOfSight(entity.getPosition(), maxMovement,
						vertical ? (negative ? Direction.North
								: Direction.South) : (negative ? Direction.West
								: Direction.East), '0');

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
			if (grid.charAt(nearest.x + (vertical ? offset : dir), nearest.y
					+ (vertical ? dir : offset)) != '0') {

				// Recalc the offset
				offset += negativeDominant ? -1 : 1;

				// Still no luck =(
				if (grid.charAt(nearest.x + (vertical ? offset : dir),
						nearest.y + (vertical ? dir : offset)) != '0') {
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

			// Calc the relevant velocity
			float relevantVelocity = (vertical ? maxVelocity.x : maxVelocity.y);

			// Calc the orthogonal movement
			float orthogonalMovement = (centerDistance >= 0 ? 1f : -1f) * tpf
					* relevantVelocity;

			// Limit the speed...
			if (Math.abs(centerDistance) < tpf * relevantVelocity) {
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
		Grid grid = entity.findParentEntity(Grid.class);

		// Find nearest grid
		Point nearest = grid.worldToNearestPoint(entity.getPosition());

		// Init the input flags
		boolean north = grid.isPressed(KeyEvent.VK_UP), south = grid
				.isPressed(KeyEvent.VK_DOWN), west = grid
				.isPressed(KeyEvent.VK_LEFT), east = grid
				.isPressed(KeyEvent.VK_RIGHT);

		// Set zero
		movement.setZero();

		if (north != south) {
			// Calc vertical movement
			processMovement(north, true, entity, grid, nearest, movement,
					velocity, tpf);
		}

		if (west != east) {
			// Calc horizontal movement
			processMovement(west, false, entity, grid, nearest, movement,
					velocity, tpf);
		}

		// Limit the x velocity
		movement.x = Vector2f.absClamp(movement.x, 0f, velocity.x * tpf);

		// Limit the y veloctiy
		movement.y = Vector2f.absClamp(movement.y, 0f, velocity.y * tpf);

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
			} else if (!yT) {
				if (movement.y >= 0) {
					direction = Direction.South;
				} else {
					direction = Direction.North;
				}
			} else {
				// This entity is not moving anymore
				moving = false;
			}

			// Finally add the new movement component
			entity.getPosition().addLocal(movement);
		}
	}
}

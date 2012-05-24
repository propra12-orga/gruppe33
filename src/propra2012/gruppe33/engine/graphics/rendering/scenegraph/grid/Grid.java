package propra2012.gruppe33.engine.graphics.rendering.scenegraph.grid;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.EntityFilter;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.GraphicsEntity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Vector2f;

/**
 * A grid entity is basically a usual graphics entity but has some useful
 * features to simplify grid management.
 * 
 * @author Christopher Probst
 * @author Malte Schmidt
 */
public class Grid extends GraphicsEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The map raster
	private final int rasterX, rasterY;

	// Here we store the grid entities
	private final Map<Point, Set<GraphicsEntity>> gridEntities = new HashMap<Point, Set<GraphicsEntity>>();

	/**
	 * Creates a new grid using the given parameters.
	 * 
	 * @param rasterX
	 *            The x raster of the grid.
	 * @param rasterY
	 *            The y raster of the grid.
	 */
	public Grid(int rasterX, int rasterY) {

		if (rasterX <= 0) {
			throw new IllegalArgumentException("rasterX must be > 0");
		} else if (rasterY <= 0) {
			throw new IllegalArgumentException("rasterY must be > 0");
		}

		// Set raster x and y
		this.rasterX = rasterX;
		this.rasterY = rasterY;
	}

	/**
	 * Moves and validates a location against the valid range.
	 * 
	 * @param location
	 *            The location you want to validate or null.
	 * @param x
	 *            The x amount yout want to add.
	 * @param y
	 *            The y amount yout want to add.
	 * @return the moved location if the location is valid, otherwise null.
	 */
	public Point moveAndValidate(Point location, int x, int y) {
		location.move(x, y);
		return validate(location);
	}

	/**
	 * Validates a location against the valid range.
	 * 
	 * @param location
	 *            The location you want to validate or null.
	 * @return the same location if the location is valid, otherwise null.
	 */
	public Point validate(Point location) {
		return location != null ? (location.x < rasterX && location.x >= 0
				&& location.y < rasterY && location.y >= 0 ? location : null)
				: null;
	}

	/**
	 * An enum which contains all four directions.
	 * 
	 * @author Christopher Probst
	 * 
	 */
	public enum Direction {
		North, South, East, West
	}

	/**
	 * This is one of the most important methods when dealing with movement. It
	 * calculates the line-of-sight using the given direction.
	 * 
	 * @param position
	 *            The world position.
	 * @param max
	 *            The max distance. Please note that the method runs faster if
	 *            you use a short distance since this value affects a loop.
	 * @param direction
	 *            The {@link Direction} of the test.
	 * @param entityFilter
	 *            The filter of valid entities which will not affect the line of
	 *            sight.
	 * @return the line-of-sight in world space.
	 */
	public float lineOfSight(Vector2f position, float max, Direction direction,
			EntityFilter entityFilter) {

		// Get nearest
		Vector2f nearest = position.nearest();

		// As point and validate
		Point point = validate(nearest.point());

		// Position is outside the grid
		if (point == null) {
			throw new IllegalArgumentException("Position out of grid");
		}

		// The return value and the increase value
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

		// Validate the new position and check for valid chars
		while (moveAndValidate(point, offx, offy) != null && distance < max) {

			// Try to filter the entities
			if (!filterAt(entityFilter, point)) {
				break;
			}

			// Increase distance
			distance += 1.0f;
		}

		// Limit the result
		if (distance > max) {
			distance = max;
		}

		return distance;
	}

	/**
	 * Filters all grid entities at the specifies position.
	 * 
	 * @param entityFilter
	 *            The entity filter or null.
	 * @param location
	 *            The location.
	 * @return true if the entity filter is null or all entities at the
	 *         specified position were accepted, otherwise false.
	 */
	public boolean filterAt(EntityFilter entityFilter, Point location) {
		if (location == null) {
			throw new NullPointerException("location");
		}
		if (entityFilter != null) {

			// Check all entities
			for (GraphicsEntity gridEntity : entitiesAt(location)) {

				// If even one entity is not accepted stop here!
				if (!entityFilter.accept(gridEntity)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @param location
	 *            The location.
	 * @return the entity set at the specified location.
	 */
	public Set<GraphicsEntity> entitiesAt(Point location) {
		if (location == null) {
			throw new NullPointerException("location");
		}
		// Lookup
		Set<GraphicsEntity> entities = gridEntities.get(location);

		// Lazy creation
		if (entities == null) {
			gridEntities.put(location,
					entities = new LinkedHashSet<GraphicsEntity>());
		}

		return entities;
	}

	/**
	 * @return the rasterX.
	 */
	public int rasterX() {
		return rasterX;
	}

	/**
	 * @return the rasterY.
	 */
	public int rasterY() {
		return rasterY;
	}

	/**
	 * @return the raster as vector.
	 */
	public Vector2f rasterAsVector() {
		return new Vector2f(rasterX, rasterY);
	}

	/**
	 * Returns all points which are affected by the given distance. The method
	 * will cast rays in all four direction and stops when hitting a field which
	 * contains entities which do not allow collecting. You can specify this
	 * behaviour by providing an entity filter.
	 * 
	 * @param location
	 *            The location of the center.
	 * @param distance
	 *            The max distance of collection.
	 * @param entityFilter
	 *            The filter of valid entities which will be collected.
	 * @return a list which contains all points.
	 */
	public List<Point> collectPoints(Point location, int distance,
			EntityFilter entityFilter) {
		if (validate(location) != null) {
			throw new IllegalArgumentException("Location out of grid");
		}

		// Create a new array list
		List<Point> points = new ArrayList<Point>();

		// Add local point
		points.add(location);

		// Tells whether or not the given dirs are valid
		boolean nv = true, sv = true, wv = true, ev = true;

		// Tmp var
		Point p;

		// Iterate
		for (int i = 1; i < distance; i++) {

			// North
			if ((p = moveAndValidate(new Point(location), 0, -i)) != null
					&& filterAt(entityFilter, p) && nv) {
				points.add(p);
			} else {
				nv = false;
			}

			// South
			if ((p = moveAndValidate(new Point(location), 0, i)) != null
					&& filterAt(entityFilter, p) && sv) {
				points.add(p);
			} else {
				sv = false;
			}

			// West
			if ((p = moveAndValidate(new Point(location), -i, 0)) != null
					&& filterAt(entityFilter, p) && wv) {
				points.add(p);
			} else {
				wv = false;
			}

			// East
			if ((p = moveAndValidate(new Point(location), i, 0)) != null
					&& filterAt(entityFilter, p) && ev) {
				points.add(p);
			} else {
				ev = false;
			}
		}

		return points;
	}
}

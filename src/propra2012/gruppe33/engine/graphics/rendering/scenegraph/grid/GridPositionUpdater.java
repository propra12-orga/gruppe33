package propra2012.gruppe33.engine.graphics.rendering.scenegraph.grid;

import java.awt.Point;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.GraphicsEntity;

/**
 * This entity manages the grid coords. When you attach this entity to an entity
 * which is attached to a {@link Grid} the grid will be informed by this entity
 * when the grid position (nearest integer position) changed. Please note that
 * the parent should be able to fire attach/detach events. Otherwise this entity
 * will not work correctly.
 * 
 * @author Christopher Probst
 * 
 */
public class GridPositionUpdater extends Entity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @author Christopher Probst
	 * 
	 */
	public enum GridEvent {
		PositionChanged
	}

	// The last point
	private Point lastPoint;

	// The grid instance
	private Grid grid;

	/**
	 * Validates the last point and removes the parent of this entity from the
	 * field set.
	 */
	private void removeFromLastField() {
		// Validate last point
		if (grid.validate(lastPoint) != null) {

			// Remove from old grid field
			grid.entitiesAt(lastPoint).remove((GraphicsEntity) parent());
		}
	}

	/**
	 * Validates the new point and adds the parent to the field set. The last
	 * point will be updated by this method.
	 * 
	 * @param newPoint
	 *            The new point of the parent.
	 */
	private void addToNewField(Point newPoint) {
		// Validate new point
		if (grid.validate(newPoint) != null) {

			// Add to new grid field
			grid.entitiesAt(newPoint).add((GraphicsEntity) parent());
		}

		// Save to last point
		lastPoint = newPoint;
	}

	/**
	 * Ensures that the grid is valid. If the hierarchy is not valid the grid
	 * will be set to null.
	 * 
	 * @return true if the grid var is valid, otherwise false.
	 */
	private boolean ensureGrid() {
		if (grid != null) {
			return true;
		} else {

			// Detach if wrong parent type...
			if (!(parent() instanceof GraphicsEntity)
					|| !(parent().parent() instanceof Grid)) {
				return false;
			} else {
				// Save the grid instance
				grid = (Grid) parent().parent();

				return true;
			}
		}
	}

	/**
	 * Removes the parent from the field set, if the given grid exists. All vars
	 * are cleared after this method.
	 */
	private void clearGrid() {
		// Do we have a grid ?
		if (grid != null) {
			// Remove from last field
			removeFromLastField();

			// Clear grid instance
			grid = null;

			// Clear last point
			lastPoint = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity#onEvent
	 * (java.lang.Object, java.lang.Object[])
	 */
	@Override
	protected void onEvent(Entity source, Object event, Object... params) {
		super.onEvent(source, event, params);

		if (event instanceof GridEvent) {
			switch ((GridEvent) event) {
			case PositionChanged:
				// Simply invoke the event method
				onPositionChanged((GridPositionUpdater) source,
						(Point) params[0], (Point) params[1]);
				break;
			}
		}
	}

	/**
	 * OVERRIDE FOR CUSTOM BEHAVIOUR.
	 * 
	 * This method is called when the observed parent entity changes the grid
	 * position.
	 * 
	 * @param gridPositionUpdater
	 *            The updater.
	 * @param from
	 *            The last point.
	 * @param to
	 *            The latest point.
	 */
	protected void onPositionChanged(GridPositionUpdater gridPositionUpdater,
			Point from, Point to) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity#
	 * onParentDetached()
	 */
	@Override
	protected void onParentDetached() {
		super.onParentDetached();

		// Crear grid when parent detached
		clearGrid();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity#onDetached
	 * ()
	 */
	@Override
	protected void onDetached() {
		super.onDetached();

		// Clear grid when detached
		clearGrid();
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
		super.onUpdate(tpf);

		// First ensure the grid
		if (ensureGrid()) {

			// Convert parent
			GraphicsEntity graphicsParent = (GraphicsEntity) parent();

			// Calc new point
			Point newPoint = graphicsParent.position().nearest().point();

			// Check position
			if (!newPoint.equals(lastPoint)) {

				// Save last point
				Point ptr = lastPoint;

				// Remove from last field
				removeFromLastField();

				// Add to new field
				addToNewField(newPoint);

				// Fire an event
				fireEvent(GridEvent.PositionChanged, ptr, newPoint);
			}
		}
	}

	public GridPositionUpdater() {
		events().put(GridEvent.PositionChanged, iterableChildren(true, true));
	}
}

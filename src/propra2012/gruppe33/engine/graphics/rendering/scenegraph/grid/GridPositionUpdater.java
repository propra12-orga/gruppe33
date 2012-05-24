package propra2012.gruppe33.engine.graphics.rendering.scenegraph.grid;

import java.awt.Point;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.GraphicsEntity;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class GridPositionUpdater extends Entity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum GridPositionUpdaterEvent {
		GridPositionChanged
	}

	// The last point
	private Point lastPoint;

	// The grid instance
	private Grid grid;

	private void removeFromLastField() {
		// Validate last point
		if (grid.validate(lastPoint) != null) {

			// Remove from old grid field
			grid.entitiesAt(lastPoint).remove((GraphicsEntity) grid.parent());
		}
	}

	private void addToNewField(Point newPoint) {
		// Validate new point
		if (grid.validate(newPoint) != null) {

			// Add to new grid field
			grid.entitiesAt(newPoint).add((GraphicsEntity) grid.parent());
		}

		// Save to last point
		lastPoint = newPoint;
	}

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
	protected void onEvent(Object event, Object... params) {
		super.onEvent(event, params);

		if (event instanceof GridPositionUpdaterEvent) {
			switch ((GridPositionUpdaterEvent) event) {
			case GridPositionChanged:
				onGridPositionChanged((GridPositionUpdater) params[0],
						(Point) params[1], (Point) params[2]);
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
	protected void onGridPositionChanged(
			GridPositionUpdater gridPositionUpdater, Point from, Point to) {
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
				fireEvent(iterableEventEntities().iterator(),
						GridPositionUpdaterEvent.GridPositionChanged, this,
						ptr, newPoint);
			}
		}
	}
}

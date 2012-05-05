package propra2012.gruppe33.graphics.rendering.scenegraph.grid;

import java.awt.Graphics2D;
import java.awt.Point;

import propra2012.gruppe33.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.graphics.rendering.scenegraph.EntityController;

/**
 * 
 * TODO: Doc.
 * 
 * @author Christopher Probst
 * 
 */
public class GridController implements EntityController {

	// The location on the grid
	private Point location;

	public GridController(int x, int y) {
		this(new Point(x, y));
	}

	public GridController(Point location) {
		setLocation(location);
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		if (location == null) {
			throw new NullPointerException("location");
		}
		this.location = location;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * propra2012.gruppe33.graphics.rendering.scenegraph.EntityController#doUpdate
	 * (propra2012.gruppe33.graphics.rendering.scenegraph.Entity, float)
	 */
	@Override
	public void doUpdate(Entity entity, float tpf) {

	}
}

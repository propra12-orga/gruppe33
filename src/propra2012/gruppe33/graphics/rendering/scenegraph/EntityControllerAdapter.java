package propra2012.gruppe33.graphics.rendering.scenegraph;

import java.awt.Graphics2D;

/**
 * An abstract implementation of an entity controller.
 * 
 * @author Christopher Probst
 * @see EntityController
 */
public abstract class EntityControllerAdapter implements EntityController {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * propra2012.gruppe33.graphics.rendering.scenegraph.EntityController#onAttached
	 * (propra2012.gruppe33.graphics.rendering.scenegraph.Entity)
	 */
	@Override
	public void onAttached(Entity entity) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * propra2012.gruppe33.graphics.rendering.scenegraph.EntityController#onDetached
	 * (propra2012.gruppe33.graphics.rendering.scenegraph.Entity)
	 */
	@Override
	public void onDetached(Entity entity) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see propra2012.gruppe33.graphics.rendering.scenegraph.EntityController#
	 * onChildAttached(propra2012.gruppe33.graphics.rendering.scenegraph.Entity,
	 * propra2012.gruppe33.graphics.rendering.scenegraph.Entity)
	 */
	@Override
	public void onChildAttached(Entity entity, Entity child) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see propra2012.gruppe33.graphics.rendering.scenegraph.EntityController#
	 * onChildDetached(propra2012.gruppe33.graphics.rendering.scenegraph.Entity,
	 * propra2012.gruppe33.graphics.rendering.scenegraph.Entity)
	 */
	@Override
	public void onChildDetached(Entity entity, Entity child) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * propra2012.gruppe33.graphics.rendering.scenegraph.EntityController#onMessage
	 * (java.lang.Object, java.lang.Object[])
	 */
	@Override
	public void onMessage(Object message, Object... args) {
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

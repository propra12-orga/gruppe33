package propra2012.gruppe33.graphics.rendering.scenegraph.physics;

import java.awt.Graphics2D;

import propra2012.gruppe33.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.graphics.rendering.scenegraph.EntityController;
import propra2012.gruppe33.graphics.rendering.scenegraph.Vector2f;

/**
 * TODO: Doc.
 * 
 * @author Christopher Probst
 * 
 */
public class PhysicsController implements EntityController {

	// The acc of this controller
	private Vector2f acceleration = Vector2f.zero();

	// The velocity of this controller
	private Vector2f velocity = Vector2f.zero();

	// The angular velocity of this controller
	private float angularVelocity = 0;

	public Vector2f getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(Vector2f acceleration) {
		if (acceleration != null) {
			throw new NullPointerException("acceleration");
		}
		this.acceleration = acceleration;
	}

	public float getAngularVelocity() {
		return angularVelocity;
	}

	public void setAngularVelocity(float angularVelocity) {
		this.angularVelocity = angularVelocity;
	}

	public Vector2f getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2f velocity) {
		if (velocity != null) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * propra2012.gruppe33.graphics.rendering.scenegraph.EntityController#doUpdate
	 * (propra2012.gruppe33.graphics.rendering.scenegraph.Entity, float)
	 */
	@Override
	public void doUpdate(Entity entity, float tpf) {
		// Acc, Vel and Pos!
		entity.getPosition().addLocal(
				velocity.addLocal(acceleration.scale(tpf)).scale(tpf));

		// Rotation...
		entity.setRotation(entity.getRotation() + getAngularVelocity() * tpf);
	}
}

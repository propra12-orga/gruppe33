package propra2012.gruppe33.graphics.rendering.scenegraph.transform;

import java.awt.Graphics2D;

import propra2012.gruppe33.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.graphics.rendering.scenegraph.EntityController;
import propra2012.gruppe33.graphics.rendering.scenegraph.Vector2f;

/**
 * This class animates the transform.
 * 
 * @author Christopher Probst
 * 
 */
public class TransformController implements EntityController {

	/*
	 * The linear, angular and scale velocities/accelerations.
	 */
	private Vector2f linearVelocity = Vector2f.zero(),
			linearAcceleration = Vector2f.zero(), scaleVelocity = Vector2f
					.zero(), scaleAcceleration = Vector2f.zero();
	private float angularVeloctiy, angularAcceleration;

	/**
	 * @return the linearVelocity.
	 */
	public Vector2f getLinearVelocity() {
		return linearVelocity;
	}

	/**
	 * @param linearVelocity
	 *            The linearVelocity to set.
	 */
	public void setLinearVelocity(Vector2f linearVelocity) {
		if (linearVelocity == null) {
			throw new NullPointerException("linearVelocity");
		}
		this.linearVelocity = linearVelocity;
	}

	/**
	 * @return the linearAcceleration.
	 */
	public Vector2f getLinearAcceleration() {
		return linearAcceleration;
	}

	/**
	 * @param linearAcceleration
	 *            The linearAcceleration to set.
	 */
	public void setLinearAcceleration(Vector2f linearAcceleration) {
		if (linearAcceleration == null) {
			throw new NullPointerException("linearAcceleration");
		}
		this.linearAcceleration = linearAcceleration;
	}

	/**
	 * @return the scaleVelocity.
	 */
	public Vector2f getScaleVelocity() {
		return scaleVelocity;
	}

	/**
	 * @param scaleVelocity
	 *            The scaleVelocity to set.
	 */
	public void setScaleVelocity(Vector2f scaleVelocity) {
		if (scaleVelocity == null) {
			throw new NullPointerException("scaleVelocity");
		}
		this.scaleVelocity = scaleVelocity;
	}

	/**
	 * @return the scaleAcceleration.
	 */
	public Vector2f getScaleAcceleration() {
		return scaleAcceleration;
	}

	/**
	 * @param scaleAcceleration
	 *            The scaleAcceleration to set.
	 */
	public void setScaleAcceleration(Vector2f scaleAcceleration) {
		if (scaleAcceleration == null) {
			throw new NullPointerException("scaleAcceleration");
		}
		this.scaleAcceleration = scaleAcceleration;
	}

	/**
	 * @return the angularVeloctiy.
	 */
	public float getAngularVeloctiy() {
		return angularVeloctiy;
	}

	/**
	 * @param angularVeloctiy
	 *            The angularVeloctiy to set.
	 */
	public void setAngularVeloctiy(float angularVeloctiy) {
		this.angularVeloctiy = angularVeloctiy;
	}

	/**
	 * @return the angularAcceleration.
	 */
	public float getAngularAcceleration() {
		return angularAcceleration;
	}

	/**
	 * @param angularAcceleration
	 *            The angularAcceleration to set.
	 */
	public void setAngularAcceleration(float angularAcceleration) {
		this.angularAcceleration = angularAcceleration;
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

		// Process position
		entity.getPosition().addLocal(
				linearVelocity.addLocal(linearAcceleration.scale(tpf)).scale(
						tpf));

		// Process scale
		entity.getScale()
				.addLocal(
						scaleVelocity.addLocal(scaleAcceleration.scale(tpf))
								.scale(tpf));

		// Process rotation
		entity.setRotation(entity.getRotation()
				+ (angularVeloctiy + angularAcceleration * tpf) * tpf);
	}
}

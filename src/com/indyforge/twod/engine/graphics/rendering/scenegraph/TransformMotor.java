package com.indyforge.twod.engine.graphics.rendering.scenegraph;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;

/**
 * This entity animates the transform of the parent entity.
 * 
 * @author Christopher Probst
 * 
 */
public final class TransformMotor extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * The linear, angular and scale velocities/accelerations.
	 */
	private Vector2f linearVelocity = Vector2f.zero(),
			linearAcceleration = Vector2f.zero(), scaleVelocity = Vector2f
					.zero(), scaleAcceleration = Vector2f.zero();
	private float angularVeloctiy, angularAcceleration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity#onUpdate
	 * (float)
	 */
	@Override
	protected void onUpdate(float tpf) {
		super.onUpdate(tpf);

		// Get parent
		Entity parent = parent();

		if (parent != null && parent instanceof GraphicsEntity) {

			// Convert
			GraphicsEntity graphicsParent = (GraphicsEntity) parent;

			// Process position
			graphicsParent.position().addLocal(
					linearVelocity.addLocal(linearAcceleration.scale(tpf))
							.scale(tpf));

			// Process scale
			graphicsParent.scale().addLocal(
					scaleVelocity.addLocal(scaleAcceleration.scale(tpf)).scale(
							tpf));

			// Process rotation
			graphicsParent.rotation(graphicsParent.rotation()
					+ (angularVeloctiy += angularAcceleration * tpf) * tpf);
		}
	}

	/**
	 * @return the linearVelocity.
	 */
	public Vector2f linearVelocity() {
		return linearVelocity;
	}

	/**
	 * @param linearVelocity
	 *            The linearVelocity to set.
	 * @return this for chaining.
	 */
	public TransformMotor linearVelocity(Vector2f linearVelocity) {
		if (linearVelocity == null) {
			throw new NullPointerException("linearVelocity");
		}
		this.linearVelocity = linearVelocity;
		return this;
	}

	/**
	 * @return the linearAcceleration.
	 */
	public Vector2f linearAcceleration() {
		return linearAcceleration;
	}

	/**
	 * @param linearAcceleration
	 *            The linearAcceleration to set.
	 * @return this for chaining.
	 */
	public TransformMotor linearAcceleration(Vector2f linearAcceleration) {
		if (linearAcceleration == null) {
			throw new NullPointerException("linearAcceleration");
		}
		this.linearAcceleration = linearAcceleration;
		return this;
	}

	/**
	 * @return the scaleVelocity.
	 */
	public Vector2f scaleVelocity() {
		return scaleVelocity;
	}

	/**
	 * @param scaleVelocity
	 *            The scaleVelocity to set.
	 * @return this for chaining.
	 */
	public TransformMotor scaleVelocity(Vector2f scaleVelocity) {
		if (scaleVelocity == null) {
			throw new NullPointerException("scaleVelocity");
		}
		this.scaleVelocity = scaleVelocity;
		return this;
	}

	/**
	 * @return the scaleAcceleration.
	 */
	public Vector2f scaleAcceleration() {
		return scaleAcceleration;
	}

	/**
	 * @param scaleAcceleration
	 *            The scaleAcceleration to set.
	 * @return this for chaining.
	 */
	public TransformMotor scaleAcceleration(Vector2f scaleAcceleration) {
		if (scaleAcceleration == null) {
			throw new NullPointerException("scaleAcceleration");
		}
		this.scaleAcceleration = scaleAcceleration;
		return this;
	}

	/**
	 * @return the angularVeloctiy.
	 */
	public float angularVeloctiy() {
		return angularVeloctiy;
	}

	/**
	 * @param angularVeloctiy
	 *            The angularVeloctiy to set.
	 * @return this for chaining.
	 */
	public TransformMotor angularVeloctiy(float angularVeloctiy) {
		this.angularVeloctiy = angularVeloctiy;
		return this;
	}

	/**
	 * @return the angularAcceleration.
	 */
	public float angularAcceleration() {
		return angularAcceleration;
	}

	/**
	 * @param angularAcceleration
	 *            The angularAcceleration to set.
	 * @return this for chaining.
	 */
	public TransformMotor angularAcceleration(float angularAcceleration) {
		this.angularAcceleration = angularAcceleration;
		return this;
	}
}

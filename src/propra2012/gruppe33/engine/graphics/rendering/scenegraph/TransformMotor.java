package propra2012.gruppe33.engine.graphics.rendering.scenegraph;

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
	 * propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity#onUpdate
	 * (float)
	 */
	@Override
	protected void onUpdate(float tpf) {

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
	 */
	public void linearVelocity(Vector2f linearVelocity) {
		if (linearVelocity == null) {
			throw new NullPointerException("linearVelocity");
		}
		this.linearVelocity = linearVelocity;
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
	 */
	public void linearAcceleration(Vector2f linearAcceleration) {
		if (linearAcceleration == null) {
			throw new NullPointerException("linearAcceleration");
		}
		this.linearAcceleration = linearAcceleration;
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
	 */
	public void scaleVelocity(Vector2f scaleVelocity) {
		if (scaleVelocity == null) {
			throw new NullPointerException("scaleVelocity");
		}
		this.scaleVelocity = scaleVelocity;
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
	 */
	public void scaleAcceleration(Vector2f scaleAcceleration) {
		if (scaleAcceleration == null) {
			throw new NullPointerException("scaleAcceleration");
		}
		this.scaleAcceleration = scaleAcceleration;
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
	 */
	public void angularVeloctiy(float angularVeloctiy) {
		this.angularVeloctiy = angularVeloctiy;
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
	 */
	public void angularAcceleration(float angularAcceleration) {
		this.angularAcceleration = angularAcceleration;
	}
}

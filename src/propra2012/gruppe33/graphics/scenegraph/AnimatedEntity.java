package propra2012.gruppe33.graphics.scenegraph;

public class AnimatedEntity extends Entity {

	// The acc of this entity
	private Vector2f acceleration = Vector2f.zero();

	// The velocity of this entity
	private Vector2f velocity = Vector2f.zero();

	public AnimatedEntity(String id) {
		super(id);
	}

	public Vector2f getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(Vector2f acceleration) {
		if (acceleration != null) {
			this.acceleration = acceleration;
		}
	}

	public Vector2f getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2f velocity) {
		if (velocity != null) {
			this.velocity = velocity;
		}
	}

	@Override
	public void update(float tpf) {
		super.update(tpf);

		// Acc, Vel and Pos!
		getPosition().addLocal(velocity.addLocal(acceleration.scale(tpf)));
		
		getSceneGraph().getDirtyLayers().add(getLayer().getName());
	}
}

package propra2012.gruppe33.graphics.scenegraph;

import java.awt.Graphics2D;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class Entity {

	/*
	 * The position of this entity stored as Vector2f. Initialize with zero!
	 */
	private Vector2f position = Vector2f.zero();

	/*
	 * The id of this entity.
	 */
	private final String id;

	// The layer in which this entity is rendered (Managed by layer)
	Layer layer;

	public Entity(String id) {
		if (id == null) {
			throw new NullPointerException("id");
		}

		this.id = id;
	}

	public String getId() {
		return id;
	}

	public SceneGraph getSceneGraph() {
		return layer != null ? layer.getSceneGraph() : null;
	}

	public Layer getLayer() {
		return layer;
	}

	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(Vector2f position) {
		// Do not allow null positions!
		if (position != null) {
			this.position = position;
		}
	}

	public void update(float tpf) {

	}

	public void render(Graphics2D g) {

	}
}

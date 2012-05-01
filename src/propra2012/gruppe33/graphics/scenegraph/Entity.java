package propra2012.gruppe33.graphics.scenegraph;

import java.awt.Graphics2D;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class Entity extends SceneGraphObject {

	/*
	 * The position of this entity stored as Vector2f. Initialize with zero!
	 */
	private Vector2f position = Vector2f.zero();

	// The layer in which this entity is rendered (Managed by layer)
	Layer layer;

	public Entity(SceneGraph sceneGraph, String name) {
		super(sceneGraph, name);
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

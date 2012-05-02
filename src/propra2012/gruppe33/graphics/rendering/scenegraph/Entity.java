package propra2012.gruppe33.graphics.rendering.scenegraph;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	 * The rotation of this entity.
	 */
	private float rotation = 0;

	/*
	 * The scale of this entity.
	 */
	private Vector2f scale = new Vector2f(1, 1);

	/*
	 * The id of this entity.
	 */
	private final String id;

	/*
	 * The parent entity which ones this entity.
	 */
	private Entity parent;

	/*
	 * The hash set which contains all children.
	 */
	private final Set<Entity> children = new LinkedHashSet<Entity>(),
			readOnlyChildren = Collections.unmodifiableSet(children);

	/*
	 * Maps ids to entities.
	 */
	private final Map<String, List<Entity>> ids2Entities = new HashMap<String, List<Entity>>();

	/*
	 * Used to cache iterations.
	 */
	private List<Entity> cache = null;

	/*
	 * Tells whether or not this entity and/or its children are visible.
	 */
	private boolean visible = true, childrenVisible = true;

	private List<Entity> getEntityList(String id) {
		// Try to get list
		List<Entity> entities = ids2Entities.get(id);

		if (entities == null) {
			ids2Entities.put(id, entities = new LinkedList<Entity>());
		}

		return entities;
	}

	private List<Entity> getCache() {
		return cache != null ? cache
				: (cache = new ArrayList<Entity>(children));
	}

	protected void doUpdate(float tpf) {

	}

	protected void doRender(Graphics2D g) {

	}

	public Entity(String id) {
		if (id == null) {
			throw new NullPointerException("id");
		}

		this.id = id;
	}

	public boolean isChildrenVisible() {
		return childrenVisible;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setChildrenVisible(boolean childrenVisible) {
		this.childrenVisible = childrenVisible;
	}

	public Entity findById(String id) {
		// Lookup the id list
		List<Entity> list = ids2Entities.get(id);

		return list != null ? list.get(0) : null;
	}

	public List<Entity> findAllById(String id) {
		// Lookup the id list
		List<Entity> list = ids2Entities.get(id);

		return list != null ? Collections.unmodifiableList(list) : null;
	}

	public void detachAll() {
		for (Entity child : getCache()) {
			detach(child);
		}
	}

	public Set<Entity> getChildren() {
		return readOnlyChildren;
	}

	public Entity getRootParent() {
		return parent != null ? parent.getRootParent() : this;
	}

	public Scene getScene() {
		Entity root = getRootParent();
		return root instanceof Scene ? (Scene) root : null;
	}

	public Entity getParent() {
		return parent;
	}

	public boolean attach(Entity child) {
		if (child == null) {
			throw new NullPointerException("child");
		} else if (child.parent != null && !child.detach()) {
			throw new IllegalStateException("Unable to detach child");
		}

		if (children.add(child)) {
			child.parent = this;

			// Get entity name list
			List<Entity> list = getEntityList(child.getId());

			// Just add the child
			list.add(child);

			// Cache is invalid now
			cache = null;
			return true;
		} else {
			return false;
		}
	}

	public boolean detach() {
		return parent != null ? parent.detach(this) : false;
	}

	public boolean detach(Entity child) {
		if (child == null) {
			throw new NullPointerException("child");
		} else if (child.parent != this) {
			throw new IllegalArgumentException("child must be a "
					+ "child of this entity");
		}

		if (children.remove(child)) {
			child.parent = null;

			// Get entity name list
			List<Entity> list = getEntityList(child.getId());

			// Remove the child
			list.remove(child);

			// Was this the last child with the given name ?
			if (list.isEmpty()) {
				// Remove from map
				ids2Entities.remove(child.getId());
			}

			// Cache is invalid now
			cache = null;
			return true;
		} else {
			return false;
		}
	}

	public String getId() {
		return id;
	}

	public Vector2f getScale() {
		return scale;
	}

	public void setScale(Vector2f scale) {
		if (scale != null) {
			this.scale = scale;
		}
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public Vector2f getWorldPosition() {
		// Create a copy of the position
		Vector2f result = new Vector2f(position);

		// Pointer to next parent
		Entity next = parent;

		while (next != null) {

			// Add x and y
			result.x += next.position.x * next.scale.x;
			result.y += next.position.y * next.scale.y;

			// Proceed...
			next = next.parent;
		}

		return result;
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

	public final Graphics2D transform(Graphics2D g) {
		// Translate
		g.translate(position.x, position.y);

		// Rotate the graphics
		g.rotate(rotation);

		// Scale the graphics
		g.scale(scale.x, scale.y);

		return g;
	}

	public final void update(float tpf) {

		// Do the update
		doUpdate(tpf);

		// Update all children
		for (Entity child : getCache()) {
			// Just update
			child.update(tpf);
		}
	}

	public final void render(Graphics2D g) {
		// Create a copy of the graphics and transform
		Graphics2D copy = transform((Graphics2D) g.create());
		try {

			if (visible) {
				// Render this entity
				doRender(copy);
			}

			if (childrenVisible) {
				// Render all children
				for (Entity child : getCache()) {
					// Just render
					child.render(copy);
				}
			}
		} finally {
			// Dispose the graphics
			copy.dispose();
		}
	}
}

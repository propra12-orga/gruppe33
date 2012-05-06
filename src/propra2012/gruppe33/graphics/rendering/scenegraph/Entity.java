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
 * This is the entity class. An entity is basically some kind of object in the
 * game, let it be an image, a controllable character or just a temporary
 * explosion sprite.
 * 
 * An entity has a position, scale (both stored as vector) and a rotation (which
 * is stored as float).
 * 
 * An entity can have children and a parent. Every child reacts RELATIVE to its
 * parent which means that for instance the position, scale and rotation are
 * relative to the parent values. This even applies to the render/update order.
 * This means that when you render/update an entity all children will be
 * rendered/updated in insertion order, too.
 * 
 * Some useful knowlegde:
 * 
 * - An entity has two flags (visible, childrenVisible) which determine whether
 * or not it/the children is/are drawn.
 * 
 * - An entity has a name. Entities can have the same name but this could lead
 * to bad find-operations since entities with equal names are stored in a list.
 * 
 * 
 * 
 * 
 * @author Christopher Probst
 * @see Vector2f
 * @see Scene
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
	 * The name of this entity.
	 */
	private final String name;

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
	 * Maps names to entities.
	 */
	private final Map<String, List<Entity>> names2Entities = new HashMap<String, List<Entity>>();

	/*
	 * Used to cache iterations.
	 */
	private List<Entity> cache = null;

	/*
	 * Tells whether or not this entity and/or its children are visible.
	 */
	private boolean visible = true, childrenVisible = true;

	/**
	 * Returns a valid list for a given name. The list is created if it does not
	 * exist yet.
	 * 
	 * @param name
	 *            The name of the list.
	 * @return a valid list impl.
	 */
	private List<Entity> getEntityList(String name) {
		// Try to get list
		List<Entity> entities = names2Entities.get(name);

		if (entities == null) {
			names2Entities.put(name, entities = new LinkedList<Entity>());
		}

		return entities;
	}

	/**
	 * Returns a cached list which contains all children. If it does not exist
	 * yet it will be created.
	 * 
	 * IMPORTANT:
	 * 
	 * You can iterate over the children and remove them from this entity while
	 * iterating since this method returns a different list.
	 * 
	 * @return a list which contains all children.
	 */
	private List<Entity> getCache() {
		return cache != null ? cache
				: (cache = new ArrayList<Entity>(children));
	}

	/**
	 * OVERRIDE FOR CUSTOM UPDATE BEHAVIOUR:
	 * 
	 * This method gets called every frame to update the state of this entity.
	 * 
	 * @param tpf
	 *            The time per frame in seconds. Used to interpolate
	 *            time-sensitive data.
	 */
	protected void doUpdate(float tpf) {

	}

	/**
	 * OVERRIDE FOR CUSTOM RENDER BEHAVIOUR:
	 * 
	 * This method gets called every frame to render this entity if the visible
	 * flag is set to true.
	 * 
	 * @param graphics
	 *            The graphics 2d context you can render to. The entity has
	 *            already applied transformation, so the context origin is your
	 *            position.
	 */
	protected void doRender(Graphics2D graphics) {

	}

	/**
	 * Creates an entity using the given name. You really have to provide a non
	 * null name since null-keys are not allowed in hashing.
	 * 
	 * @param name
	 *            the name of this entity after creation.
	 */
	public Entity(String name) {
		if (name == null) {
			throw new NullPointerException("name");
		}

		this.name = name;
	}

	/**
	 * @return the children visible flag.
	 */
	public boolean isChildrenVisible() {
		return childrenVisible;
	}

	/**
	 * @return the visible flag.
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Sets the visible flag. True means this entity is rendered. This flag does
	 * not influence the rendering of the children.
	 * 
	 * @param visible
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Sets the children visible flag. True means all children are rendered.
	 * This flag does not influence the rendering of the entity.
	 * 
	 * @param childrenVisible
	 */
	public void setChildrenVisible(boolean childrenVisible) {
		this.childrenVisible = childrenVisible;
	}

	/**
	 * @param name
	 *            The name of the child you want to find.
	 * @return the first child which has the same name or null if there is no
	 *         such child.
	 */
	public Entity findChildByName(String name) {
		if (name == null) {
			throw new NullPointerException("name");
		}

		// Lookup the name list
		List<Entity> list = names2Entities.get(name);

		return list != null ? list.get(0) : null;
	}

	/**
	 * @param name
	 *            The name of the children you want to find.
	 * @return a list of children which have the same name or null if there are
	 *         no such children.
	 */
	public List<Entity> findChildrenByName(String name) {
		// Lookup the name list
		List<Entity> list = names2Entities.get(name);

		return list != null ? Collections.unmodifiableList(list) : null;
	}

	/**
	 * Detaches all children from this entity.
	 */
	public void detachAll() {
		for (Entity child : getCache()) {
			detach(child);
		}
	}

	/**
	 * @return an unmodifiable set of all children.
	 */
	public Set<Entity> getChildren() {
		return readOnlyChildren;
	}

	/**
	 * @return true if this entity has no parent, otherwise false.
	 */
	public boolean isRootEntity() {
		return parent == null;
	}

	/**
	 * @return the root entity (an entity which has no parent) of this entity or
	 *         this entity if this entity has no parent (which means that this
	 *         entity is a root entity).
	 */
	public Entity getRootEntity() {
		return parent != null ? parent.getRootEntity() : this;
	}

	/**
	 * @return the next scene which ownes this entity or this entity if this
	 *         entity is already a scene or null if there is no scene in the
	 *         hierarchy at all.
	 * 
	 * @see Scene
	 */
	public Scene getScene() {
		return this instanceof Scene ? (Scene) this : parent != null ? parent
				.getScene() : null;
	}

	/**
	 * @return the parent of this entity or null if there is no parent.
	 */
	public Entity getParent() {
		return parent;
	}

	/**
	 * @return true if this entity has children, otherwise false.
	 */
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	/**
	 * @return the children count.
	 */
	public int getChildrenCount() {
		return children.size();
	}

	/**
	 * Attaches a child to this entity. If this entity has already a parent it
	 * is detached automatically and will be attached afterwards.
	 * 
	 * @param child
	 *            The entity you want to attach as child.
	 * @return true if the entity is now a child of this entity, otherwise
	 *         false.
	 */
	public boolean attach(Entity child) {
		if (child == null) {
			throw new NullPointerException("child");
		} else if (child.parent != null) {
			// If this entity is already the parent...
			if (child.parent == this) {
				return true;
			} else if (!child.detach()) {
				throw new IllegalStateException("Unable to detach child");
			}
		}

		if (children.add(child)) {
			child.parent = this;

			// Get entity name list
			List<Entity> list = getEntityList(child.getName());

			// Just add the child
			list.add(child);

			// Cache is invalid now
			cache = null;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Detaches this entity from its parent.
	 * 
	 * @return true if there was a parent and this entity is detached
	 *         successfully, otherwise false.
	 */
	public boolean detach() {
		return parent != null ? parent.detach(this) : false;
	}

	/**
	 * Detaches a entity from this entity.
	 * 
	 * @param child
	 *            The entity you want to detach from this entity.
	 * @return true if the entity was a child of this entity and is detached
	 *         successfully, otherwise false.
	 */
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
			List<Entity> list = getEntityList(child.getName());

			// Remove the child
			list.remove(child);

			// Was this the last child with the given name ?
			if (list.isEmpty()) {
				// Remove from map
				names2Entities.remove(child.getName());
			}

			// Cache is invalid now
			cache = null;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return the name of this entity.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the relative scale of this entity as vector.
	 */
	public Vector2f getScale() {
		return scale;
	}

	/**
	 * Sets the relative scale of this entity.
	 * 
	 * @param scale
	 *            The vector you want to use as scale now. If the vector is null
	 *            nothing happens.
	 */
	public void setScale(Vector2f scale) {
		if (scale != null) {
			this.scale = scale;
		}
	}

	/**
	 * @return the relative rotation of this entity in radians.
	 */
	public float getRotation() {
		return rotation;
	}

	/**
	 * Sets the relative rotation in radians.
	 * 
	 * @param rotation
	 *            The rotation value stored as float you want to set.
	 */
	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	/**
	 * @return the relative position of this entity.
	 */
	public Vector2f getPosition() {
		return position;
	}

	/**
	 * Sets the relative position of this entity.
	 * 
	 * @param position
	 *            The vector you want to use as position now. If the vector is
	 *            null nothing happens.
	 */
	public void setPosition(Vector2f position) {
		// Do not allow null positions!
		if (position != null) {
			this.position = position;
		}
	}

	/**
	 * Transforms the given graphics context origin in a way that 0/0 is exactly
	 * the origin of THIS entity.
	 * 
	 * @param g
	 *            The graphics context you want to transform.
	 * @return the given transformed graphics object for chaining.
	 */
	public final Graphics2D transform(Graphics2D g) {
		// Translate
		g.translate(position.x, position.y);

		// Rotate the graphics
		g.rotate(rotation);

		// Scale the graphics
		g.scale(scale.x, scale.y);

		return g;
	}

	/**
	 * Updates this entity and all children.
	 * 
	 * @param tpf
	 *            The passed time to update time-sensitive data.
	 */
	public final void update(float tpf) {

		// Do the update
		doUpdate(tpf);

		// Update all children
		for (Entity child : getCache()) {
			// Just update
			child.update(tpf);
		}
	}

	/**
	 * Transforms and renders this entity and all children. This method uses the
	 * visible/childrenVisible flags to determine what to render.
	 * 
	 * @param graphics
	 *            The graphics context you want to render to.
	 */
	public final void render(Graphics2D graphics) {
		// Create a copy of the graphics and transform
		Graphics2D copy = transform((Graphics2D) graphics.create());
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

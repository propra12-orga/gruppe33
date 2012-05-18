package propra2012.gruppe33.engine.graphics.rendering.scenegraph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import propra2012.gruppe33.engine.graphics.GraphicsRoutines;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.image.ImageController;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.math.Vector2f;
import propra2012.gruppe33.engine.resources.BufferedImageResource;

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
 * - An entity has a name.
 * 
 * - An entity can have controllers which basically provides dynamic behaviour,
 * because you can add or remove controllers whenever you like.
 * 
 * - To find an entity you have to use the find*** methods and provide an entity
 * filter.
 * 
 * 
 * @author Christopher Probst
 * @see EntityController
 * @see Scene
 * @see Vector2f
 */
public class Entity extends EntityControllerAdapter implements
		Comparable<Entity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
	private String name;

	/*
	 * The order of this entity.
	 */
	private int order = 0;

	/*
	 * The parent entity which ones this entity.
	 */
	private Entity parent = null;

	/*
	 * The children count.
	 */
	private int childrenCount = 0;

	/*
	 * The map which contains all order2entity mappings.
	 */
	private final NavigableMap<Integer, Set<Entity>> children = new TreeMap<Integer, Set<Entity>>();

	/*
	 * Used to cache children iterations.
	 */
	private List<Entity> childrenCache = null, readOnlyChildrenCache = null;

	/*
	 * Maps classes to entity controllers.
	 */
	private final Map<Class<? extends EntityController>, EntityController> controllers = new LinkedHashMap<Class<? extends EntityController>, EntityController>();

	/*
	 * Used to cache controller iterations.
	 */
	private List<EntityController> controllerCache = null;

	/*
	 * Tells whether or not this entity and/or its children are visible.
	 */
	private boolean visible = true, childrenVisible = true;

	/**
	 * Returns a cached list which contains all controllers. If it does not
	 * exist yet it will be created.
	 * 
	 * IMPORTANT:
	 * 
	 * You can iterate over the controllers and remove them from this entity
	 * while iterating since this method returns a different list.
	 * 
	 * @return a list which contains all controllers.
	 */
	private List<EntityController> getControllerCache() {
		return controllerCache != null ? controllerCache
				: (controllerCache = new ArrayList<EntityController>(
						controllers.values()));
	}

	/**
	 * Removes the given set if it is empty.
	 * 
	 * @param order
	 *            The order of the given set.
	 * @param entities
	 *            The entity set.
	 */
	private void removeSetIfEmpty(int order, Set<Entity> entities) {
		if (entities != null && entities.isEmpty()) {

			// Try to remove the set from the map
			Set<Entity> removedEntities = children.remove(order);

			// If it is not the same set readd it (should never happen...)
			if (removedEntities != entities) {

				// Put again into map
				children.put(order, removedEntities);

				// Throw an exception
				throw new IllegalArgumentException("You tried to remove "
						+ "a set which does not represent the set which "
						+ "is mapped to the given order");
			}
		}
	}

	/**
	 * Returns the lazy entity set of the given order. If there is no set yet a
	 * new one will be created.
	 * 
	 * @param order
	 *            The order.
	 * @return a valid set.
	 */
	private Set<Entity> getLazyEntities(int order) {
		// Try to get the entities
		Set<Entity> entities = children.get(order);

		// Lazy creation
		if (entities == null) {
			/*
			 * Always use linked-hash-set for better iteration performance and
			 * correct ordering.
			 */
			children.put(order, entities = new LinkedHashSet<Entity>());
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
	private List<Entity> getChildrenCache() {
		if (childrenCache == null) {

			// Create a new children cache
			childrenCache = new ArrayList<Entity>(childrenCount);

			// Copy all entities to the list
			for (Set<Entity> entities : children.values()) {
				for (Entity entity : entities) {
					childrenCache.add(entity);
				}
			}

			System.out.println("-> " + childrenCache.size());
		}

		return childrenCache;
	}

	/**
	 * @see Entity#getChildrenCache()
	 * @return a read-only children cache.
	 */
	private List<Entity> getReadOnlyChildrenCache() {
		return readOnlyChildrenCache != null ? readOnlyChildrenCache
				: (readOnlyChildrenCache = Collections
						.unmodifiableList(getChildrenCache()));
	}

	/**
	 * Clears the children cache.
	 */
	private void clearChildrenCache() {
		readOnlyChildrenCache = childrenCache = null;
	}

	/**
	 * Clears the controller cache.
	 */
	private void clearControllerCache() {
		controllerCache = null;
	}

	/**
	 * Renders the given controller and manages copies of the graphics contexts.
	 * 
	 * @param controller
	 *            The controller you want to render.
	 * @param original
	 *            The original graphics context.
	 * @param transformed
	 *            The transformed graphics context.
	 */
	private void renderController(EntityController controller,
			Graphics2D original, Graphics2D transformed) {

		if (original == null) {
			throw new NullPointerException("original");
		} else if (transformed == null) {
			throw new NullPointerException("transformed");
		}

		// Create a copy the original graphics
		Graphics2D originalCopy = (Graphics2D) original.create();

		try {

			// Create a copy of the transformed graphics
			Graphics2D transformedCopy = (Graphics2D) transformed.create();

			try {

				// Render the entity to this entity
				controller.doRender(this, originalCopy, transformedCopy);

			} finally {
				// Dispose the transformed copy
				transformedCopy.dispose();
			}
		} finally {
			// Dispose the original copy
			originalCopy.dispose();
		}
	}

	/**
	 * Creates an entity using a generated name.
	 */
	public Entity() {
		this(UUID.randomUUID().toString());
	}

	/**
	 * Creates an entity using the given name.
	 * 
	 * @param name
	 *            the name of this entity after creation.
	 */
	public Entity(String name) {
		setName(name);
	}

	/**
	 * Sets the name of this entity.
	 * 
	 * @param name
	 *            The new name.
	 */
	public void setName(String name) {
		if (name == null) {
			throw new NullPointerException("name");
		}

		this.name = name;
	}

	/**
	 * Puts a controller and returns the old one.
	 * 
	 * @param controller
	 *            The controller you want to add.
	 * @return an instance of the class you specified or null.
	 */
	@SuppressWarnings("unchecked")
	public <T extends EntityController> T putController(T controller) {
		if (controller == null) {
			throw new NullPointerException("controller");
		}

		// Adding works always...
		clearControllerCache();

		return (T) controllers.put(controller.getClass(), controller);
	}

	/**
	 * Removes and returns a controller.
	 * 
	 * @param controllerClass
	 *            The controller class you want to remove.
	 * @return an instance of the class you specified or null.
	 */
	@SuppressWarnings("unchecked")
	public <T extends EntityController> T remoteController(
			Class<T> controllerClass) {
		if (controllerClass == null) {
			throw new NullPointerException("controllerClass");
		}

		// Try to remove
		T result = (T) controllers.remove(controllerClass);

		// Check for null
		if (result != null) {
			clearControllerCache();
		}

		return result;
	}

	/**
	 * Gets a controller of the given type.
	 * 
	 * @param controllerClass
	 *            The controller class you want to get.
	 * @return an instance of the class you specified or null.
	 */
	@SuppressWarnings("unchecked")
	public <T extends EntityController> T getController(Class<T> controllerClass) {
		if (controllerClass == null) {
			throw new NullPointerException("controllerClass");
		}
		return (T) controllers.get(controllerClass);
	}

	/**
	 * Clears the controllers.
	 */
	public void clearControllers() {
		controllers.clear();
		clearControllerCache();
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
	 * Detaches all children from this entity.
	 */
	public void detachAll() {
		for (Entity child : getChildrenCache()) {
			detach(child);
		}
	}

	/**
	 * @return an unmodifiable list of all children.
	 */
	public List<Entity> getChildren() {
		return getReadOnlyChildrenCache();
	}

	/**
	 * @return true if this entity has no parent, otherwise false.
	 */
	public boolean isRootEntity() {
		return parent == null;
	}

	/**
	 * @return the order of this entity.
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * Sets the order of this entity.
	 * 
	 * @param order
	 *            The new order.
	 */
	public void setOrder(int order) {
		// Diff ?
		if (order != this.order) {

			// Is there already a parent ?
			if (parent != null) {

				// Lookup
				Set<Entity> entities = parent.children.get(this.order);

				// If the set exist
				if (entities != null) {
					// Remove this entity
					if (!entities.remove(this)) {
						throw new IllegalStateException("This entity could "
								+ "not be removed from the old parent set. "
								+ "Please check your code.");
					}

					// Clean up
					parent.removeSetIfEmpty(this.order, entities);
				} else {
					throw new IllegalStateException("The parent set of this "
							+ "entity is null. Please check your code.");
				}

				// Try to add
				if (!parent.getLazyEntities(order).add(this)) {
					throw new IllegalStateException("This entity could not be "
							+ "added to the new parent set. "
							+ "Please check your code.");
				}
			}

			// Save
			this.order = order;
		}
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
	 * Tries to find the next parente scene using a {@link TypeFilter} with
	 * {@link Entity#findParent(EntityFilter, boolean)}.
	 * 
	 * @return a parent scene (could be this class) or null.
	 * @see Scene
	 */
	public Scene findScene() {
		return (Scene) findParent(new TypeFilter(Scene.class, true), true);
	}

	/**
	 * This method searches for the first child (recursively) which matches the
	 * given entity filter.
	 * 
	 * @param entityFilter
	 *            The entity filter.
	 * @param includeThis
	 *            If true the method will also check THIS entity to match the
	 *            filter.
	 * @return a valid entity or null.
	 * @see EntityFilter
	 */
	public Entity findChildRecursively(EntityFilter entityFilter,
			boolean includeThis) {

		// Try the children first
		Entity entity = findChild(entityFilter, includeThis);

		// Not valid ?
		if (entity == null) {

			// Iterate over all children
			for (Entity child : getChildrenCache()) {

				// Try to find the child recursively
				if ((entity = child.findChildRecursively(entityFilter, false)) != null) {
					break;
				}
			}
		}

		// Return null...
		return entity;
	}

	/**
	 * This method searches for children and sub-children (recursively) which
	 * match the given entity filter.
	 * 
	 * @param entityFilter
	 *            The entity filter. If null all children and sub-children are
	 *            returned.
	 * @param includeThis
	 *            If true the method will also check THIS entity to match the
	 *            filter.
	 * @return a list of entities.
	 * @see EntityFilter
	 */
	public List<Entity> findChildrenRecursively(EntityFilter entityFilter,
			boolean includeThis) {

		// Create a new linked list to hold the search results
		List<Entity> results = findChildren(entityFilter, includeThis);

		// Iterate over all children
		for (Entity child : getChildrenCache()) {

			// Repeat this method recursively and merge results
			results.addAll(child.findChildrenRecursively(entityFilter, false));
		}

		// Return the merged results
		return results;
	}

	/**
	 * This method searches for the first child which matches the given entity
	 * filter.
	 * 
	 * @param entityFilter
	 *            The entity filter.
	 * @param includeThis
	 *            If true the method will also check THIS entity to match the
	 *            filter.
	 * @return a valid entity or null.
	 * @see EntityFilter
	 */
	public Entity findChild(EntityFilter entityFilter, boolean includeThis) {

		if (entityFilter == null) {
			throw new NullPointerException("entityFilter");
		}

		// The children iterator
		Iterator<Entity> childrenIterator = getChildrenCache().iterator();

		// Decide where to start
		Entity ptr = includeThis ? this
				: (childrenIterator.hasNext() ? childrenIterator.next() : null);

		// Stop here
		if (ptr == null) {
			return null;
		}

		// Loop...
		for (;;) {

			// Try to accept an entity
			int flag = entityFilter.accept(ptr);

			// Should this entity be added ?
			if ((flag & EntityFilter.VALID) != 0) {
				return ptr;
			} else if ((flag & EntityFilter.CONTINUE) != 0
					&& childrenIterator.hasNext()) {
				ptr = childrenIterator.next();
			} else {
				// Stop here
				return null;
			}
		}
	}

	/**
	 * This method searches for children which match the given entity filter.
	 * 
	 * @param entityFilter
	 *            The entity filter. If null all children are returned.
	 * @param includeThis
	 *            If true the method will also check THIS entity to match the
	 *            filter.
	 * @return a list of entities.
	 * @see EntityFilter
	 */
	public List<Entity> findChildren(EntityFilter entityFilter,
			boolean includeThis) {

		// Create a new linked list to hold the search results
		List<Entity> results = new LinkedList<Entity>();

		// The children iterator
		Iterator<Entity> childrenIterator = getChildrenCache().iterator();

		// Decide where to start
		Entity ptr = includeThis ? this
				: (childrenIterator.hasNext() ? childrenIterator.next() : null);

		// As long as there are entities
		while (ptr != null) {

			if (entityFilter != null) {

				// Try to accept an entity
				int flag = entityFilter.accept(ptr);

				// Should this entity be added ?
				if ((flag & EntityFilter.VALID) != 0) {
					results.add(ptr);
				}

				// Should we continue searching ?
				ptr = (flag & EntityFilter.CONTINUE) != 0
						&& childrenIterator.hasNext() ? childrenIterator.next()
						: null;
			} else {

				// Just add and continue
				results.add(ptr);
				ptr = childrenIterator.hasNext() ? childrenIterator.next()
						: null;
			}
		}
		return results;
	}

	/**
	 * This method searches for the first entity which matches the given entity
	 * filter.
	 * 
	 * @param entityFilter
	 *            The entity filter.
	 * @param includeThis
	 *            If true the method will also check THIS entity to match the
	 *            filter.
	 * @return a valid entity or null.
	 * @see EntityFilter
	 */
	public Entity findParent(EntityFilter entityFilter, boolean includeThis) {

		if (entityFilter == null) {
			throw new NullPointerException("entityFilter");
		}

		// Decide where to start
		Entity ptr = includeThis ? this : getParent();

		// Stop here
		if (ptr == null) {
			return null;
		}

		// Loop...
		for (;;) {

			// Try to accept an entity
			int flag = entityFilter.accept(ptr);

			// Should this entity be added ?
			if ((flag & EntityFilter.VALID) != 0) {
				return ptr;
			} else if ((flag & EntityFilter.CONTINUE) != 0
					&& ptr.getParent() != null) {
				ptr = ptr.getParent();
			} else {
				// Do not continue
				return null;
			}
		}
	}

	/**
	 * This method searches for parents which match the given entity filter.
	 * 
	 * @param entityFilter
	 *            The entity filter. If null all parents are returned.
	 * @param includeThis
	 *            If true the method will also check THIS entity to match the
	 *            filter.
	 * @return a list of entities.
	 * @see EntityFilter
	 */
	public List<Entity> findParents(EntityFilter entityFilter,
			boolean includeThis) {

		// Create a new linked list to hold the search results
		List<Entity> results = new LinkedList<Entity>();

		// Decide where to start
		Entity ptr = includeThis ? this : getParent();

		// As long there are entities...
		while (ptr != null) {

			if (entityFilter != null) {
				// Try to accept an entity
				int flag = entityFilter.accept(ptr);

				// Should this entity be added ?
				if ((flag & EntityFilter.VALID) != 0) {
					results.add(ptr);
				}

				// Should we continue searching ?
				if ((flag & EntityFilter.CONTINUE) != 0) {
					ptr = ptr.getParent();
				} else {
					ptr = null;
				}
			} else {
				// Just add and continue
				results.add(ptr);
				ptr = ptr.getParent();
			}
		}

		// Return the results
		return results;
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
		return childrenCount > 0;
	}

	/**
	 * @return the children count.
	 */
	public int getChildrenCount() {
		return childrenCount;
	}

	/**
	 * Sends a message with the given parameters to this entity and all
	 * controllers.
	 * 
	 * @param message
	 *            The message object.
	 * @param args
	 *            The arguments.
	 */
	public void sendMessage(Object message, Object... args) {
		// Invoke methods on this entity
		onMessage(message, args);
		for (EntityController controller : getControllerCache()) {
			controller.onMessage(message, args);
		}
	}

	/**
	 * Detaches all children given by the iterable interface from this entity.
	 * 
	 * @param children
	 *            The entities you want to detach from this entity.
	 * @return true if all entities were children of this entity and are
	 *         detached successfully, otherwise false.
	 */
	public boolean detach(Iterable<Entity> children) {
		if (children == null) {
			throw new NullPointerException("children");
		}
		return detach(children.iterator());
	}

	/**
	 * Detaches all children given by the iterator from this entity.
	 * 
	 * @param children
	 *            The entities you want to detach from this entity.
	 * @return true if all entities were children of this entity and are
	 *         detached successfully, otherwise false.
	 */
	public boolean detach(Iterator<Entity> children) {
		if (children == null) {
			throw new NullPointerException("children");
		}
		boolean success = true;

		// Iterate over the given children
		while (children.hasNext()) {

			// Detach the next child
			if (!detach(children.next())) {
				success = false;
			}
		}

		return success;
	}

	/**
	 * Attaches all children given by the iterable interface to this entity. If
	 * these entities have already parents they are detached automatically and
	 * will be attached afterwards.
	 * 
	 * @param children
	 *            The entities you want to attach as children.
	 */
	public void attach(Iterable<Entity> children) {
		if (children == null) {
			throw new NullPointerException("children");
		}
		attach(children.iterator());
	}

	/**
	 * Attaches all children given by the iterator to this entity. If these
	 * entities have already parents they are detached automatically and will be
	 * attached afterwards.
	 * 
	 * @param children
	 *            The entities you want to attach as children.
	 */
	public void attach(Iterator<Entity> children) {
		if (children == null) {
			throw new NullPointerException("children");
		}
		// Iterate over the given children
		while (children.hasNext()) {

			// Attach the next child
			attach(children.next());
		}
	}

	/**
	 * Attaches a child to this entity. If this entity has already a parent it
	 * is detached automatically and will be attached afterwards.
	 * 
	 * @param child
	 *            The entity you want to attach as child.
	 */
	public void attach(Entity child) {
		if (child == null) {
			throw new NullPointerException("child");
		} else if (child.parent != null) {
			// If this entity is already the parent...
			if (child.parent == this) {
				return;
			} else if (!child.detach()) {
				throw new IllegalStateException("Unable to detach child");
			}
		}

		// Lookup entity set
		Set<Entity> entities = getLazyEntities(child.getOrder());

		if (entities.add(child)) {

			// Inc counter
			childrenCount++;

			// Set parent
			child.parent = this;

			// Cache is invalid now
			clearChildrenCache();

			// Invoke methods on child
			child.onAttached(child);
			for (EntityController controller : child.getControllerCache()) {
				controller.onAttached(child);
			}

			// Invoke methods on this entity
			onChildAttached(this, child);
			for (EntityController controller : getControllerCache()) {
				controller.onChildAttached(this, child);
			}
		} else {
			throw new IllegalStateException("This entity could not add the "
					+ "given child to its set. Please check your code.");
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
			return false;
		}

		// Lookup
		Set<Entity> entities = children.get(child.getOrder());

		// Check
		if (entities == null) {
			throw new IllegalStateException("The entity set of the given "
					+ "child order does not exist. Please check your code.");
		} else if (entities.remove(child)) {

			// Clean up
			removeSetIfEmpty(child.getOrder(), entities);

			// Dec counter
			childrenCount--;

			// Clear parent
			child.parent = null;

			// Cache is invalid now
			clearChildrenCache();

			// Invoke methods on child
			child.onDetached(child);
			for (EntityController controller : child.getControllerCache()) {
				controller.onDetached(child);
			}

			// Invoke method on this entity
			onChildDetached(this, child);
			for (EntityController controller : getControllerCache()) {
				controller.onChildDetached(this, child);
			}

			return true;
		} else {
			throw new IllegalStateException("This entity could not remove "
					+ "the given child from its set. Please check your code.");
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
	 *            The vector you want to use as scale now.
	 */
	public void setScale(Vector2f scale) {
		if (scale != null) {
			throw new NullPointerException("scale");
		}
		this.scale = scale;
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
	 *            The vector you want to use as position now.
	 */
	public void setPosition(Vector2f position) {
		if (position == null) {
			throw new NullPointerException("position");
		}

		this.position = position;
	}

	/**
	 * Applies local transform to a given graphics context.
	 * 
	 * @param graphics
	 *            The graphics context you want to transform.
	 * @return the given transformed graphics context for chaining.
	 */
	public final Graphics2D transform(Graphics2D graphics) {
		if (graphics == null) {
			throw new NullPointerException("graphics");
		}

		// Translate
		graphics.translate(position.x, position.y);

		// Rotate the graphics
		graphics.rotate(rotation);

		// Scale the graphics
		graphics.scale(scale.x, scale.y);

		return graphics;
	}

	/**
	 * Applies local transform to a given affine transform.
	 * 
	 * @param transform
	 *            The affine transform you want to transform.
	 * @return the given transformed affine transform for chaining.
	 */
	public final AffineTransform transform(AffineTransform transform) {
		if (transform == null) {
			throw new NullPointerException("transform");
		}
		transform.translate(position.x, position.y);
		transform.rotate(rotation);
		transform.scale(scale.x, scale.y);
		return transform;
	}

	/**
	 * @return a affine transform containing the state of this entity.
	 */
	public AffineTransform getTransform() {
		return transform(new AffineTransform());
	}

	/**
	 * Updates this entity and all children.
	 * 
	 * @param tpf
	 *            The passed time to update time-sensitive data.
	 */
	public final void update(float tpf) {

		// Do the update
		doUpdate(this, tpf);

		// Check controllers
		if (!controllers.isEmpty()) {
			for (EntityController controller : getControllerCache()) {
				// Update the controller
				controller.doUpdate(this, tpf);
			}
		}

		// Update all children
		for (Entity child : getChildrenCache()) {
			// Just update
			child.update(tpf);
		}
	}

	/**
	 * Renders this entity into an image controller of the given entity. If
	 * there is no image controller yet, a new one will be created using the
	 * given transparency and the next scene in hierarchy and use its dimension
	 * to create a new image. If there is no scene an exception will be thrown.
	 * 
	 * @param entity
	 *            The entity in which you want to render.
	 * @param optTransparency
	 *            The optional transparency of the image if there is no image
	 *            controller yet.
	 * @param optBG
	 *            The optional background color of the image if there is no
	 *            image controller yet.
	 */
	public final void renderTo(Entity entity, int optTransparency, Color optBG) {
		renderTo(entity, -1, -1, optTransparency, optBG);
	}

	/**
	 * Renders this entity into an image controller of the given entity. If
	 * there is no image controller yet, a new one will be created using the
	 * given parameters. If there parameters are invalid the method will get the
	 * next scene in hierarchy and use its dimension to create a new image. If
	 * there is no scene an exception will be thrown.
	 * 
	 * @param entity
	 *            The entity in which you want to render.
	 * @param optWidth
	 *            The optional width of the image if there is no image
	 *            controller yet.
	 * @param optHeight
	 *            The optional height of the image if there is no image
	 *            controller yet.
	 * @param optTransparency
	 *            The optional transparency of the image if there is no image
	 *            controller yet.
	 * @param optBG
	 *            The optional background color of the image if there is no
	 *            image controller yet.
	 */
	public final void renderTo(Entity entity, int optWidth, int optHeight,
			int optTransparency, Color optBG) {
		if (entity == null) {
			throw new NullPointerException("entity");
		}

		// Lookup image controller
		ImageController image = entity.getController(ImageController.class);

		// Oops... thats bad
		if (image == null) {

			/*
			 * If width and height are not valid try to read these data from the
			 * next scene in hierarchy.
			 */
			if (optWidth <= 0 || optHeight <= 0) {

				// Try to get a scene
				Scene scene = findScene();

				/*
				 * Bad... We can not decide which size the user want to use.
				 */
				if (scene == null) {
					throw new IllegalStateException(
							"The entity has no image controller and the "
									+ "width/height you specified is not valid. But "
									+ "there is no scene in your hierarchy, too, so "
									+ "this method can not create a new image controller.");
				} else {
					// Reset width and height
					optWidth = scene.getWidth();
					optHeight = scene.getHeight();
				}
			}

			// Create a new image controller and add to entity
			entity.putController(image = new ImageController(
					new BufferedImageResource(GraphicsRoutines.clear(
							GraphicsRoutines.createImage(optWidth, optHeight,
									optTransparency), optBG))));
		}

		// Render to the image
		renderTo(image.getImageResource().get());
	}

	/**
	 * Renders this entity and all children to an image. This method uses the
	 * visible/childrenVisible flags to determine what to render.
	 * 
	 * @param destination
	 *            The image you want to render to.
	 * @return the given destination image.
	 */
	public final Image renderTo(Image destination) {
		if (destination == null) {
			throw new NullPointerException("destination");
		}

		// Get the graphics context
		Graphics graphics = destination.getGraphics();

		try {
			if (graphics instanceof Graphics2D) {

				// Just render to image
				render((Graphics2D) graphics, (Graphics2D) graphics);
			} else {
				throw new IllegalArgumentException("The image must return a "
						+ "Graphics2D object when calling getGraphics().");
			}

			return destination;
		} finally {
			// Dispose
			graphics.dispose();
		}
	}

	/**
	 * Renders this entity and all children to a graphics context. This method
	 * uses the visible/childrenVisible flags to determine what to render.
	 * 
	 * @param original
	 *            The graphics context which is NOT affected by entity
	 *            transformation.
	 * @param transformed
	 *            The graphics context which is affected by entity
	 *            transformation.
	 * 
	 */
	public final void render(Graphics2D original, Graphics2D transformed) {

		if (original == null) {
			throw new NullPointerException("original");
		} else if (transformed == null) {
			throw new NullPointerException("transformed");
		}

		// Create a copy of the transformed graphics and transform again
		Graphics2D transformedCopy = transform((Graphics2D) transformed
				.create());
		try {

			if (visible) {

				/*
				 * Performance optimization:
				 * 
				 * We only want to render an entity if this entity is extended
				 * because the doRender method is always empty in default impl.
				 * so we would create "useless" copies of the context.
				 */
				if (!getClass().equals(Entity.class)) {
					// Render this entity
					renderController(this, original, transformedCopy);
				}

				// Check controllers
				if (!controllers.isEmpty()) {
					for (EntityController controller : getControllerCache()) {
						// Render the controller
						renderController(controller, original, transformedCopy);
					}
				}
			}

			if (childrenVisible) {
				// Render all children
				for (Entity child : getChildrenCache()) {
					// Just render
					child.render(original, transformedCopy);
				}
			}
		} finally {
			// Dispose the transformed copy
			transformedCopy.dispose();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Entity o) {
		if (order > o.order) {
			return 1;
		} else if (order == o.order) {
			return 0;
		} else {
			return -1;
		}
	}

	@Override
	public String toString() {
		return "[Name = " + name + ", Order = " + order + ", Position = "
				+ position + "]";
	}
}

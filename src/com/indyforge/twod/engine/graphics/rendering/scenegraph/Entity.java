package com.indyforge.twod.engine.graphics.rendering.scenegraph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.util.ChildIterator;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.util.ParentIterator;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.util.RecursiveChildIterator;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.util.RootFilter;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.util.SiblingIterator;
import com.indyforge.twod.engine.util.ArrayIterator;
import com.indyforge.twod.engine.util.FilteredIterator;
import com.indyforge.twod.engine.util.IterationRoutines;
import com.indyforge.twod.engine.util.SerializableIterable;

/**
 * 
 * An entity is basically a node of tree. Each entity (node) can have multiple
 * children which are sorted by their indeces. This ensures modifiable event
 * ordering. The entity-tree-concept is the key feature for processing
 * hierarchically ordered data.
 * 
 * @author Christopher Probst
 * @see EntityFilter
 */
public class Entity implements Comparable<Entity>, Iterable<Entity>,
		Serializable {

	/**
	 * Used for tags.
	 */
	public static final Object TAG_OBJECT = new Object();

	/**
	 * 
	 * The default entity events.
	 * 
	 * @author Christopher Probst
	 * 
	 */
	public enum EntityEvent {
		Attached, Detached, Update
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Fires the event for the given entity.
	 * 
	 * @param entity
	 *            The entity which will receive the event.
	 * 
	 * @param source
	 *            The entity which fired the event or null.
	 * @param event
	 *            The event.
	 * @param params
	 *            The parameters.
	 */
	public static void fireEvent(Entity entity, Entity source, Object event,
			Object... params) {
		if (entity != null
				&& (source == null || source.events().containsKey(event))) {
			// Just call the method
			entity.onEvent(source, event, params);
		}
	}

	/**
	 * Fires the given event for all entities.
	 * 
	 * @param entityIterator
	 *            The entity iterator.
	 * @param source
	 *            The entity which fired the event or null.
	 * @param event
	 *            The event.
	 * @param params
	 *            The parameters.
	 */
	public static void fireEvent(Iterator<? extends Entity> entityIterator,
			Entity source, Object event, Object... params) {
		if (entityIterator != null
				&& (source == null || source.events().containsKey(event))) {
			// Post event to all entities
			while (entityIterator.hasNext()) {

				// Just call the method
				entityIterator.next().onEvent(source, event, params);
			}
		}
	}

	/*
	 * The name of this entity.
	 */
	private String name = super.toString();

	/*
	 * The uuid of this entity which is the registry key. Used to identify
	 * entities on multiple platforms.
	 */
	private final UUID registryKey = UUID.randomUUID();

	/*
	 * The ordering index of this entity.
	 */
	private int index = 0,

	/*
	 * The index in the parent cache. This value will be set by the parent if
	 * the cache changes.
	 */
	cacheIndex = 0;

	/*
	 * The parent entity which ones this entity.
	 */
	private Entity parent = null;

	/*
	 * The map which contains all children. The children are stored in sets
	 * which are mapped to their indeces.
	 */
	private final NavigableMap<Integer, Set<Entity>> children = new TreeMap<Integer, Set<Entity>>();

	/*
	 * This map is used to map the uuids of all (sub-) children. The concept of
	 * a registry is very important to identify single entities in a probably
	 * complex hierarchy.
	 */
	private final Map<UUID, Entity> registry = new HashMap<UUID, Entity>(),
			readOnlyRegistry = Collections.unmodifiableMap(registry);

	/*
	 * Used to create event iterators.
	 */
	private final Map<Object, Iterable<? extends Entity>> events = new HashMap<Object, Iterable<? extends Entity>>();

	/*
	 * The properties of this entity.
	 */
	private final Map<Object, Object> props = new HashMap<Object, Object>();

	/*
	 * Used to cache children iterations.
	 */
	private List<Entity> cachedChildren = null;

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
	private Set<Entity> lazyEntities(int order) {
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
	 * Clears the cached children.
	 */
	private void clearCachedChildren() {
		cachedChildren = null;
	}

	/**
	 * OVERRIDE FOR CUSTOM BEHAVIOUR.
	 * 
	 * This method is called when an event is fired.
	 * 
	 * @param source
	 *            The entity which fired the event or null.
	 * @param event
	 *            The event.
	 * @param params
	 *            The parameters.
	 */
	protected void onEvent(Entity source, Object event, Object... params) {
		/*
		 * Process the default events.
		 */
		if (event instanceof EntityEvent) {
			switch ((EntityEvent) event) {
			case Attached:

				// Convert
				Entity child = (Entity) params[0];

				// Invoke default event
				onAttached(source, child);

				// Evaluate the other events...
				if (this == source) {
					onChildAttached(child);
				} else if (this == child) {
					onAttached();
				} else if (parent == child) {
					onParentAttached();
				}
				break;
			case Detached:
				// Convert
				child = (Entity) params[0];

				// Invoke default event
				onDetached(source, child);

				// Evaluate the other events...
				if (this == source) {
					onChildDetached(child);
				} else if (this == child) {
					onDetached();
				} else if (parent == child) {
					onParentDetached();
				}
				break;

			case Update:
				onUpdate((Float) params[0]);
			}
		}
	}

	/**
	 * OVERRIDE FOR CUSTOM BEHAVIOUR.
	 * 
	 * This method is called when an entity was attached to an entity.
	 * 
	 * @param parent
	 *            The parent which attached the given child.
	 * @param child
	 *            The child which has been attached.
	 */
	protected void onAttached(Entity parent, Entity child) {

	}

	/**
	 * OVERRIDE FOR CUSTOM BEHAVIOUR.
	 * 
	 * This method is called when this entity was attached to an entity.
	 */
	protected void onAttached() {
	}

	/**
	 * OVERRIDE FOR CUSTOM BEHAVIOUR.
	 * 
	 * This method is called when a child is attached to this entity.
	 * 
	 * @param child
	 *            The child which has been attached.
	 */
	protected void onChildAttached(Entity child) {

	}

	/**
	 * OVERRIDE FOR CUSTOM BEHAVIOUR.
	 * 
	 * This method is called when the parent of this entity was attached to an
	 * entity.
	 */
	protected void onParentAttached() {
	}

	/**
	 * OVERRIDE FOR CUSTOM BEHAVIOUR.
	 * 
	 * This method is called when an entity was detached from an entity.
	 * 
	 * @param parent
	 *            The parent which detached the given child.
	 * @param child
	 *            The child which has been detached.
	 */
	protected void onDetached(Entity parent, Entity child) {
	}

	/**
	 * OVERRIDE FOR CUSTOM BEHAVIOUR.
	 * 
	 * This method is called when this entity was detached from an entity.
	 */
	protected void onDetached() {
	}

	/**
	 * OVERRIDE FOR CUSTOM BEHAVIOUR.
	 * 
	 * This method is called when a child is detached from this entity.
	 * 
	 * @param child
	 *            The child which has been detached.
	 */
	protected void onChildDetached(Entity child) {

	}

	/**
	 * OVERRIDE FOR CUSTOM BEHAVIOUR.
	 * 
	 * This method is called when the parent of this entity was detached from an
	 * entity.
	 */
	protected void onParentDetached() {
	}

	/**
	 * OVERRIDE FOR CUSTOM UPDATE BEHAVIOUR.
	 * 
	 * This method gets called every frame to update the state of this entity.
	 * 
	 * @param tpf
	 *            The time per frame in seconds. Used to interpolate
	 *            time-sensitive data.
	 */
	protected void onUpdate(float tpf) {
	}

	public Entity() {
		// Register the default entity events
		events().put(EntityEvent.Attached, iterableChildren(true, true));
		events().put(EntityEvent.Detached, iterableChildren(true, true));
		events().put(EntityEvent.Update, iterableChildren(true, true));

		/*
		 * Very important! Put this entity into the own map!
		 */
		registry.put(registryKey, this);
	}

	/**
	 * @return the registry of the root entity.
	 */
	public Map<UUID, Entity> registry() {
		return root().readOnlyRegistry;
	}

	/**
	 * @return the name.
	 */
	public String name() {
		return name;
	}

	/**
	 * Sets the name of this entity.
	 * 
	 * @param name
	 *            The new name.
	 * @return this for chaining.
	 */
	public Entity name(String name) {
		if (name == null) {
			throw new NullPointerException("name");
		}

		this.name = name;
		return this;
	}

	/**
	 * @return true if this entity has children, otherwise false.
	 */
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	/**
	 * @return true if this entity has a parent, otherwise false.
	 */
	public boolean hasParent() {
		return parent != null;
	}

	/**
	 * @param cacheIndex
	 *            The cache index.
	 * @return the child which is stored at the given cache index.
	 */
	public Entity childAt(int cacheIndex) {
		return children().get(cacheIndex);
	}

	/**
	 * @return a snapshot of all children sorted by their indeces as
	 *         unmodifiable list.
	 */
	public List<Entity> children() {

		// Do we already have created the cache?
		if (cachedChildren == null) {

			// Create a new list
			cachedChildren = new ArrayList<Entity>();

			// Used to give the children cache indeces
			int counter = 0;

			// Copy all entities to the list
			for (Set<Entity> entities : children.values()) {
				for (Entity entity : entities) {
					// The cache index
					cachedChildren.add(entity);

					// Set the cache index
					entity.cacheIndex = counter++;
				}
			}

			// Make unmodifiable
			cachedChildren = Collections.unmodifiableList(cachedChildren);
		}

		return cachedChildren;
	}

	/**
	 * @return the parent or null.
	 */
	public Entity parent() {
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Entity> iterator() {
		return new ChildIterator(this, false);
	}

	/**
	 * {@link SiblingIterator#SiblingIterator(Entity)}
	 */
	public Iterator<Entity> siblingIterator() {
		return new SiblingIterator(this);
	}

	/**
	 * {@link SiblingIterator#SiblingIterator(Entity, boolean)}
	 */
	public Iterator<Entity> siblingIterator(boolean includeParent) {
		return new SiblingIterator(this, includeParent);
	}

	/**
	 * 
	 * {@link ChildIterator#ChildIterator(Entity)}
	 * <p>
	 * OR
	 * <p>
	 * {@link RecursiveChildIterator#RecursiveChildIterator(Entity)}
	 */
	public Iterator<Entity> childIterator(boolean recursive) {
		return recursive ? new RecursiveChildIterator(this)
				: new ChildIterator(this);
	}

	/**
	 * 
	 * {@link ChildIterator#ChildIterator(Entity, boolean)}
	 * <p>
	 * OR
	 * <p>
	 * {@link RecursiveChildIterator#RecursiveChildIterator(Entity, boolean)}
	 */
	public Iterator<Entity> childIterator(boolean includeThis, boolean recursive) {
		return recursive ? new RecursiveChildIterator(this, includeThis)
				: new ChildIterator(this, includeThis);
	}

	/**
	 * Wrapps {@link Entity#childIterator(boolean, boolean)}.
	 * 
	 * @return iterable children.
	 */
	public SerializableIterable<Entity> iterableChildren(
			final boolean includeThis, final boolean recursive) {
		return new SerializableIterable<Entity>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Iterable#iterator()
			 */
			@Override
			public Iterator<Entity> iterator() {
				return childIterator(includeThis, recursive);
			}
		};
	}

	/**
	 * {@link ParentIterator#ParentIterator(Entity)}
	 */
	public Iterator<Entity> parentIterator() {
		return new ParentIterator(this);
	}

	/**
	 * {@link ParentIterator#ParentIterator(Entity, boolean)}
	 */
	public Iterator<Entity> parentIterator(boolean includeThis) {
		return new ParentIterator(this, includeThis);
	}

	/**
	 * @return true if this entity has no parent, otherwise false.
	 */
	public boolean isRoot() {
		return parent == null;
	}

	/**
	 * @return the ordering index of this entity.
	 */
	public int index() {
		return index;
	}

	/**
	 * @return true if and only if the cache index is completely valid.
	 */
	public boolean isCacheIndexValid() {
		return parent != null && parent.cachedChildren != null
				&& cacheIndex < parent.cachedChildren.size() ? parent.cachedChildren
				.get(cacheIndex) == this : false;
	}

	/**
	 * @return the cache index of this entity.
	 */
	public int cacheIndex() {
		return cacheIndex;
	}

	/**
	 * Sets the ordering index of this entity. If this entity is already
	 * attached the parent will sort its children again.
	 * 
	 * @param index
	 *            The new index.
	 * @return this for chaining.
	 */
	public Entity index(int index) {
		// A new index ??
		if (index != this.index) {

			// Save the old index
			int oldIndex = this.index;

			// Is there already a parent ?
			if (parent != null) {

				// Lookup
				Set<Entity> entities = parent.children.get(oldIndex);

				// If the set exist
				if (entities != null) {
					// Remove this entity
					if (!entities.remove(this)) {
						throw new IllegalStateException("This entity could "
								+ "not be removed from the old parent set. "
								+ "Please check your code.");
					} else if (!parent.lazyEntities(index).add(this)) {
						throw new IllegalStateException("This entity could "
								+ "not be added to the new parent set. "
								+ "Please check your code.");
					} else {
						// Clear the client cache!
						parent.clearCachedChildren();
					}

					// Clean up
					parent.removeSetIfEmpty(oldIndex, entities);
				} else {
					throw new IllegalStateException("The parent set of this "
							+ "entity is null. Please check your code.");
				}
			}

			// Save
			this.index = index;
		}
		return this;
	}

	/**
	 * @return the root entity (an entity which has no parent) of this entity or
	 *         this entity if this entity has no parent (which means that this
	 *         entity is a root entity).
	 */
	public Entity root() {
		return IterationRoutines.next(new FilteredIterator<Entity>(
				RootFilter.INSTANCE, parentIterator(true)));
	}

	/**
	 * @return the event-to-iterable map. An entity must declare the event types
	 *         here first to be able to fire them.
	 */
	public Map<Object, Iterable<? extends Entity>> events() {
		return events;
	}

	/**
	 * @param event
	 *            The event you want to fire.
	 * @return a new iterator for the given event or null.
	 */
	public Iterator<? extends Entity> eventIterator(Object event) {
		Iterable<? extends Entity> iterableEntities = events.get(event);
		return iterableEntities != null ? iterableEntities.iterator() : null;
	}

	/**
	 * Fires the given event for all entities using this entity as source.
	 * 
	 * @param event
	 *            The event.
	 * @param params
	 *            The parameters.
	 */
	public void fireEvent(Object event, Object... params) {
		fireEvent(null, event, params);
	}

	/**
	 * Fires the given event for all entities using this entity as source.
	 * 
	 * @param entityFilter
	 *            The entity event filter or null.
	 * @param event
	 *            The event.
	 * @param params
	 *            The parameters.
	 */
	public void fireEvent(EntityFilter entityFilter, Object event,
			Object... params) {
		fireEvent(entityFilter != null ? new FilteredIterator<Entity>(
				entityFilter, eventIterator(event)) : eventIterator(event),
				this, event, params);
	}

	/**
	 * Attaches all children in the array to this entity. If these entities have
	 * already parents they are detached automatically and will be attached
	 * afterwards.
	 * 
	 * @param children
	 *            The entities you want to attach as children.
	 * @return this for chaining.
	 */
	public Entity attach(Entity... children) {
		return attach((Iterator<Entity>) new ArrayIterator<Entity>(children));
	}

	/**
	 * Attaches all children given by the iterable interface to this entity. If
	 * these entities have already parents they are detached automatically and
	 * will be attached afterwards.
	 * 
	 * @param children
	 *            The entities you want to attach as children.
	 * @return this for chaining.
	 */
	public Entity attach(Iterable<Entity> children) {
		if (children == null) {
			throw new NullPointerException("children");
		}
		attach(children.iterator());
		return this;
	}

	/**
	 * Attaches all children given by the iterator to this entity. If these
	 * entities have already parents they are detached automatically and will be
	 * attached afterwards.
	 * 
	 * @param children
	 *            The entities you want to attach as children.
	 * @return this for chaining.
	 */
	public Entity attach(Iterator<Entity> children) {
		if (children == null) {
			throw new NullPointerException("children");
		}
		// Iterate over the given children
		while (children.hasNext()) {

			// Attach the next child
			attach(children.next());
		}
		return this;
	}

	/**
	 * Attaches a child to this entity. If this entity has already a parent it
	 * is detached automatically and will be attached afterwards.
	 * 
	 * @param child
	 *            The entity you want to attach as child.
	 * @return this for chaining.
	 */
	public Entity attach(Entity child) {
		if (child == null) {
			throw new NullPointerException("child");
		} else if (child == this) {
			throw new IllegalArgumentException("You cannot attach an entity "
					+ "to it self");
		} else if (child.parent != null) {
			// If this entity is already the parent...
			if (child.parent == this) {
				return this;
			} else if (!child.detach()) {
				throw new IllegalStateException("Unable to detach child");
			}
		}

		// Lookup entity set
		Set<Entity> entities = lazyEntities(child.index());

		if (entities.add(child)) {

			// Lookup root
			Entity root = root();

			/*
			 * Very important! When we attach a child we have to copy its
			 * registry to OUR root entity.
			 */
			root.registry.putAll(child.registry);

			// Clear the registry of the child
			child.registry.clear();

			// Set parent
			child.parent = this;

			// Cache is invalid now
			clearCachedChildren();

			// Fire the attached event for this entity
			fireEvent(EntityEvent.Attached, child);
			// Fire the attached event for the child
			child.fireEvent(EntityEvent.Attached, child);

			return this;
		} else {
			throw new IllegalStateException("This entity could not add the "
					+ "given child to its set. Please check your code.");
		}
	}

	/**
	 * @return the uuid of this entity which is the registry key.
	 */
	public UUID registryKey() {
		return registryKey;
	}

	/**
	 * @param typeClass
	 *            The type of the property.
	 * @return the property with the given type.
	 */
	@SuppressWarnings("unchecked")
	public <T> T typeProp(Class<T> typeClass) {
		if (typeClass == null) {
			throw new NullPointerException("typeClass");
		}
		return (T) props.get(typeClass);
	}

	/**
	 * Puts the given property. The class of the property is used as key.
	 * 
	 * @param value
	 *            The value you want to put.
	 * @return this for chaining.
	 */
	public Entity addTypeProp(Object value) {
		if (value == null) {
			throw new NullPointerException("value");
		}
		props.put(value.getClass(), value);
		return this;
	}

	/**
	 * Removes the given property type.
	 * 
	 * @param typeClass
	 *            The type of the property.
	 * @return this for chaining.
	 */
	public Entity removeTypeProp(Class<?> typeClass) {
		if (typeClass == null) {
			throw new NullPointerException("typeClass");
		}
		props.remove(typeClass);
		return this;
	}

	/**
	 * Puts the given property. If the key already exists the old value will be
	 * replaced.
	 * 
	 * @param key
	 *            The prop key.
	 * @param value
	 *            The prop value.
	 * @return this for chaining.
	 */
	public Entity addProp(Object key, Object value) {
		props.put(key, value);
		return this;
	}

	/**
	 * Removes the given property.
	 * 
	 * @param key
	 *            The prop key.
	 * @return this for chaining.
	 */
	public Entity removeProp(Object key) {
		props.remove(key);
		return this;
	}

	/**
	 * @param key
	 *            The key of the value you want to lookup.
	 * @return the value of the given key or null.
	 */
	public Object prop(Object key) {
		return props.get(key);
	}

	/**
	 * @return the properties of this entity.
	 */
	public Map<Object, Object> props() {
		return props;
	}

	/**
	 * Puts the given tag. If the key (tag) already exists the old value will be
	 * replaced.
	 * 
	 * @param tag
	 *            The tag.
	 * @return this for chaining.
	 */
	public Entity tag(Object tag) {
		return addProp(tag, TAG_OBJECT);
	}

	/**
	 * Removes the given tag.
	 * 
	 * @param tag
	 *            The tag.
	 * @return this for chaining.
	 */
	public Entity untag(Object tag) {
		return removeProp(tag);
	}

	/**
	 * Tells whether or not the given tag exists.
	 * 
	 * @param tag
	 *            The tag you want to check.
	 * @return true if the tag exists, otherwise false.
	 */
	public boolean tagged(Object tag) {
		return props.get(tag) == TAG_OBJECT;
	}

	/**
	 * Detaches all children from this entity.
	 */
	public boolean detachAll() {
		return detach(iterator());
	}

	/**
	 * Detaches all children in the array from this entity.
	 * 
	 * @param children
	 *            The entities you want to detach from this entity.
	 * @return true if all entities were children of this entity and are
	 *         detached successfully, otherwise false.
	 */
	public boolean detach(Entity... children) {
		return detach((Iterator<Entity>) new ArrayIterator<Entity>(children));
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
		} else if (child == this) {
			throw new IllegalArgumentException("You cannot detach an entity "
					+ "from it self");
		} else if (child.parent != this) {
			return false;
		}

		// Lookup
		Set<Entity> entities = children.get(child.index());

		// Check
		if (entities == null) {
			throw new IllegalStateException("The entity set of the given "
					+ "child order does not exist. Please check your code.");
		} else if (entities.remove(child)) {

			// Lookup root
			Entity root = root();

			/*
			 * Very important! When we detach a child we have to remove all
			 * (sub-) children of this child from the root registry and copy
			 * them to the child registry.
			 */
			for (Entity subChild : child.iterableChildren(true, true)) {
				root.registry.remove(subChild.registryKey);
				child.registry.put(subChild.registryKey, subChild);
			}

			// Clean up
			removeSetIfEmpty(child.index(), entities);

			// Clear parent
			child.parent = null;

			// Cache is invalid now
			clearCachedChildren();

			// Fire the detached event for this entity
			fireEvent(EntityEvent.Detached, child);
			// Fire the detached event for the child
			child.fireEvent(EntityEvent.Detached, child);

			return true;
		} else {
			throw new IllegalStateException("This entity could not remove "
					+ "the given child from its set. Please check your code.");
		}
	}

	/**
	 * Updates this entity and all children.
	 * 
	 * @param tpf
	 *            The passed time to update time-sensitive data.
	 * @return this for chaining.
	 */
	public Entity update(float tpf) {
		return update(null, tpf);
	}

	/**
	 * Updates this entity and all children using the given filter.
	 * 
	 * @param EntityFilter
	 *            The entity filter or null.
	 * @param tpf
	 *            The passed time to update time-sensitive data.
	 * @return this for chaining.
	 */
	public Entity update(EntityFilter entityFilter, float tpf) {

		// Fire event for all children
		fireEvent(entityFilter, EntityEvent.Update, tpf);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Entity o) {
		if (index > o.index) {
			return 1;
		} else if (index == o.index) {
			return 0;
		} else {
			return -1;
		}
	}

	@Override
	public String toString() {
		return "[Name = " + name + ", Index = " + index + ", Properties = "
				+ props + "]";
	}
}

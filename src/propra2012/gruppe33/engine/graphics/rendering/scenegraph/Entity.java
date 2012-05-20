package propra2012.gruppe33.engine.graphics.rendering.scenegraph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.filters.RootFilter;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.iterators.ChildIterator;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.iterators.FilteredIterator;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.iterators.ParentIterator;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.iterators.RecursiveChildIterator;

/**
 * 
 * @author Christopher Probst
 * @see EntityFilter
 */
public class Entity implements Comparable<Entity>, Iterable<Entity>,
		Serializable {

	/**
	 * 
	 * The default entity events.
	 * 
	 * @author Christopher Probst
	 * 
	 */
	public enum EntityEvent {
		Attached, ChildAttached, Detached, ChildDetached, Update
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Fires the event for the given entity.
	 * 
	 * @param event
	 *            The event.
	 * @param params
	 *            The parameters.
	 */
	public static void fireEvent(Entity entity, Object event, Object... params) {
		if (entity == null) {
			throw new NullPointerException("entity");
		}
		// Just call the method
		entity.onEvent(event, params);
	}

	/**
	 * Fires the given event for all entities.
	 * 
	 * @param entityIterator
	 *            The entity iterator.
	 * @param event
	 *            The event.
	 * @param params
	 *            The parameters.
	 */
	public static void fireEvent(Iterator<? extends Entity> entityIterator,
			Object event, Object... params) {
		if (entityIterator == null) {
			throw new NullPointerException("entityIterator");
		}

		// Post event to all entities
		while (entityIterator.hasNext()) {
			fireEvent(entityIterator.next(), event, params);
		}
	}

	/*
	 * The name of this entity.
	 */
	private String name = super.toString();

	/*
	 * The index of this entity.
	 */
	private int index = 0;

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
	 * @param event
	 *            The event.
	 * @param params
	 *            The parameters.
	 */
	protected void onEvent(Object event, Object... params) {
		/*
		 * Process the default events.
		 */
		if (event instanceof EntityEvent) {
			switch ((EntityEvent) event) {
			case Attached:
				onAttached();
				break;
			case ChildAttached:
				onChildAttached((Entity) params[0]);
				break;
			case Detached:
				onDetached();
				break;
			case ChildDetached:
				onChildDetached((Entity) params[0]);
				break;
			case Update:
				onUpdate((Float) params[0]);
			}
		}
	}

	/**
	 * OVERRIDE FOR CUSTOM BEHAVIOUR.
	 * 
	 * This method is called when this entity is attached to an entity.
	 */
	protected void onAttached() {

	}

	/**
	 * OVERRIDE FOR CUSTOM BEHAVIOUR.
	 * 
	 * This method is called when this entity is detached from an entity.
	 */
	protected void onDetached() {

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
	 * This method is called when a child is detached from this entity.
	 * 
	 * @param child
	 *            The child which has been detached.
	 */
	protected void onChildDetached(Entity child) {

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
	protected void onUpdate(float tpf) {
	}

	/**
	 * @return the name.
	 */
	public String getName() {
		return name;
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
	 * @return true if this entity has children, otherwise false.
	 */
	public boolean hasChildren() {
		return !children.isEmpty();
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

			// Copy all entities to the list
			for (Set<Entity> entities : children.values()) {
				for (Entity entity : entities) {
					cachedChildren.add(entity);
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
	 * @return true if this entity has no parent, otherwise false.
	 */
	public boolean isRoot() {
		return parent == null;
	}

	/**
	 * @return the index of this entity.
	 */
	public int index() {
		return index;
	}

	/**
	 * Sets the index of this entity. If this entity is already attached the
	 * parent will sort its children again.
	 * 
	 * @param index
	 *            The new index.
	 * @return the old index.
	 */
	public int index(int index) {
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
					}

					// Clean up
					parent.removeSetIfEmpty(oldIndex, entities);
				} else {
					throw new IllegalStateException("The parent set of this "
							+ "entity is null. Please check your code.");
				}

				// Try to add
				if (!parent.getLazyEntities(index).add(this)) {
					throw new IllegalStateException("This entity could not be "
							+ "added to the new parent set. "
							+ "Please check your code.");
				}
			}

			// Save
			this.index = index;

			// Return the old index
			return oldIndex;
		} else {
			return this.index;
		}
	}

	/**
	 * @return the root entity (an entity which has no parent) of this entity or
	 *         this entity if this entity has no parent (which means that this
	 *         entity is a root entity).
	 */
	public Entity root() {
		Iterator<Entity> itr = new FilteredIterator<Entity>(
				RootFilter.INSTANCE, new ParentIterator(this, true));
		return itr.hasNext() ? itr.next() : null;
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
		Set<Entity> entities = getLazyEntities(child.index());

		if (entities.add(child)) {

			// Set parent
			child.parent = this;

			// Cache is invalid now
			clearCachedChildren();

			// Fire the attached event
			fireEvent(child, EntityEvent.Attached);

			// Fire the child attached event
			fireEvent(this, EntityEvent.ChildAttached, child);
		} else {
			throw new IllegalStateException("This entity could not add the "
					+ "given child to its set. Please check your code.");
		}
	}

	/**
	 * Detaches all children from this entity.
	 */
	public void detachAll() {
		for (Entity child : this) {
			detach(child);
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
		Set<Entity> entities = children.get(child.index());

		// Check
		if (entities == null) {
			throw new IllegalStateException("The entity set of the given "
					+ "child order does not exist. Please check your code.");
		} else if (entities.remove(child)) {

			// Clean up
			removeSetIfEmpty(child.index(), entities);

			// Clear parent
			child.parent = null;

			// Cache is invalid now
			clearCachedChildren();

			// Fire the detached event
			fireEvent(child, EntityEvent.Detached);

			// Fire the child detached event
			fireEvent(this, EntityEvent.ChildDetached, child);

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
	 */
	public final void update(float tpf) {
		update(null, tpf);
	}

	/**
	 * Updates this entity and all children.
	 * 
	 * @param EntityFilter
	 *            The entity filter or null.
	 * @param tpf
	 *            The passed time to update time-sensitive data.
	 */
	public final void update(EntityFilter entityFilter, float tpf) {

		// Fire event for all children
		fireEvent(new FilteredIterator<Entity>(entityFilter,
				new RecursiveChildIterator(this, true)), EntityEvent.Update,
				tpf);
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
		return "[Name = " + name + ", Index = " + index + "]";
	}
}

package propra2012.gruppe33.graphics.rendering.scenegraph;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
 * - An entity can have controllers which basically provides dynamic behaviour,
 * because you can add or remove controllers whenever you like.
 * 
 * 
 * 
 * 
 * @author Christopher Probst
 * @see Vector2f
 * @see Scene
 * 
 */
public class Entity implements EntityController {

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
	 * Maps names to entity controllers.
	 */
	private final Map<Class<? extends EntityController>, EntityController> controllers = new LinkedHashMap<Class<? extends EntityController>, EntityController>();

	/*
	 * Used to cache controller iterations.
	 */
	private List<EntityController> controllerCache = null;

	/*
	 * Maps names to entities.
	 */
	private final Map<String, List<Entity>> names2Entities = new HashMap<String, List<Entity>>();

	/*
	 * Used to cache entity iterations.
	 */
	private List<Entity> entityCache = null;

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
		if (name == null) {
			throw new NullPointerException("name");
		}

		// Try to get list
		List<Entity> entities = names2Entities.get(name);

		if (entities == null) {
			names2Entities.put(name, entities = new LinkedList<Entity>());
		}

		return entities;
	}

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
	private List<Entity> getEntityCache() {
		return entityCache != null ? entityCache
				: (entityCache = new ArrayList<Entity>(children));
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
		controllerCache = null;

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
			controllerCache = null;
		}

		return result;
	}

	/**
	 * Lookup a controller.
	 * 
	 * @param controllerClass
	 *            The controller class you want to lookup.
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
		controllerCache = null;
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
		if (name == null) {
			throw new NullPointerException("name");
		}

		// Lookup the name list
		List<Entity> list = names2Entities.get(name);

		return list != null ? Collections.unmodifiableList(list) : null;
	}

	/**
	 * Detaches all children from this entity.
	 */
	public void detachAll() {
		for (Entity child : getEntityCache()) {
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
	 * {@link Entity#findParentEntity(Class)}
	 * 
	 * @see Scene
	 */
	public Scene findScene() {
		return findParentEntity(Scene.class);
	}

	/**
	 * Find an entity of the given class in the parent hierarchy.
	 * 
	 * @param entityClass
	 *            The entity class you want to find.
	 * @return the next entity of the given class which ownes this entity or
	 *         this entity if this entity has already the correct class or null
	 *         if there is no valid parent entity in the hierarchy at all.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Entity> T findParentEntity(Class<T> entityClass) {
		if (entityClass == null) {
			throw new NullPointerException("entityClass");
		}

		return getClass().equals(entityClass) ? (T) this
				: parent != null ? parent.findParentEntity(entityClass) : null;
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
			entityCache = null;
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
			entityCache = null;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * propra2012.gruppe33.graphics.rendering.scenegraph.EntityController#doRender
	 * (propra2012.gruppe33.graphics.rendering.scenegraph.Entity,
	 * java.awt.Graphics2D, java.awt.Graphics2D)
	 */
	@Override
	public void doRender(Entity entity, Graphics2D original,
			Graphics2D transformed) {

		// Is not rendered in default impl. for performance reasons.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * propra2012.gruppe33.graphics.rendering.scenegraph.EntityController#doUpdate
	 * (propra2012.gruppe33.graphics.rendering.scenegraph.Entity, float)
	 */
	@Override
	public void doUpdate(Entity entity, float tpf) {
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
		for (Entity child : getEntityCache()) {
			// Just update
			child.update(tpf);
		}
	}

	/**
	 * Renders this entity into a picture component of the given entity. If
	 * there is no picture component yet, a new one will be created using the
	 * given transparency and the next scene in hierarchy and use its dimension
	 * to create a new image. If there is no scene an exception will be thrown.
	 * 
	 * @param entity
	 *            The entity in which you want to render.
	 * @param optTransparency
	 *            The optional transparency of the image if there is no picture
	 *            component yet.
	 */
	public final void renderTo(Entity entity, int optTransparency) {
		renderTo(entity, -1, -1, optTransparency);
	}

	/**
	 * Renders this entity into a picture component of the given entity. If
	 * there is no picture component yet, a new one will be created using the
	 * given parameters. If there parameters are invalid the method will get the
	 * next scene in hierarchy and use its dimension to create a new image. If
	 * there is no scene an exception will be thrown.
	 * 
	 * @param entity
	 *            The entity in which you want to render.
	 * @param optWidth
	 *            The optional width of the image if there is no picture
	 *            component yet.
	 * @param optHeight
	 *            The optional height of the image if there is no picture
	 *            component yet.
	 * @param optTransparency
	 *            The optional transparency of the image if there is no picture
	 *            component yet.
	 */
	public final void renderTo(Entity entity, int optWidth, int optHeight,
			int optTransparency) {
		if (entity == null) {
			throw new NullPointerException("entity");
		}

		// Lookup picture controller
		PictureController pic = entity.getController(PictureController.class);

		// Oops... thats bad
		if (pic == null) {

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
							"The entity has no picture component and the "
									+ "width/height you specified is not valid. But "
									+ "there is no scene in your hierarchy, too, so "
									+ "this method can not create a new image.");
				} else {
					// Reset width and height
					optWidth = scene.getWidth();
					optHeight = scene.getHeight();
				}
			}

			// Create a new picture and add to entity
			entity.putController(pic = new PictureController(optWidth,
					optHeight, optTransparency));
		}

		// Render to the image
		renderTo(pic.getImage());
	}

	/**
	 * Renders this entity and all children to an image. This method uses the
	 * visible/childrenVisible flags to determine what to render.
	 * 
	 * @param destination
	 *            The image you want to render to.
	 */
	public final void renderTo(Image destination) {
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
				for (Entity child : getEntityCache()) {
					// Just render
					child.render(original, transformedCopy);
				}
			}
		} finally {
			// Dispose the transformed copy
			transformedCopy.dispose();
		}
	}
}

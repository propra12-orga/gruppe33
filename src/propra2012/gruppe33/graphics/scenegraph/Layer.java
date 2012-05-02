package propra2012.gruppe33.graphics.scenegraph;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.VolatileImage;
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
public final class Layer {

	/**
	 * Create a volatile image using width and height.
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	public VolatileImage createVolatileImage() {
		return GRAPHICS_CONFIGURATION.createCompatibleVolatileImage(
				getSceneGraph().getWidth(), getSceneGraph().getHeight(),
				Transparency.TRANSLUCENT);
	}

	// The graphics conf of this system.
	public static final GraphicsConfiguration GRAPHICS_CONFIGURATION = GraphicsEnvironment
			.getLocalGraphicsEnvironment().getDefaultScreenDevice()
			.getDefaultConfiguration();

	// Linked hash set storing the entities
	private final Set<Entity> entities = new LinkedHashSet<Entity>();

	// The entity cache
	private List<Entity> cache = null;

	// Maps ids to entities
	private final Map<String, List<Entity>> ids2Entities = new HashMap<String, List<Entity>>();

	// The volatile image which is used to render this layer
	private VolatileImage image;

	// The scene graph of this layer
	private final SceneGraph sceneGraph;

	// The name of the layer
	private final String name;

	private List<Entity> getEntityList(String id) {
		// Try to get list
		List<Entity> entities = ids2Entities.get(id);

		if (entities == null) {
			ids2Entities.put(id, entities = new LinkedList<Entity>());
		}

		return entities;
	}

	private List<Entity> getCache() {
		if (cache == null) {
			cache = new ArrayList<Entity>(entities);
		}
		return cache;
	}

	Layer(SceneGraph sceneGraph, String name) {
		if (sceneGraph == null) {
			throw new NullPointerException("sceneGraph");
		}
		if (name == null) {
			throw new NullPointerException("name");
		}
		this.sceneGraph = sceneGraph;
		this.name = name;
	}

	protected void repaint(Graphics2D g) {

		// Create a copy
		Graphics2D copy = (Graphics2D) g.create();

		// Use anti-aliasing
		copy.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		// Clear the image
		copy.clearRect(0, 0, image.getWidth(), image.getHeight());

		// Repaint all items
		for (Entity entity : getCache()) {

			// Create a local copy
			Graphics2D entityCopy = (Graphics2D) copy.create();

			// Translate the coords
			entityCopy
					.translate(entity.getPosition().x, entity.getPosition().y);

			// Rotate the entity
			entityCopy.rotate(entity.getRotation());

			// Render the entity
			entity.render(entityCopy);

			// Dispose everything
			entityCopy.dispose();
		}

		// Dispose the entity
		copy.dispose();
	}

	public List<Entity> find(String id) {
		// Lookup the id list
		List<Entity> list = ids2Entities.get(id);

		return list != null ? Collections.unmodifiableList(list) : null;
	}

	public boolean hasEntities() {
		return !entities.isEmpty();
	}

	public void clearEntities() {
		// Remove all entities
		for (Entity entity : getCache()) {
			removeEntity(entity);
		}
	}

	public String getName() {
		return name;
	}

	public SceneGraph getSceneGraph() {
		return sceneGraph;
	}

	public boolean addEntity(Entity entity) {
		if (entity == null) {
			throw new NullPointerException("entity");
		} else if (entity.getLayer() != null) {
			// Remove from old layer
			entity.layer.removeEntity(entity);
		}

		// Add and return result
		if (entities.add(entity)) {
			// Accept and set layer
			entity.layer = this;

			// Get entity name list
			List<Entity> list = getEntityList(entity.getId());

			// Just add the entity
			list.add(entity);

			// Cache has been changed
			cache = null;

			return true;
		} else {
			return false;
		}
	}

	public boolean removeEntity(Entity entity) {
		if (entity == null) {
			throw new NullPointerException("entity");
		} else if (entity.getLayer() != this) {
			throw new IllegalArgumentException(
					"entity must be owned by this layer");
		}

		// Remove and return result
		if (entities.remove(entity)) {
			// Remove the layer
			entity.layer = null;

			// Get entity name list
			List<Entity> list = getEntityList(entity.getId());

			// Remove the entity
			list.remove(entity);

			// Was this the last entity with the given name ?
			if (list.isEmpty()) {
				// Remove from map
				ids2Entities.remove(entity.getId());
			}

			// Cache has been changed
			cache = null;

			return true;
		} else {
			return false;
		}
	}

	public boolean validSize() {
		return image.getWidth() == getSceneGraph().getWidth()
				&& image.getHeight() == getSceneGraph().getHeight();
	}

	public boolean needRepaint(boolean isLayerDirty) {

		// We should not repaint...
		if (sceneGraph.getWidth() <= 0 || sceneGraph.getHeight() <= 0) {
			return false;
		}

		// Not even initialized!
		if (image == null) {
			// Create new image
			image = createVolatileImage();

			// We need an update!
			return true;
		} else {
			// Validate the image
			switch (image.validate(GRAPHICS_CONFIGURATION)) {
			case VolatileImage.IMAGE_RESTORED:

				// Check if wrong size
				if (!validSize()) {
					image = createVolatileImage();
				}

				// Always repaint!
				return true;

			case VolatileImage.IMAGE_INCOMPATIBLE:
				// Create new image
				image = createVolatileImage();

				// We need an update!
				return true;

			case VolatileImage.IMAGE_OK:
				// Check if wrong size
				if (!validSize()) {
					image = createVolatileImage();
					// Repaint!
					return true;
				} else {
					// Return the is dirty flag!
					return isLayerDirty;
				}

			default:
				throw new IllegalStateException("Unknown image validation");
			}
		}
	}

	public VolatileImage getImage() {
		return image;
	}

	public void update(float tpf) {
		// Update all entities
		for (Entity entity : getCache()) {
			entity.update(tpf);
		}
	}

	public VolatileImage repaint() {

		// Check if this layer has to be repainted...
		if (needRepaint(getSceneGraph().getDirtyLayers().contains(getName()))) {

			do {

				// Open graphics context
				Graphics2D g2d = image.createGraphics();

				// Repaint the layer
				repaint(g2d);

				// Dispose the context
				g2d.dispose();

			} while (image.contentsLost());

			// Remoe dirty flag if set...
			getSceneGraph().getDirtyLayers().remove(getName());
		}

		return image;
	}
}

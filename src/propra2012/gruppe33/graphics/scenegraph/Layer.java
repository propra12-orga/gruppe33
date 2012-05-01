package propra2012.gruppe33.graphics.scenegraph;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
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
public final class Layer extends SceneGraphObject {

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

	private final Set<Entity> entities = new LinkedHashSet<Entity>();
	private List<Entity> cache = null;
	private final Map<String, List<Entity>> names2Entities = new HashMap<String, List<Entity>>();
	private VolatileImage image;

	private List<Entity> getEntityList(String name) {
		// Try to get list
		List<Entity> entities = names2Entities.get(name);

		if (entities == null) {
			names2Entities.put(name, entities = new LinkedList<Entity>());
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
		super(sceneGraph, name);
	}

	protected void repaint(Graphics2D g) {
		// Clear the image
		g.clearRect(0, 0, image.getWidth(), image.getHeight());

		// Repaint all items
		for (Entity entity : getCache()) {

			// Create a local copy
			Graphics2D copy = (Graphics2D) g.create();

			// Translate the coords
			copy.translate(entity.getPosition().x / image.getWidth(),
					entity.getPosition().y / image.getHeight());

			// Render the entity
			entity.render(copy);

			// Dispose everything
			copy.dispose();
		}
	}

	public List<Entity> find(String name) {
		// Lookup the name list
		List<Entity> list = names2Entities.get(name);

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
			List<Entity> list = getEntityList(entity.getName());

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
			List<Entity> list = getEntityList(entity.getName());

			// Remove the entity
			list.remove(entity);

			// Was this the last entity with the given name ?
			if (list.isEmpty()) {
				// Remove from map
				names2Entities.remove(entity.getName());
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

	public VolatileImage ensureImage() {
		// Just call this method with false
		needRepaint(false);

		return image;
	}

	public boolean needRepaint(boolean isLayerDirty) {

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

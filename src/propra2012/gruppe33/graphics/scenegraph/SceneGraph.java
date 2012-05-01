package propra2012.gruppe33.graphics.scenegraph;

import java.awt.Graphics;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * This class stores, updates and renders all entities in the scene. We use
 * multiply layers to improve render performance. Only layers marked as dirty
 * are redrawn. In addition: We use the VolatileImage class which is technically
 * a direct reference to a high performance opengl texture.
 * 
 * The scene graph uses a relative coord system. Which means that we actually
 * only use values between 0 and 1 to adjust the scale of the game while
 * resizing.
 * 
 * @author Christopher Probst
 * 
 * 
 *         IMPORTANT:
 * 
 *         -Dsun.java2d.translaccel=true
 */
public final class SceneGraph {

	/*
	 * Here we store all layers. The layers are sorted by name using a TreeMap.
	 */
	private Map<String, Layer> layers = new TreeMap<String, Layer>();

	/*
	 * Used to mark layers as dirty and force repaint.
	 */
	private Set<String> dirtyLayers = new HashSet<String>();

	/*
	 * The dimension of the scene graph.
	 */
	private int width, height;

	public List<Entity> find(String name) {
		List<Entity> query = null;
		for (Layer layer : getLayers()) {
			List<Entity> tmp = layer.find(name);
			if (tmp != null) {
				(query != null ? query : (query = new LinkedList<Entity>()))
						.addAll(tmp);
			}
		}
		return query;
	}

	public void clearAllEntities() {
		// Clear all entities
		for (Layer layer : getLayers()) {
			layer.clearEntities();
		}
	}

	public Layer getLayer(String name) {
		// Get layer from map
		Layer layer = layers.get(name);

		// Verify
		if (layer == null) {

			// Assign new layer and put into map
			layers.put(name, layer = new Layer(this, name));
		}

		return layer;
	}

	public void makeAllLayersDirty() {
		for (Layer layer : getLayers()) {
			makeLayerDirty(layer.getName());
		}
	}

	public boolean makeLayerDirty(String name) {
		return dirtyLayers.add(name);
	}

	public Collection<Layer> getLayers() {
		return layers.values();
	}

	public Set<String> getDirtyLayers() {
		return dirtyLayers;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void updateLayers(float tpf) {
		// Get the iterator
		Iterator<Layer> layers = getLayers().iterator();

		// For all layers...
		while (layers.hasNext()) {

			// Get layer
			Layer layer = layers.next();

			// Is this layer out-dated ?
			if (!layer.hasEntities()) {
				// Remove from layers
				layers.remove();
			} else {
				// Update the layer
				layer.update(tpf);
			}
		}
	}

	public void repaintLayers(Graphics g) {
		// Get the iterator
		Iterator<Layer> layers = getLayers().iterator();

		// For all layers...
		while (layers.hasNext()) {

			// Get layer
			Layer layer = layers.next();

			// Is this layer out-dated ?
			if (!layer.hasEntities()) {
				// Remove from layers
				layers.remove();
			} else {
				// Manage, get and paint the volatile image
				g.drawImage(layer.repaint(), 0, 0, null);
			}
		}
	}
}

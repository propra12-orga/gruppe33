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

	/*
	 * The last timestamp.
	 */
	private long timestamp = -1;

	public SceneGraph(int width, int height) {
		setWidth(width);
		setHeight(height);
	}

	public List<Entity> find(String id) {
		List<Entity> query = null;
		for (Layer layer : getLayers()) {
			List<Entity> tmp = layer.find(id);
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
		if (height <= 0) {
			throw new IllegalArgumentException("height out of range");
		}

		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		if (width <= 0) {
			throw new IllegalArgumentException("width out of range");
		}

		this.width = width;
	}

	public void clearTimestamp() {
		timestamp = -1;
	}

	public void updateLayers() {

		// Get active time
		long time = System.currentTimeMillis();

		// Check for -1
		if (timestamp == -1) {
			timestamp = time;
		}

		// Convert to seconds
		float tpf = (time - timestamp) * 0.001f;

		// Save the last timestamp
		timestamp = time;

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

	public void repaintLayers(Graphics g, int dWidth, int dHeight) {
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

				// Calc image ratio
				float imRatio = getWidth() / (float) getHeight();

				// Calc new dimension
				float newWidth = dWidth, newHeight = dWidth / imRatio;

				// Check height
				if (newHeight > dHeight) {

					// Recalc
					newHeight = dHeight;
					newWidth = imRatio * newHeight;
				}

				// Calc offset
				int x = (int) (dWidth * 0.5f - newWidth * 0.5f);
				int y = (int) (dHeight * 0.5f - newHeight * 0.5f);

				// Manage, get and paint the volatile image
				g.drawImage(layer.repaint(), x, y, (int) newWidth,
						(int) newHeight, null);
			}
		}
	}
}

package propra2012.gruppe33.graphics.gui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.JPanel;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class JMapPanel extends JPanel {

	public static final String TOP_LAYER = "TOP_LAYER";
	public static final String BOTTOM_LAYER = "BOTTOM_LAYER";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * This map contains all layers of the map. The layers are drawn in FIFO
	 * order.
	 */
	private LinkedList<Image> layers = new LinkedList<Image>();

	/*
	 * This map contains all objects which are rendered.
	 */
	private Map<String, MapObject> objects = new LinkedHashMap<String, MapObject>();

	/**
	 * Create the panel.
	 */
	public JMapPanel() {

	}

	public Map<String, MapObject> getObjects() {
		return objects;
	}

	public LinkedList<Image> getLayers() {
		return layers;
	}

	public void paintComponent(Graphics g) {
		// Paint the native stuff
		super.paintComponent(g);

		// For all images
//		for (BufferedImage bi : layers.values()) {
//
//			// Draw at origin!
//			g.drawImage(bi, 0, 0, null);
//		}
	}
}

package propra2012.gruppe33.graphics.gui;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public class MapObject {

	// Here
	private Point2D coords = new Point2D.Float(0, 0);

	// The layer on which is object is drawn
	private String layer = JMapPanel.TOP_LAYER;

	// The id of the object
	private String id;

	public String getLayer() {
		return layer;
	}

	public void setLayer(String layer) {
		this.layer = layer;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Point2D getCoords() {
		return coords;
	}

	public void render(Graphics2D graphics) {

	}
}

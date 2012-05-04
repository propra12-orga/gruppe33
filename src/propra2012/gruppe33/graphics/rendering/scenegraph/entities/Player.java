package propra2012.gruppe33.graphics.rendering.scenegraph.entities;

import java.awt.Point;

import propra2012.gruppe33.graphics.rendering.scenegraph.Entity;

public class Player extends Entity {

	public Player(String id) {
		super(id);
	}
	private final Point target = new Point(1, 1), active = new Point(0, 0);

	public Point getTarget() {
		return target;
	}

	public Point getActive() {
		return active;
	}
	
//	@Override
//	protected void doRender(Graphics2D original, Graphics2D transformed) {
//		super.doRender(original, transformed);
//
//		Vector2f p = level.gridToWorld(level.worldToNearestGrid(getPosition()));
//
//		// The rect
//		// Graphics2D c = (Graphics2D) original.create();
//		//
//		// c.translate(p.x - level.getRasterWidth() * 0.5f,
//		// p.y - level.getRasterHeight() * 0.5f);
//		// c.scale(level.getRasterWidth(), level.getRasterHeight());
//		// c.setColor(Color.BLUE);
//		// c.fillRect(0, 0, 1, 1);
//		//
//		// c.dispose();
//
//		transformed.setColor(Color.red);
//		transformed.fillRect(-6, -6, 12, 12);
//	}
//
//	protected void processInput(float tpf, boolean equal) {
//
//		Scene scene = getScene();
//
//		if (scene != null) {
//
//			int nx = 0, ny = 0;
//
//			if (scene.isPressed(KeyEvent.VK_LEFT)) {
//				nx = -1;
//			}
//			if (scene.isPressed(KeyEvent.VK_RIGHT)) {
//				nx = 1;
//			}
//			if (scene.isPressed(KeyEvent.VK_UP)) {
//				ny = -1;
//			}
//			if (scene.isPressed(KeyEvent.VK_DOWN)) {
//				ny = 1;
//			}
//			if (nx != 0 || ny != 0) {
//
//				if (level.at(active.x + nx, active.y) == '0') {
//					active.x += nx;
//				}
//				if (level.at(active.x, active.y + ny) == '0') {
//					active.y += ny;
//				}
//
//				target.setLocation(active.x, active.y);
//			}
//		}
//
//	}
//
//
//	@Override
//	protected void doUpdate(float tpf) {
//		super.doUpdate(tpf);
//
//		// Move towards to vec
//		Vector2f vec = level.gridToWorld(target);
//
//		// Move with default speed
//		setPosition(Vector2f.moveTowards(getPosition(), vec, 300 * tpf));
//
//		// Check threshold
//		boolean equal = vec
//				.threshold(getPosition(), new Vector2f(0.01f, 0.01f));
//
//		// Set location if equal
//		if (equal) {
//			active.setLocation(target);
//		}
//
//		// Further input processing
//		processInput(tpf, equal);
//	}
}

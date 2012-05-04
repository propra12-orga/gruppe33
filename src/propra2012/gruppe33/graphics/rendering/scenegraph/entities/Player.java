package propra2012.gruppe33.graphics.rendering.scenegraph.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import propra2012.gruppe33.graphics.rendering.level.Level;
import propra2012.gruppe33.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.graphics.rendering.scenegraph.Scene;
import propra2012.gruppe33.graphics.rendering.scenegraph.Vector2f;

public class Player extends Entity {

	private final Level level;

	public Player(String id, Level level) {
		super(id);
		this.level = level;
	}

	public Level getLevel() {
		return level;
	}

	private final Point target = new Point();

	public Point getTarget() {
		return target;
	}

	@Override
	protected void doRender(Graphics2D original, Graphics2D transformed) {
		super.doRender(original, transformed);

		Vector2f p = level.gridToWorld(level.worldToNearestGrid(getPosition()));

		// The rect
		Graphics2D c = (Graphics2D) original.create();

		c.translate(p.x - level.getRasterWidth() * 0.5f,
				p.y - level.getRasterHeight() * 0.5f);
		c.scale(level.getRasterWidth(), level.getRasterHeight());
		c.setColor(Color.BLUE);
		c.fillRect(0, 0, 1, 1);

		c.dispose();

		transformed.setColor(Color.red);
		transformed.fillRect(-6, -6, 12, 12);
	}

	@Override
	protected void doUpdate(float tpf) {
		super.doUpdate(tpf);

		Vector2f vec = level.gridToWorld(target);

		setPosition(Vector2f.moveTowards(getPosition(), vec, 50f * tpf));

		
		Scene scene = getScene();
		//
		// if (scene != null) {
		//
		// if (scene.isPressed(KeyEvent.VK_LEFT)) {
		// getPosition().x -= tpf * 100;
		// }
		// if (scene.isPressed(KeyEvent.VK_RIGHT)) {
		// getPosition().x += tpf * 100;
		// }
		// if (scene.isPressed(KeyEvent.VK_UP)) {
		// getPosition().y -= tpf * 100;
		// }
		// if (scene.isPressed(KeyEvent.VK_DOWN)) {
		// getPosition().y += tpf * 100;
		// }
		//
		// }

	}

}

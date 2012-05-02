package propra2012.gruppe33.graphics.rendering.scenegraph.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import propra2012.gruppe33.graphics.rendering.level.Level;
import propra2012.gruppe33.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.graphics.rendering.scenegraph.Scene;

public class Player extends Entity {

	private final Level level;

	public Player(String id, Level level) {
		super(id);
		this.level = level;
	}

	public Level getLevel() {
		return level;
	}

	@Override
	protected void doUpdate(float tpf) {
		super.doUpdate(tpf);

		Scene scene = getScene();

		if (scene != null) {

			if (scene.isPressed(KeyEvent.VK_LEFT)) {
				getPosition().x -= tpf * 400;
			}
			if (scene.isPressed(KeyEvent.VK_RIGHT)) {
				getPosition().x += tpf * 400;
			}
			if (scene.isPressed(KeyEvent.VK_UP)) {
				getPosition().y -= tpf * 400;
			}
			if (scene.isPressed(KeyEvent.VK_DOWN)) {
				getPosition().y += tpf * 400;
			}

		}
	}

	@Override
	protected void doRender(Graphics2D g) {
		super.doRender(g);

		g.setColor(Color.blue);
		g.fillArc(-30, -30, 60, 60, 0, 360);
	}

}

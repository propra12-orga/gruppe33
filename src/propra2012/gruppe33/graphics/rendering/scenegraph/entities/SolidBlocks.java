package propra2012.gruppe33.graphics.rendering.scenegraph.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import propra2012.gruppe33.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.graphics.rendering.scenegraph.Scene;

public class SolidBlocks extends Entity {

	private final BufferedImage blockImage;

	public BufferedImage createImage(int w, int h) {
		return GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getDefaultConfiguration()
				.createCompatibleImage(w, h, Transparency.BITMASK);
	}

	public SolidBlocks(String id, BufferedImage blockImageTile, char[][] map,
			int tw, int th) {
		super(id);

		int w = map.length * tw;
		int h = map[0].length * th;

		// Create new image
		blockImage = createImage(w, h);

		Graphics2D g = blockImage.createGraphics();

		g.setBackground(new Color(0, 0, 0, 0));
		g.clearRect(0, 0, w, h);

		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				char c = map[i][j];

				if (c == '1') {
					g.drawImage(blockImageTile, j * tw, i * th, tw, th, null);
				}
			}
		}

		g.dispose();
	}

	public BufferedImage getBlockImage() {
		return blockImage;
	}

	@Override
	protected void doUpdate(float tpf) {
		super.doUpdate(tpf);

		Scene scene = getScene();

		if (scene != null) {
			if (scene.isPressed(KeyEvent.VK_LEFT)) {
				getPosition().x -= tpf * 10;
			} else if (scene.isPressed(KeyEvent.VK_RIGHT)) {
				getPosition().x += tpf * 10;
			}
		}
	}

	@Override
	protected void doRender(Graphics2D g) {
		super.doRender(g);

		g.drawImage(blockImage, 0, 0, null);
	}
}

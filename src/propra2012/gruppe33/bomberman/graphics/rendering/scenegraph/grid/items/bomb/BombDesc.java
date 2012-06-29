package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.items.bomb;

import java.util.UUID;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.items.ItemDesc;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class BombDesc extends ItemDesc {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private UUID player;
	private int range;
	private float delay;

	public UUID player() {
		return player;
	}

	public BombDesc player(UUID player) {
		this.player = player;
		return this;
	}

	public int range() {
		return range;
	}

	public BombDesc range(int range) {
		this.range = range;
		return this;
	}

	public float delay() {
		return delay;
	}

	public BombDesc delay(float delay) {
		this.delay = delay;
		return this;
	}
}

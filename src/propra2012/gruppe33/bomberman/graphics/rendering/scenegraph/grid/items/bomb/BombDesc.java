package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.items.bomb;

import java.io.Serializable;
import java.util.UUID;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.items.CollectableItem;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class BombDesc implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The bomb type of this bomb
	private CollectableItem bomb;
	private UUID bombImage, player;
	private int range;
	private float delay;

	public CollectableItem bomb() {
		return bomb;
	}

	public BombDesc bomb(CollectableItem bomb) {
		this.bomb = bomb;
		return this;
	}

	public UUID bombImage() {
		return bombImage;
	}

	public BombDesc bombImage(UUID bombImage) {
		this.bombImage = bombImage;
		return this;
	}

	public BombDesc randomBombImage() {
		bombImage = UUID.randomUUID();
		return this;
	}

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

package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.bomb;

import java.io.Serializable;
import java.util.UUID;

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
	private BombType bombType = BombType.Default;
	private UUID bombImage, player;
	private int range;
	private float delay;

	public BombType bombType() {
		return bombType;
	}

	public BombDesc bombType(BombType bombType) {
		if (bombType == null) {
			throw new NullPointerException("bombType");
		}
		this.bombType = bombType;
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

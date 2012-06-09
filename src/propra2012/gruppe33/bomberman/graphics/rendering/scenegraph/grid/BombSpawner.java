package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid;

import chn.Bomb;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.sprite.Sprite;

public class BombSpawner extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Sprite sprite;

	private boolean spawned = false;

	public BombSpawner(Sprite sprite) {
		this.sprite = sprite;
	}

	public void spawn() {
		spawned = true;
	}

	public Sprite sprite() {
		return sprite;
	}

	@Override
	protected void onUpdate(float tpf) {
		super.onUpdate(tpf);

		GraphicsEntity node = ((GraphicsEntity) parent().parent());
		if (spawned) {

			if (!GridRoutines.hasFieldExplosion(node)) {

				Bomb bomb = new Bomb(registrationKey());
				node.findSceneProcessor().adminSessionServer().combined()
						.applyChange(bomb);
			}

			spawned = false;
		}
	}
}

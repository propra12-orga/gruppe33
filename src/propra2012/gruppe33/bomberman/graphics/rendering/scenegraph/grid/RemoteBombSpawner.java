package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid;

import java.awt.event.KeyEvent;
import java.util.UUID;

import chn.BombChange;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;

public class RemoteBombSpawner extends GraphicsEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final UUID reg;
	private boolean v = true;

	public RemoteBombSpawner(UUID reg) {
		this.reg = reg;
	}

	@Override
	protected void onUpdate(float tpf) {
		super.onUpdate(tpf);

		Scene scene = findScene();

		if (scene.isPressed(KeyEvent.VK_SPACE)) {

			if (v) {
				if (scene.processor().hasSession()) {
					scene.processor().session().server()
							.applyChange(new BombChange(reg));
				}

				v = false;
			}
		} else {
			v = true;
		}

	}
}

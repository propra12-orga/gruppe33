package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid;

import java.util.Map;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridConstants.Input;
import chn.Bomb;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.input.InputChange;
import com.indyforge.twod.engine.graphics.sprite.Sprite;

public class BombSpawner extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Sprite sprite;

	private boolean spawn = false;

	public BombSpawner(Sprite sprite) {
		this.sprite = sprite;
	}

	public Sprite sprite() {
		return sprite;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onEvent(Entity source, Object event, Object... params) {
		super.onEvent(source, event, params);

		if (event instanceof String) {
			String str = (String) event;
			if (str.equals(InputChange.EVENT_NAME)) {
				Map<Input, Boolean> input = (Map<Input, Boolean>) params[0];

				if (input != null) {
					Boolean b = input.get(Input.PlaceBomb);
					spawn = b != null ? b : false;
				} else {
					spawn = false;
				}
			}
		}
	}

	@Override
	protected void onUpdate(float tpf) {
		super.onUpdate(tpf);

		GraphicsEntity node = ((GraphicsEntity) parent().parent());
		if (spawn && !GridRoutines.hasFieldExplosion(node)) {
			Bomb bomb = new Bomb(registrationKey());
			bomb.dest = node.registrationKey();
			node.findSceneProcessor().adminSessionServer().combined()
					.applyChange(bomb);
		}
	}
}

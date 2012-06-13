package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.bomb;

import java.util.Map;
import java.util.UUID;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridConstants.Input;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridRoutines;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.input.InputChange;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class BombSpawner extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean spawn = false;

	public int bombs = 20;

	@SuppressWarnings("unchecked")
	@Override
	protected void onEvent(Entity source, Object event, Object... params) {
		super.onEvent(source, event, params);
		if (event == InputChange.class) {
			Map<Input, Boolean> input = (Map<Input, Boolean>) params[0];
			spawn = input.get(Input.PlaceBomb);
		}
	}

	@Override
	protected void onUpdate(float tpf) {
		super.onUpdate(tpf);

		GraphicsEntity node = ((GraphicsEntity) parent().parent());
		if (spawn && GridRoutines.isFieldBombable(node) && bombs > 0) {
			UUID u = UUID.randomUUID();

			// Create bomb for the given field
			Bomb bombA = new Bomb();
			bombA.entities().add(node.registrationKey());
			bombA.bombreg = u;
			bombA.play = parent().registrationKey();

			Bomb bombB = new Bomb();
			bombB.entities().add(node.registrationKey());
			bombB.bombreg = u;
			bombs--;

			// Apply global change
			node.findSceneProcessor().adminSessionServer().broadcast()
					.applyChange(bombB);
			node.findSceneProcessor().adminSessionServer().local()
					.applyChange(bombA);
		}
	}
}

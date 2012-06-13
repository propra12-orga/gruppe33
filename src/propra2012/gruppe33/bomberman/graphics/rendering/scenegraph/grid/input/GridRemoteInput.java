package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.input;

import java.awt.event.KeyEvent;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridConstants.Input;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class GridRemoteInput extends GraphicsEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final UUID peerKey;
	private final Map<Input, Boolean> inputMap = new EnumMap<Input, Boolean>(
			Input.class);

	public GridRemoteInput(UUID peerKey) {
		this.peerKey = peerKey;
	}

	@Override
	protected void onUpdate(float tpf) {
		super.onUpdate(tpf);

		Scene scene = findScene();

		final Map<Input, Boolean> tmp = new EnumMap<Input, Boolean>(Input.class);
		tmp.put(Input.Up, scene.isPressed(KeyEvent.VK_UP));
		tmp.put(Input.Down, scene.isPressed(KeyEvent.VK_DOWN));
		tmp.put(Input.Left, scene.isPressed(KeyEvent.VK_LEFT));
		tmp.put(Input.Right, scene.isPressed(KeyEvent.VK_RIGHT));
		tmp.put(Input.PlaceBomb, scene.isPressed(KeyEvent.VK_SPACE));

		if (scene.processor().hasSession() && !tmp.equals(inputMap)) {
			inputMap.putAll(tmp);

			GridInputChange ch = new GridInputChange(Input.class);
			ch.entities().add(peerKey);
			ch.inputMap().putAll(tmp);
			scene.processor().session().server().applyChange(ch);
		}
	}
}

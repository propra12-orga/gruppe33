package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid;

import java.awt.event.KeyEvent;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import chn.GridChange;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f.Direction;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class GridRemoteController extends GraphicsEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final UUID peerKey;
	private final Map<Direction, Boolean> inputMap = new EnumMap<Direction, Boolean>(
			Direction.class);

	public GridRemoteController(UUID peerKey) {
		this.peerKey = peerKey;
	}

	@Override
	protected void onUpdate(float tpf) {
		super.onUpdate(tpf);

		Scene scene = findScene();

		final Map<Direction, Boolean> tmp = new EnumMap<Direction, Boolean>(
				Direction.class);
		tmp.put(Direction.North, scene.isPressed(KeyEvent.VK_UP));
		tmp.put(Direction.South, scene.isPressed(KeyEvent.VK_DOWN));
		tmp.put(Direction.West, scene.isPressed(KeyEvent.VK_LEFT));
		tmp.put(Direction.East, scene.isPressed(KeyEvent.VK_RIGHT));

		if (scene.processor().hasSession() && !tmp.equals(inputMap)) {
			inputMap.putAll(tmp);
			scene.processor().session().server()
					.applyChange(new GridChange(tmp, peerKey));
		}
	}
}
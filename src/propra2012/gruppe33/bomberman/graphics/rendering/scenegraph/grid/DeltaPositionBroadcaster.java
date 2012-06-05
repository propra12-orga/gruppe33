package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;

public class DeltaPositionBroadcaster extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final GraphicsEntity observed;
	private Vector2f oldPosition = null;

	public DeltaPositionBroadcaster(GraphicsEntity observed) {
		this.observed = observed;
	}

	private float timePassed = 0;

	@Override
	protected void onUpdate(float tpf) {
		super.onUpdate(tpf);

		Scene scene = observed.findScene();

		Vector2f pos = observed.position().add(
				((GraphicsEntity) observed.parent()).position());

		if (oldPosition == null) {
			oldPosition = new Vector2f(pos);
		}

		if (scene.processor().hasAdminSessionServer() && timePassed >= 0.04f
				&& !pos.equals(oldPosition)) {

			Vector2f delta = pos.sub(oldPosition);
			oldPosition.set(pos);

			PosChange pc = new PosChange(delta, observed.registryKey());
			scene.processor().adminSessionServer().broadcastChange(pc);

			timePassed = 0;
		} else {
			timePassed += tpf;
		}
	}
}

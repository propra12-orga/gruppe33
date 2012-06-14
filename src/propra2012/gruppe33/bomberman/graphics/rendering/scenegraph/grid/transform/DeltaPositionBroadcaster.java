package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.transform;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;

/**
 * Broadcasts the position changes (delta) of the parent which must be a
 * graphics entity.
 * 
 * @author Christopher Probst
 * 
 */
public final class DeltaPositionBroadcaster extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The observed entities
	private final Map<UUID, Vector2f> entities = new LinkedHashMap<UUID, Vector2f>();

	// The message
	private final ManyPositionsToMany message = new ManyPositionsToMany();

	// Tells the passed time
	private float timePassed = Float.MAX_VALUE;

	// The update delay
	private final float updateDelay;

	@Override
	protected void onUpdate(float tpf) {
		super.onUpdate(tpf);

		if (parent() instanceof Scene) {
			// Convert to scene
			Scene scene = (Scene) parent();

			// Server mode ?
			if (scene.processor().hasAdminSessionServer()
					&& timePassed >= updateDelay) {

				// Clear old
				message.entityMap().clear();

				// Go through all observed entities
				for (Entry<UUID, Vector2f> entity : entities.entrySet()) {

					// Lookup entity
					GraphicsEntity ptr = (GraphicsEntity) scene.registry().get(
							entity.getKey());

					// Calc the absolute position
					Vector2f absolutePosition = ptr.position().add(
							((GraphicsEntity) ptr.parent()).position());

					// If positions are not equal
					if (!absolutePosition.equals(entity.getValue())) {
						// Calc the absolute delta position
						Vector2f delta = absolutePosition
								.sub(entity.getValue());

						// Save!
						entity.getValue().set(absolutePosition);

						// Put delta vector
						message.entityMap().put(entity.getKey(), delta);
					}
				}

				// Send if not empty!
				if (!message.entityMap().isEmpty()) {

					// Broadcast the change
					scene.processor().adminSessionServer().broadcast()
							.applyChange(message);
				}

				// Reset time
				timePassed = 0;
			} else {
				timePassed += tpf;
			}
		}
	}

	/**
	 * Creats a new delta position broadcaster using the absolute update delay.
	 * 
	 * @param updateDelay
	 *            The update delay.
	 */
	public DeltaPositionBroadcaster(float updateDelay) {
		this.updateDelay = Math.abs(updateDelay);
		message.translate(true);
	}

	public Map<UUID, Vector2f> entities() {
		return entities;
	}
}

package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.transform;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import propra2012.gruppe33.bomberman.Game;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;
import com.indyforge.twod.engine.util.task.Pause;
import com.indyforge.twod.engine.util.task.Task;

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
			final Scene scene = (Scene) parent();

			// Server mode ?
			if (scene.processor().hasAdminSessionServer()
					&& timePassed >= updateDelay) {

				// Are there any entities left ??
				if (entities.size() <= 1) {

					scene.processor().taskQueue().tasks().add(new Pause(3f));
					scene.processor().taskQueue().tasks().add(new Task() {

						/**
						 * 
						 */
						private static final long serialVersionUID = 1L;

						/*
						 * (non-Javadoc)
						 * 
						 * @see
						 * com.indyforge.twod.engine.util.task.Task#update(float
						 * )
						 */
						@Override
						public boolean update(float tpf) {

							/*
							 * Initiate new server game!
							 */
							try {
								scene.typeProp(Game.class).serverGame(
										scene.processor());
							} catch (Exception e) {
								e.printStackTrace();
							}

							return true;
						}
					});

					// Detach this broadcaster!
					detach();

					return;
				}

				// Clear old
				message.entityMap().clear();

				// Used to remove uuids later...
				List<UUID> removed = null;

				// Go through all observed entities
				for (Entry<UUID, Vector2f> entity : entities.entrySet()) {

					// Lookup entity
					GraphicsEntity ptr = (GraphicsEntity) scene.registry().get(
							entity.getKey());

					// Add old uuid to remove list!
					if (ptr == null) {
						if (removed == null) {
							removed = new ArrayList<UUID>();
						}
						removed.add(entity.getKey());

						// Continue...
						continue;
					}

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

				// Delete old entities
				if (removed != null) {
					for (UUID entity : removed) {
						entities.remove(entity);
					}
				}

				// Send if not empty!
				if (!message.entityMap().isEmpty()) {

					// Broadcast the change
					scene.processor().adminSessionServer().broadcast()
							.queueChange(message, true);
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
		this.updateDelay = updateDelay;
	}

	/**
	 * @return the update delay.
	 */
	public float updateDelay() {
		return updateDelay;
	}

	public Map<UUID, Vector2f> entities() {
		return entities;
	}
}

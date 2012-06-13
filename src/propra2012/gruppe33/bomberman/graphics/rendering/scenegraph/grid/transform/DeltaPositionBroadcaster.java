package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.transform;

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

	// The update delay
	private final float updateDelay;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity#onAttached
	 * ()
	 */
	@Override
	protected void onAttached() {
		super.onAttached();

		// Parent changed...
		oldPosition = null;

		// Reset time...
		timePassed = 0;
	}

	private Vector2f oldPosition;
	private float timePassed;

	@Override
	protected void onUpdate(float tpf) {
		super.onUpdate(tpf);

		if (parent() instanceof GraphicsEntity) {

			// Convert to graphic entity
			GraphicsEntity graphicsParent = (GraphicsEntity) parent();

			// Find the scene
			Scene scene = graphicsParent.findScene();

			// Calc the absolute position
			Vector2f absolutePosition = graphicsParent.position().add(
					((GraphicsEntity) graphicsParent.parent()).position());

			// Check for null...
			if (oldPosition == null) {
				oldPosition = new Vector2f(absolutePosition);
			}

			// Server mode ?
			if (scene.processor().hasAdminSessionServer()
			// If update delay passed or if this is the first update!
					&& (timePassed >= updateDelay || oldPosition == null)
					// If positions are not equal
					&& !absolutePosition.equals(oldPosition)) {

				// Calc the absolute delta position
				Vector2f delta = absolutePosition.sub(oldPosition);

				// Save!
				oldPosition.set(absolutePosition);

				// Create a translation change
				OnePositionToMany translation = new OnePositionToMany();

				// Activate translation and setup correctly
				translation.translate(true).value(delta).entities()
						.add(graphicsParent.registrationKey());

				// Broadcast the change
				scene.processor().adminSessionServer().broadcast()
						.applyChange(translation);

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
	}
}

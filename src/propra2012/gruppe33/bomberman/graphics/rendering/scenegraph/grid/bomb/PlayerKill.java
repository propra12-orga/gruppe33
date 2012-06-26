package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.bomb;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridConstants;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.animation.RenderedAnimation;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.animation.RenderedAnimation.AnimationEvent;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity.Many;
import com.indyforge.twod.engine.util.iteration.TypeFilter;

/**
 * This class kills a player.
 * 
 * @author Christopher Probst
 * 
 */
public final class PlayerKill extends Many<GraphicsEntity> implements
		GridConstants {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity
	 * .Many
	 * #apply(com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity)
	 */
	@Override
	protected void apply(GraphicsEntity entity) {

		// Get the old parent
		Entity parent = entity.parent();

		// Detach the old entity
		entity.detach();

		// Get animation
		final RenderedAnimation ra = (RenderedAnimation) entity.findChild(
				new TypeFilter(RenderedAnimation.class), true, true);

		// Apply scale
		ra.scale().set(entity.scale());

		// Only attach the animation
		parent.attach(ra);

		// Use the free tag and the bomb index
		ra.tag(FREE_TAG).index(BOMB_INDEX);

		// Set animation
		ra.animationName(ra.animationName().replace("run_", "die_"));
		ra.animation().loop(false).paused(false).reset();

		// Attach an animation stopper!
		ra.attach(new Entity() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.indyforge.twod.engine.graphics.rendering.scenegraph
			 * .Entity #onEvent(com.indyforge.twod.engine.graphics.rendering
			 * .scenegraph.Entity, java.lang.Object, java.lang.Object[])
			 */
			@Override
			protected void onEvent(Entity source, Object event,
					Object... params) {
				super.onEvent(source, event, params);
				if (event == AnimationEvent.AnimationFinished) {
					ra.animation().useLastStep().paused(true);
				}
			}
		});
	}
}
package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.bomb;

import java.util.UUID;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.animation.RenderedAnimation;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.animation.RenderedAnimation.AnimationEvent;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.AbstractEntityChange;

public class Killer extends AbstractEntityChange<GraphicsEntity> {

	public Killer() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Killer(UUID registrationKey) {
		super(registrationKey);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void apply(GraphicsEntity entity) {
		for (Entity child : entity) {
			if (child instanceof RenderedAnimation) {

				final RenderedAnimation ra = (RenderedAnimation) child;
				ra.animationName(ra.animationName().replace("run_", "die_"));
				ra.animation().loop(false).paused(false).reset();

				ra.attach(new Entity() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					protected void onEvent(Entity source, Object event,
							Object... params) {
						super.onEvent(source, event, params);

						if (event instanceof AnimationEvent) {

							ra.animation().useLastStep();
							ra.animation().paused(true);
						}
					}
				});
			} else {
				child.detach();
			}
		}
	}
}
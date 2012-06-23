package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.bomb;

import java.awt.Point;
import java.util.List;
import java.util.UUID;

import propra2012.gruppe33.PreMilestoneApp;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridConstants;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridRoutines;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.RenderedImage;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Grid;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity.DetachEntityChange;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity.Many;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.scene.SceneChange;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.timeout.DetachOnTimeout;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.timeout.Timeout;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.transform.TransformMotor;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class Bomb extends Many<GraphicsEntity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public UUID bombreg, play;
	public int range = 3;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.twod.engine.graphics.rendering.scenegraph.network.
	 * Many
	 * #apply(com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity)
	 */
	@Override
	protected void apply(final GraphicsEntity entity) {

		// Get scene
		final Scene scene = entity.findScene();

		RenderedImage bombImage = new RenderedImage(
				scene.imageProp(GridConstants.BOMB_IMAGE)).centered(true);
		bombImage.registrationKey(bombreg);
		bombImage.scale().set(0.3f, 0.3f);
		bombImage.index(GridRoutines.BOMB_ORDER).tag(GridConstants.BOMB_TAG);
		bombImage.attach(new TransformMotor().scaleVelocity(new Vector2f(0.1f,
				0.1f)));
		entity.attach(bombImage);

		// Destroy after 3 seconds and spawn explosion!
		bombImage.attach(new Timeout(3f).attach(new DetachOnTimeout()));

		/*
		 * Create explosion maker!
		 */
		bombImage.attach(new Entity() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			protected void onParentDetached(Entity parent, Entity child) {
				super.onParentDetached(parent, child);

				if (scene.processor() == null) {
					return;
				}

				if (scene.processor().hasAdminSessionServer()) {
					scene.registry().get(play).prop("BS", BombSpawner.class)
							.addBomb();
				}

				// Lookup grid
				Grid grid = entity.parent().typeProp(Grid.class);

				List<Point> points = GridRoutines.bombRange(entity,
						GridRoutines.EXP_RANGE_FILTER, range);

				DetachEntityChange c = new DetachEntityChange();

				for (Point point : points) {
					// Create a new bomb
					GraphicsEntity bomb = GridRoutines.createExplosion(
							scene.spriteProp(GridConstants.EXP_SPRITE), 33);

					Entity node = entity.parent().childAt(grid.index(point));

					/*
					 * Server mode ?
					 */
					if (scene.processor().hasAdminSessionServer()) {

						// Destroy breakable objects on both sides!
						for (Entity ptr : node) {
							if (ptr.tagged(GridConstants.BREAKABLE_TAG)
									|| ptr.tagged(GridConstants.BOMB_TAG)) {
								c.entities().add(ptr.registrationKey());
							} else if (ptr.tagged(GridConstants.PLAYER_TAG)) {

								// Create new killer
								Killer k = new Killer();
								k.entities().add(ptr.registrationKey());

								// Submit the killer
								scene.processor().adminSessionServer()
										.composite().applyChange(k);

								List<UUID> players = (List<UUID>) scene
										.prop("players");

								players.remove(ptr.registrationKey());

								if (players.size() <= 1) {

									try {
										SceneProcessor proc = scene.processor();
										proc.adminSessionServer()
												.composite()
												.applyChange(
														new SceneChange(null));

										PreMilestoneApp.createServerGame(proc);

										return;
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
					}

					node.attach(bomb);
				}

				if (!c.entities().isEmpty()) {
					scene.processor().adminSessionServer().composite()
							.queueChange(c, true);
				}

				// Play a sound
				scene.soundManager().playSound(GridConstants.EXP_SOUND_NAME,
						true);
			}
		});
	}
}

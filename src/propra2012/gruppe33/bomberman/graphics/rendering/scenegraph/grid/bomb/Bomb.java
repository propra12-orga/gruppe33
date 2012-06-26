package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.bomb;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import propra2012.gruppe33.bomberman.Game;
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
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity.OneToMany;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.scene.SceneChange;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.sound.PlaySound;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.timeout.DetachOnTimeout;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.timeout.Timeout;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.transform.TransformMotor;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class Bomb extends OneToMany<GraphicsEntity, BombDesc> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity
	 * .OneToMany
	 * #apply(com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity,
	 * java.lang.Object)
	 */
	@Override
	protected void apply(final GraphicsEntity entity, BombDesc value) {

		// Get scene
		final Scene scene = entity.findScene();

		// Create a new bomb image
		RenderedImage bombImage = new RenderedImage(
				scene.imageProp(GridConstants.BOMB_IMAGE)).centered(true);

		// Use the same reg key
		bombImage.registrationKey(value.bombImage());

		// Scale a bit
		bombImage.scale().set(0.3f, 0.3f);

		// Set bomb order + tag
		bombImage.index(GridRoutines.BOMB_ORDER).tag(GridConstants.BOMB_TAG);

		// Scale velocity...
		bombImage.attach(new TransformMotor().scaleVelocity(new Vector2f(0.1f,
				0.1f)));

		// Attach to the node
		entity.attach(bombImage);

		/*
		 * The server handles the destruction of the bomb!
		 */
		if (scene.processor().hasAdminSessionServer()) {

			// Detach after the given delay!
			bombImage.attach(new Timeout(value.delay())
					.attach(new DetachOnTimeout()));

			/*
			 * Handle the destruction!
			 */
			bombImage.attach(new Entity() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity
				 * #
				 * onParentDetached(com.indyforge.twod.engine.graphics.rendering
				 * .scenegraph.Entity,
				 * com.indyforge.twod.engine.graphics.rendering
				 * .scenegraph.Entity)
				 */
				@Override
				protected void onParentDetached(Entity parent, Entity child) {
					super.onParentDetached(parent, child);

					if (scene.processor() == null) {
						return;
					}

					scene.registry().get(value().player())
							.prop("BS", BombSpawner.class).addBomb();

					// Lookup grid
					Grid grid = entity.parent().typeProp(Grid.class);

					List<Point> points = GridRoutines.bombRange(entity,
							GridRoutines.EXP_RANGE_FILTER, value().range());

					// Destroy the bomb image
					DetachEntityChange c = new DetachEntityChange();
					c.entities().add(value().bombImage());

					// The ids of the bombs will be stored here
					List<UUID> destroyedFields = new ArrayList<UUID>(points
							.size());

					for (Point point : points) {

						Entity node = entity.parent()
								.childAt(grid.index(point));

						// Add the reg key
						destroyedFields.add(node.registrationKey());

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

										// Initiate new server game
										scene.typeProp(Game.class).serverGame(
												proc);
										return;
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
					}

					scene.processor().adminSessionServer().composite()
							.queueChange(c, true);

					/*
					 * Spawn explosions!
					 */
					scene.processor().adminSessionServer().composite()
							.queueChange(new Explosion(destroyedFields), true);

					/*
					 * Play a single sound on each client.
					 */
					scene.processor()
							.adminSessionServer()
							.broadcast()
							.queueChange(
									new PlaySound(GridConstants.EXP_SOUND_NAME),
									true);
				}
			});
		}
	}

	public Bomb() {
	}

	public Bomb(List<UUID> registrationKeys) {
		super(registrationKeys);
	}
}

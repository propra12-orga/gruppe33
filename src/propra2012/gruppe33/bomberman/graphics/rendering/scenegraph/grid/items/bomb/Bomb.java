package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.items.bomb;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import propra2012.gruppe33.bomberman.GameConstants;
import propra2012.gruppe33.bomberman.GameRoutines;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.items.CollectableItem;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.items.ItemSpawner;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.items.spawners.SpawnDead;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.items.spawners.SpawnExplosion;

import com.indyforge.foxnet.rmi.pattern.change.AdminSessionServer;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.RenderedImage;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Grid;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity.DetachEntityChange;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity.OneToMany;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.sound.PlaySound;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.timeout.DetachOnTimeout;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.timeout.Timeout;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.transform.TransformMotor;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class Bomb extends OneToMany<GraphicsEntity, BombDesc> implements
		GameConstants {

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

		// Get the bomb
		final CollectableItem bomb = value.bomb();

		// Create a new bomb image
		RenderedImage bombImage = new RenderedImage(
				scene.imageProp(GameRoutines.bombToAsset(bomb))).centered(true);

		// Use the same reg key
		bombImage.registrationKey(value.bombImage());

		// Scale a bit
		bombImage.scale().set(BOMB_SCALE);

		// Set bomb order + tag
		bombImage.index(ITEM_INDEX).tag(BOMB_TAG);

		// Scale velocity...
		bombImage.attach(new TransformMotor()
				.scaleVelocity(BOMB_SCALE_VELOCITY));

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
				@SuppressWarnings("unchecked")
				@Override
				protected void onParentDetached(Entity parent, Entity child) {
					super.onParentDetached(parent, child);

					// Get the server instance
					AdminSessionServer<SceneProcessor> server = scene
							.processor().adminSessionServer();

					// Lookup player entity
					Entity playerEntity = scene.registry()
							.get(value().player());

					// Bombspawner valid ??
					if (playerEntity != null) {
						// Respawn bomb
						ItemSpawner sp = playerEntity
								.typeProp(ItemSpawner.class);

						// Increase the bomb count
						sp.addItems(bomb, 1);
					}

					// Lookup grid
					Grid grid = entity.parent().typeProp(Grid.class);

					// Calc the explosion range
					List<Point> points = GameRoutines.bombRange(entity,
							GameRoutines.EXP_RANGE_FILTER, value().range());

					// Used to detach entities
					DetachEntityChange entityDetacher = new DetachEntityChange();

					// Destroy the bomb image
					entityDetacher.entities().add(value().bombImage());

					// The ids of the bombs will be stored here
					List<UUID> destroyedFields = new ArrayList<UUID>(points
							.size());

					/*
					 * For all affected fields...
					 */
					for (Point point : points) {

						// Get the node
						Entity node = entity.parent()
								.childAt(grid.index(point));

						// Add the reg key of the node!
						destroyedFields.add(node.registrationKey());

						// Destroy breakable objects on both sides!
						for (Entity ptr : node) {

							// If BREAKABLE or BOMB ...
							if (ptr.tagged(BREAKABLE_TAG)) {

								// Simply detach from the scenegraph
								entityDetacher.entities().add(
										ptr.registrationKey());
							} else if (ptr.tagged(BOMB_TAG)) {

								/*
								 * Recursive bomb management!
								 */
								ptr.detach();

							} else if (ptr.tagged(PLAYER_TAG)) {

								// Create new player kill
								SpawnDead playerKill = new SpawnDead();

								// Use the active player
								playerKill.entities()
										.add(ptr.registrationKey());

								// Queue the kill
								server.composite()
										.queueChange(playerKill, true);

								// Get player list
								List<UUID> players = (List<UUID>) scene
										.prop(PLAYERS_KEY);

								// Remove from player list directly!
								players.remove(ptr.registrationKey());
							}
						}
					}

					/*
					 * Detach destroyed objects.
					 */
					server.composite().queueChange(entityDetacher, true);

					/*
					 * Spawn explosions!
					 */
					server.composite().queueChange(
							new SpawnExplosion(destroyedFields), true);

					/*
					 * Play a single sound on each client.
					 */
					server.broadcast().queueChange(
							new PlaySound(GameConstants.EXP_SOUND_NAME), true);
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

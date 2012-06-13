package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.bomb;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.UUID;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridConstants;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridRoutines;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.RenderedImage;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Grid;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.AbstractEntityChange;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity.DetachEntityChange;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.timeout.DetachOnTimeout;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.timeout.Timeout;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.transform.TransformMotor;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class Bomb extends AbstractEntityChange<GraphicsEntity> {

	public UUID bombreg, play;

	public Bomb() {
	}

	public Bomb(UUID registrationKey) {
		super(registrationKey);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeObject(bombreg);
		out.writeObject(play);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		super.readExternal(in);
		bombreg = (UUID) in.readObject();
		play = (UUID) in.readObject();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.twod.engine.graphics.rendering.scenegraph.network.
	 * AbstractEntityChange
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

			@Override
			protected void onParentDetached() {
				super.onParentDetached();

				if (scene.processor().hasAdminSessionServer()) {
					scene.registry().get(play).prop("BS", BombSpawner.class).bombs++;
				}

				// Lookup grid
				Grid grid = entity.parent().typeProp(Grid.class);

				List<Point> points = GridRoutines.bombRange(entity,
						GridRoutines.EXP_RANGE_FILTER, 3);

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

						// Destroy breackable objects on both sides!
						for (Entity child : node) {
							if (child.tagged(GridConstants.BREAKABLE_TAG)
									|| child.tagged(GridConstants.BOMB_TAG)) {
								c.list.add(child.registrationKey());
							} else if (child.tagged(GridConstants.PLAYER_TAG)) {

								scene.processor()
										.adminSessionServer()
										.composite()
										.applyChange(
												new Killer(child
														.registrationKey()));

								List<UUID> players = (List<UUID>) scene
										.prop("players");
								long[] ids = (long[]) scene.prop("sessions");

								players.remove(child.registrationKey());
							}
						}
					}

					node.attach(bomb);
				}

				if (!c.list.isEmpty()) {
					scene.processor().adminSessionServer().composite()
							.applyChange(c);
				}

				// Play a sound
				scene.soundManager().playSound(GridConstants.EXP_SOUND_NAME,
						true);
			}
		});
	}
}

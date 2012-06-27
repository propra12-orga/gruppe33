package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.bomb;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.EnumMap;
import java.util.Map;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridConstants.Input;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridRoutines;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.input.GridRemoteInput;

import com.indyforge.foxnet.rmi.pattern.change.Session;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.MathExt;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.input.InputChange;
import com.indyforge.twod.engine.util.iteration.TypeFilter;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class BombSpawner extends GraphicsEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The bombs
	private final Map<BombType, Integer> bombs = new EnumMap<BombType, Integer>(
			BombType.class);

	// The ranges of each bomb
	private final Map<BombType, Integer> ranges = new EnumMap<BombType, Integer>(
			BombType.class);

	// The delays
	private final Map<BombType, Float> delays = new EnumMap<BombType, Float>(
			BombType.class);

	// The player starts with the default bomb
	private BombType activeBombType = BombType.Default;

	// The spawn flag
	private boolean spawn = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity#onEvent
	 * (com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity,
	 * java.lang.Object, java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void onEvent(Entity source, Object event, Object... params) {
		super.onEvent(source, event, params);
		if (event == InputChange.class) {
			Map<Input, Boolean> input = (Map<Input, Boolean>) params[0];
			spawn = input.get(Input.PlaceBomb);

			// Check input
			boolean up = input.get(Input.BombUp), down = input
					.get(Input.BombDown);

			if (up != down) {
				// Get the ordinal of the enum and apply
				activeBombType = BombType.values()[MathExt.clamp(
						activeBombType.ordinal() + (up ? 1 : -1), 0,
						BombType.values().length - 1)];

				// Find the scene processor
				SceneProcessor proc = findSceneProcessor();

				// The sync bomb type
				SyncBombType syncBombType = new SyncBombType();
				syncBombType.entityMap().put(registrationKey(), activeBombType);

				// Get session
				Session<SceneProcessor> session = proc.adminSessionServer()
						.session(Long.parseLong(parent().name()));

				// If session exists...
				if (session != null) {
					session.client().applyChange(syncBombType);
				}
			}
		}
	}

	public BombSpawner() {
		// Set bomb counters
		bombs.put(BombType.Default, 1);
		bombs.put(BombType.Nuke, 0);
		bombs.put(BombType.Time, 0);

		// Set bomb ranges
		ranges.put(BombType.Default, 1);
		ranges.put(BombType.Nuke, 3);
		ranges.put(BombType.Time, 1);

		// Set bomb delays
		delays.put(BombType.Default, 3f);
		delays.put(BombType.Nuke, 3f);
		delays.put(BombType.Time, 1.5f);
	}

	public BombType activeBombType() {
		return activeBombType;
	}

	public BombSpawner activeBombType(BombType activeBombType) {
		if (activeBombType == null) {
			throw new NullPointerException("activeBombType");
		}
		this.activeBombType = activeBombType;
		return this;
	}

	public Map<BombType, Integer> bombs() {
		return bombs;
	}

	public Map<BombType, Integer> ranges() {
		return ranges;
	}

	public Map<BombType, Float> delays() {
		return delays;
	}

	public Integer activeBombs() {
		return bombs.get(activeBombType);
	}

	public Integer activeRange() {
		return ranges.get(activeBombType);
	}

	public Float activeDelay() {
		return delays.get(activeBombType);
	}

	public BombSpawner removeBombs(BombType bombType, int count) {
		return addBombs(bombType, -count);
	}

	public BombSpawner addBombs(final BombType bombType, final int count) {
		int value = bombs.get(bombType) + count;
		bombs.put(bombType, value < 0 ? 0 : value);

		// Find the scene processor
		SceneProcessor proc = findSceneProcessor();

		/*
		 * Server running ??
		 */
		if (proc.hasAdminSessionServer()) {

			// Create bomb count sync
			SyncBombCount syncBombCount = new SyncBombCount();
			syncBombCount.bombType(bombType).count(bombs.get(bombType))
					.entities().add(registrationKey());

			// Get session
			Session<SceneProcessor> session = proc.adminSessionServer()
					.session(Long.parseLong(parent().name()));

			// If session exists...
			if (session != null) {
				session.client().applyChange(syncBombCount);
			}
		}
		return this;
	}

	public boolean isSpawn() {
		return spawn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity
	 * #onRender(java.awt.Graphics2D, java.awt.Graphics2D)
	 */
	@Override
	protected void onRender(Graphics2D original, Graphics2D transformed) {
		super.onRender(original, transformed);

		if (findSibling(new TypeFilter(GridRemoteInput.class), false) != null) {
			original.setFont(new Font("Arial", Font.BOLD, 36));
			original.setColor(Color.black);
			original.drawString("Bomb type: " + activeBombType, 0, 20);
			original.drawString("Bombs: " + activeBombs(), 0, 60);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity#onUpdate
	 * (float)
	 */
	@Override
	protected void onUpdate(float tpf) {
		super.onUpdate(tpf);

		// Get parent
		GraphicsEntity node = ((GraphicsEntity) parent().parent());

		/*
		 * If the field is bombable, there are remaining bombs and the spawn
		 * flag is set.
		 */
		if (spawn && GridRoutines.isFieldBombable(node) && activeBombs() > 0) {

			// Create bomb for the given field
			Bomb bomb = new Bomb();
			bomb.entities().add(node.registrationKey());
			bomb.value(new BombDesc().delay(activeDelay()).range(activeRange())
					.bombType(activeBombType).randomBombImage()
					.player(parent().registrationKey()));

			// Remove a bomb
			removeBombs(activeBombType, 1);

			// Apply global change
			node.findSceneProcessor().adminSessionServer().composite()
					.queueChange(bomb, true);

			// Deactivate spawning
			spawn = false;
		}
	}
}

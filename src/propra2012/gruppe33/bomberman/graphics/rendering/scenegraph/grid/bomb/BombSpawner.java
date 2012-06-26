package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.bomb;

import java.util.Map;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridConstants.Input;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridRoutines;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.input.InputChange;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class BombSpawner extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The spawn flag
	private boolean spawn = false;

	// The number of remaining bombs
	private int bombs = 3;

	// The range of the bombs
	private int range = 4;

	// The delay of each bomb
	private float delay = 3;

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
		}
	}

	public int bombs() {
		return bombs;
	}

	public BombSpawner removeBombs(int count) {
		return addBombs(-count);
	}

	public BombSpawner addBombs(int count) {
		bombs += count;
		if (bombs < 0) {
			bombs = 0;
		}
		return this;
	}

	public BombSpawner addBomb() {
		return addBombs(1);
	}

	public BombSpawner removeBomb() {
		return removeBombs(1);
	}

	public BombSpawner bombs(int bombs) {
		if (bombs < 0) {
			throw new IllegalArgumentException("bombs must be >= 0");
		}
		this.bombs = bombs;
		return this;
	}

	public boolean isSpawn() {
		return spawn;
	}

	public BombSpawner spawn(boolean spawn) {
		this.spawn = spawn;
		return this;
	}

	public int range() {
		return range;
	}

	public BombSpawner range(int range) {
		if (range < 0) {
			throw new IllegalArgumentException("Range must be >= 0");
		}
		this.range = range;
		return this;
	}

	public float delay() {
		return delay;
	}

	public BombSpawner delay(float delay) {
		if (delay < 0) {
			throw new IllegalArgumentException("Delay must be >= 0");
		}
		this.delay = delay;
		return this;
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
		if (spawn && GridRoutines.isFieldBombable(node) && bombs > 0) {

			// Create bomb for the given field
			Bomb bomb = new Bomb();
			bomb.entities().add(node.registrationKey());
			bomb.value(new BombDesc().delay(delay).range(range)
					.randomBombImage().player(parent().registrationKey()));

			// Remove a bomb
			removeBomb();

			// Apply global change
			node.findSceneProcessor().adminSessionServer().composite()
					.queueChange(bomb, true);

			// Deactivate spawning
			spawn = false;
		}
	}
}

package chn;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.UUID;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.BombSpawner;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridRoutines;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.AbstractEntityChange;

public class Bomb extends AbstractEntityChange<BombSpawner> {

	public Bomb() {
		super();
	}

	public Bomb(UUID registrationKey) {
		super(registrationKey);
	}

	public UUID dest;

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeObject(dest);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		super.readExternal(in);
		dest = (UUID) in.readObject();
	}

	@Override
	protected void apply(BombSpawner entity) {

		// Get scene
		Scene scene = ((GraphicsEntity) entity.parent()).findScene();
		GraphicsEntity node = ((GraphicsEntity) scene.registry().get(dest));

		GraphicsEntity bomb = GridRoutines.createExplosion(entity.sprite(), 33);

		scene.soundManager().playSound("exp", true);

		// Attach the bomb
		node.attach(bomb);

	}
}

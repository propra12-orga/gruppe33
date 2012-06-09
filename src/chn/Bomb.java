package chn;

import java.util.UUID;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.BombSpawner;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridRoutines;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Grid;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.AbstractEntityChange;

public class Bomb extends AbstractEntityChange<BombSpawner> {

	public Bomb() {
		super();
	}

	public Bomb(UUID registrationKey) {
		super(registrationKey);
	}

	@Override
	protected void apply(BombSpawner entity) {

		// Get scene
		Scene scene = ((GraphicsEntity) entity.parent()).findScene();

		GraphicsEntity node = ((GraphicsEntity) entity.parent().parent());
		GraphicsEntity grid = (GraphicsEntity) node.parent();

		GraphicsEntity bomb = GridRoutines.createExplosion(entity.sprite(), 33);

		scene.soundManager().playSound("exp", true);

		// Attach the bomb
		grid.childAt(grid.typeProp(Grid.class).index(node.position())).attach(
				bomb);

	}
}

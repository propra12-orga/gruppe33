package chn;

import java.util.UUID;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.BombSpawner;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.AbstractEntityChange;

public class BombChange extends AbstractEntityChange<BombSpawner> {

	public BombChange() {
	}

	public BombChange(UUID registrationKey) {
		super(registrationKey);
	}

	@Override
	protected void apply(BombSpawner entity) {
		entity.spawn();
	}
}

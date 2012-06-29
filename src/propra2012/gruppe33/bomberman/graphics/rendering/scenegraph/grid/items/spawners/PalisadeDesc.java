package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.items.spawners;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.items.ItemDesc;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f.Direction;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class PalisadeDesc extends ItemDesc {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Direction direction;

	public Direction direction() {
		return direction;
	}

	public PalisadeDesc direction(Direction direction) {
		this.direction = direction;
		return this;
	}
}

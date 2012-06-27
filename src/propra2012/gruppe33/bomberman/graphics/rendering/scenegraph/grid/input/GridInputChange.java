package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.input;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridConstants.Input;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.input.InputChange;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class GridInputChange extends InputChange<Input, GraphicsEntity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GridInputChange(Class<Input> enumType) {
		super(enumType);
	}
}

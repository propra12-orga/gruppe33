package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.input;

import java.util.Map;
import java.util.UUID;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridConstants.Input;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.input.InputChange;

public class GridInputChange extends InputChange<Input, GraphicsEntity> {

	public GridInputChange() {
		super();
	}

	public GridInputChange(Map<Input, Boolean> inputMap) {
		super(inputMap);
	}

	public GridInputChange(UUID registrationKey, Map<Input, Boolean> inputMap) {
		super(registrationKey, inputMap);
	}

	public GridInputChange(UUID registrationKey) {
		super(registrationKey);
	}
}

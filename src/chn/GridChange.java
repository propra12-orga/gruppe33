package chn;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridController;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f.Direction;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.AbstractEntityChange;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class GridChange extends AbstractEntityChange<GridController> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Map<Direction, Boolean> inputMap;

	public GridChange() {
	}

	public GridChange(Map<Direction, Boolean> inputMap, UUID registrationKey) {
		super(registrationKey);
		this.inputMap = inputMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.twod.engine.graphics.rendering.scenegraph.network.
	 * AbstractEntityChange#readExternal(java.io.ObjectInput)
	 */
	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		super.readExternal(in);

		if (in.readBoolean()) {
			inputMap = new EnumMap<Direction, Boolean>(Direction.class);
			inputMap.put(Direction.North, in.readBoolean());
			inputMap.put(Direction.South, in.readBoolean());
			inputMap.put(Direction.West, in.readBoolean());
			inputMap.put(Direction.East, in.readBoolean());
		} else {
			inputMap = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.twod.engine.graphics.rendering.scenegraph.network.
	 * AbstractEntityChange#writeExternal(java.io.ObjectOutput)
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);

		out.writeBoolean(inputMap != null);
		if (inputMap != null) {
			out.writeBoolean(inputMap.get(Direction.North));
			out.writeBoolean(inputMap.get(Direction.South));
			out.writeBoolean(inputMap.get(Direction.West));
			out.writeBoolean(inputMap.get(Direction.East));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.twod.engine.graphics.rendering.scenegraph.network.
	 * AbstractEntityChange
	 * #apply(com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity)
	 */
	@Override
	protected void apply(GridController entity) {
		entity.inputMap().putAll(inputMap);
	}
}

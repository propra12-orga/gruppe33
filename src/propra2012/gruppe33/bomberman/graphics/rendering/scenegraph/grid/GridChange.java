package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import com.foxnet.rmi.pattern.change.Change;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f.Direction;

public final class GridChange implements Change<SceneProcessor>, Externalizable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Map<Direction, Boolean> inputMap;
	private UUID key;

	public GridChange() {
	}

	public GridChange(Map<Direction, Boolean> inputMap, UUID key) {
		this.inputMap = inputMap;
		this.key = key;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {

		key = new UUID(in.readLong(), in.readLong());

		inputMap = new EnumMap<Direction, Boolean>(Direction.class);
		inputMap.put(Direction.North, in.readBoolean());
		inputMap.put(Direction.South, in.readBoolean());
		inputMap.put(Direction.West, in.readBoolean());
		inputMap.put(Direction.East, in.readBoolean());
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeLong(key.getMostSignificantBits());
		out.writeLong(key.getLeastSignificantBits());
		out.writeBoolean(inputMap.get(Direction.North));
		out.writeBoolean(inputMap.get(Direction.South));
		out.writeBoolean(inputMap.get(Direction.West));
		out.writeBoolean(inputMap.get(Direction.East));
	}

	@Override
	public void apply(SceneProcessor ctx) {
		Entity remoteEntity = ctx.root().registry().get(key);

		GridController gc = (GridController) remoteEntity;
		gc.inputMap().putAll(inputMap);
	}
}

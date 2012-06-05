package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.UUID;

import com.foxnet.rmi.pattern.change.Change;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;

public final class PosChange implements Change<SceneProcessor>, Externalizable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Vector2f pos;
	private UUID key;

	public PosChange() {
	}

	public PosChange(Vector2f pos, UUID key) {
		this.pos = pos;
		this.key = key;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {

		key = new UUID(in.readLong(), in.readLong());
		pos = new Vector2f(in.readFloat(), in.readFloat());
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeLong(key.getMostSignificantBits());
		out.writeLong(key.getLeastSignificantBits());
		out.writeFloat(pos.x);
		out.writeFloat(pos.y);
	}

	@Override
	public void apply(SceneProcessor ctx) {
		Entity remoteEntity = ctx.root().registry().get(key);

		GraphicsEntity ge = (GraphicsEntity) remoteEntity;
		ge.position().addLocal(pos);
	}
}

package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.transform;

import java.util.Map;
import java.util.UUID;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridRoutines;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.transform.ManyVectorsToMany;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class ManyPositionsToMany extends
		ManyVectorsToMany<GraphicsEntity> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
	 * If true the given vector will be added to the position, otherwise the
	 * position will be set.
	 */
	private boolean translate;

	@Override
	protected void apply(GraphicsEntity entity, Vector2f value) {
		if (translate) {
			entity.position().addLocal(value);
		} else {
			entity.position().set(value);
		}
		GridRoutines.rearrangeGridNode((GraphicsEntity) entity.parent());
	}

	public ManyPositionsToMany() {
	}

	public ManyPositionsToMany(Map<UUID, Vector2f> entityMap) {
		super(entityMap);
	}

	public boolean isTranslate() {
		return translate;
	}

	public ManyPositionsToMany translate(boolean translate) {
		this.translate = translate;
		return this;
	}
}

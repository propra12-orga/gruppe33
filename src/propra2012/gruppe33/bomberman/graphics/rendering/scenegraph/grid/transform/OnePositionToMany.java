package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.transform;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridRoutines;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.transform.OneVectorToMany;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class OnePositionToMany extends OneVectorToMany<GraphicsEntity> {

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

	public boolean isTranslate() {
		return translate;
	}

	public OnePositionToMany translate(boolean translate) {
		this.translate = translate;
		return this;
	}
}

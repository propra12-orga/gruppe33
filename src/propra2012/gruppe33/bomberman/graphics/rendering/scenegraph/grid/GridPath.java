package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid;

import java.util.LinkedList;
import java.util.Queue;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f.Direction;

public class GridPath extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * Here we store all directions. This class will animate the parent (only if
	 * graphics entity) using the given directions.
	 */
	private final Queue<Direction> directions = new LinkedList<Direction>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity#onUpdate
	 * (float)
	 */
	@Override
	protected void onUpdate(float tpf) {
		super.onUpdate(tpf);

		// Get and convert parent
		if (parent() instanceof GraphicsEntity) {
			// Convert
			GraphicsEntity graphicsParent = (GraphicsEntity) parent();

			
			
		}
	}
}

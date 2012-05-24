package propra2012.gruppe33.engine.graphics.rendering.scenegraph;

import java.awt.Graphics2D;


/**
 * 
 * @author Christopher Probst
 * 
 */
public interface Layout {

	/**
	 * Do the layout of the given child entity.
	 * 
	 * @param child
	 *            The child.
	 * @param processedChildren
	 *            The number of children which have been already processed.
	 * @param transformed
	 *            The graphics context you have to layout.
	 * @return the transformed layout.
	 */
	Graphics2D layout(GraphicsEntity child, int processedChildren,
			Graphics2D transformed);
}

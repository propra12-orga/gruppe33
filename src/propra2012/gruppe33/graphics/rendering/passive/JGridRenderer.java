package propra2012.gruppe33.graphics.rendering.passive;

import propra2012.gruppe33.graphics.rendering.scenegraph.grid.Grid;

public class JGridRenderer extends JSceneRenderer<Grid> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new grid renderer using the given grid.
	 * 
	 * @param root
	 *            The root grid which you want to render.
	 */
	public JGridRenderer(Grid root) {
		super(root);
	}
}
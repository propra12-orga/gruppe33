package propra2012.gruppe33.engine.graphics.rendering.scenegraph.filters;

/**
 * 
 * @author Christopher Probst
 * 
 * @param <E>
 *            The element type.
 */
public interface IterationFilter<E> {

	/**
	 * 
	 * @param element
	 * @return
	 */
	boolean accept(E element);
}

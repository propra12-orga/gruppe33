package propra2012.gruppe33.engine.util;

/**
 * A filter interface to filter elements.
 * 
 * @author Christopher Probst
 * 
 * @param <E>
 *            The element type.
 */
public interface Filter<E> {

	/**
	 * This method checks the given element to be valid.
	 * 
	 * @param element
	 *            The element this filter should. Can be null.
	 * @return true if the given element is valid, otherwise false.
	 */
	boolean accept(E element);
}

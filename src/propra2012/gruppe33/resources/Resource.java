package propra2012.gruppe33.resources;

import java.io.Serializable;

/**
 * This interface represents a serializable resource.
 * 
 * @author Christopher Probst
 * 
 * @param <T>
 *            The type of the resource.
 */
public interface Resource<T> extends Serializable {

	/**
	 * Returns the resource and never null. The method can be called very
	 * frequently, so implementations should use caching when dealing with
	 * native resources.
	 * 
	 * @return the resource.
	 */
	T get();
}

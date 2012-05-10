package propra2012.gruppe33.graphics.rendering.util;

import java.io.Serializable;

/**
 * This interface represents a serializable resource. There is no default
 * implementation. For instance default image serialization would be mostly
 * senseless in the most cases. But even if it makes sense you are able to do
 * this with this interface.
 * 
 * @author Christopher Probst
 * 
 * @param <T>
 *            The type of the resource.
 */
public interface Resource<T> extends Serializable {

	/**
	 * Returns the resource. The method can be called very frequently, so
	 * implementations should use caching when dealing with native resources.
	 * 
	 * @return the resource.
	 */
	T get();
}

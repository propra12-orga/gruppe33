package propra2012.gruppe33.graphics.rendering.util;

import java.io.Serializable;

public interface Resource<T> extends Serializable {

	T get() throws Exception;
}

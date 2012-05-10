package propra2012.gruppe33.graphics.rendering.scenegraph;

import java.io.Serializable;

/**
 * A simple filter interface. Used to find entities.
 * 
 * @author Christopher Probst
 * @see Entity#findChildren(EntityFilter, boolean)
 * @see Entity#findChildrenRecursively(EntityFilter, boolean)
 * @see Entity#findParents(EntityFilter, boolean)
 */
public interface EntityFilter extends Serializable {

	/**
	 * <code>CONTINUE</code> means the search should try to find more entities.
	 */
	int CONTINUE = 1 << 0;

	/**
	 * <code>VALID</code> means the active entity is valid and should be added
	 * to the results.
	 */
	int VALID = 1 << 1;

	/**
	 * Checks a given entity. Please note that you have to return a flag instead
	 * of a simple var.
	 * 
	 * For instance: <code>return CONTINUE | VALID;</code> means that you want
	 * to add the given entity to the results and to continue the search process
	 * to find more entities.
	 * 
	 * 
	 * @param entity
	 *            The entity this method has to check.
	 * @return an int flag. For instance <code>0</code> means the entity does
	 *         not match and the search should not continue. Please look at
	 *         {@link EntityFilter#CONTINUE} and {@link EntityFilter#VALID}.
	 */
	int accept(Entity entity);
}

package propra2012.gruppe33.graphics.rendering.scenegraph;

import java.awt.Graphics2D;

/**
 * This is basically an interceptor interface so you can modify or set the
 * behaviour of an entity dynamically.
 * 
 * @author Christopher Probst
 * @see Entity
 */
public interface EntityController {

	/**
	 * OVERRIDE FOR CUSTOM UPDATE BEHAVIOUR:
	 * 
	 * This method gets called every frame to update the state of this entity.
	 * 
	 * @param entity
	 *            The entity.
	 * @param tpf
	 *            The time per frame in seconds. Used to interpolate
	 *            time-sensitive data.
	 */
	void doUpdate(Entity entity, float tpf);

	/**
	 * OVERRIDE FOR CUSTOM RENDER BEHAVIOUR:
	 * 
	 * This method gets called every frame to render this entity if the visible
	 * flag is set to true.
	 * 
	 * @param entity
	 *            The entity.
	 * @param original
	 *            A graphics context you can render to. There is no
	 *            transformation applied yet.
	 * @param transformed
	 *            A graphics context you can render to. The entity has already
	 *            applied transformation, so the context origin is your
	 *            position.
	 * 
	 */
	void doRender(Entity entity, Graphics2D original, Graphics2D transformed);
}

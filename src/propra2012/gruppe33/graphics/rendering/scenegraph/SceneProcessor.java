package propra2012.gruppe33.graphics.rendering.scenegraph;

import java.util.concurrent.BlockingQueue;

/**
 * Processes a given scene.
 * 
 * @author Christopher Probst
 * @see Scene
 */
public interface SceneProcessor<S extends Scene> {

	/**
	 * @return the scene root.
	 */
	S getRoot();

	/**
	 * @return the task queue.
	 */
	public BlockingQueue<Runnable> getTasks();

	/**
	 * Resets the processor time.
	 */
	void resetTime();

	/**
	 * Process the whole scene.
	 * 
	 * @param maxFPS
	 *            The maximum frames per second. A value < 1 means no max.
	 */
	void process(int maxFPS);
}

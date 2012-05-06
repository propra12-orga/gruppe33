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
	 * Sets the root of this processor and resets the time.
	 * 
	 * @param root
	 *            The root you want to set.
	 */
	void setRoot(S root);

	/**
	 * The returned queue is thread-safe.
	 * 
	 * @return the task queue.
	 */
	public BlockingQueue<Runnable> getTasks();

	/**
	 * Resets the processor time.
	 */
	void resetTime();

	/**
	 * @return the shutdown-requested flag.
	 */
	boolean isShutdownRequested();

	/**
	 * Sets the shutdown-requested flag.
	 * 
	 * @param shutdownRequested
	 *            The boolean value.
	 */
	void setShutdownRequest(boolean shutdownRequested);

	/**
	 * Process the whole scene.
	 * 
	 * @param maxFPS
	 *            The maximum frames per second. A value < 1 means no max.
	 * @throws Exception
	 *             If some kind of error occurs.
	 */
	void process(int maxFPS) throws Exception;
}

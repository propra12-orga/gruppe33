package com.indyforge.foxnet.rmi.pattern.change;

/**
 * 
 * @author Christopher Probst
 * 
 * @param <T>
 */
public interface ChangeableQueue<T> extends Changeable<T> {

	/**
	 * Applies the queued changes.
	 */
	void applyQueuedChanges();

	/**
	 * Merges all queued changes to a single one.
	 */
	void mergeQueuedChanges();

	/**
	 * Separates the queued changes (Only works after
	 * {@link Changeable#mergeQueuedChanges()}).
	 */
	void separateQueuedChanges();

	/**
	 * Queues the given change in a thread-safe way.
	 * 
	 * @param change
	 *            The {@link Change} you want to queue.
	 * @param merge
	 *            If true the change will be merged with all existing changes.
	 */
	void queueChange(Change<T> change, boolean merge);

	/**
	 * Removes all queued changes.
	 */
	void removeQueuedChanges();
}

package com.indyforge.foxnet.rmi.pattern.change.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import com.indyforge.foxnet.rmi.Invocation;
import com.indyforge.foxnet.rmi.Invoker;
import com.indyforge.foxnet.rmi.RemoteInterfaces;
import com.indyforge.foxnet.rmi.pattern.change.Change;
import com.indyforge.foxnet.rmi.pattern.change.Changeable;
import com.indyforge.foxnet.rmi.pattern.change.ChangeableQueue;

/**
 * 
 * @author Christopher Probst
 * 
 * @param <T>
 */
@RemoteInterfaces(Changeable.class)
public final class DefaultChangeableQueue<T> implements ChangeableQueue<T> {

	/*
	 * Used to queue changes.
	 */
	private final Queue<Change<T>> changes = new LinkedList<Change<T>>();

	/*
	 * The changeable peer.
	 */
	private final Changeable<T> peer;

	public DefaultChangeableQueue(Changeable<T> peer) {
		if (peer == null) {
			throw new NullPointerException("peer");
		}
		this.peer = peer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.foxnet.rmi.pattern.change.Changeable#applyChange(com.indyforge
	 * .foxnet.rmi.pattern.change.Change)
	 */
	@Override
	public void applyChange(Change<T> change) {
		peer.applyChange(change);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.foxnet.rmi.pattern.change.ChangeableQueue#applyChangeLater
	 * (com.indyforge.foxnet.rmi.pattern.change.Change, java.util.Collection)
	 */
	@Override
	public void applyChangeLater(Change<T> change,
			Collection<Invocation> invocations) {

		// Generate an invocation
		Invocation invocation = Invoker.of(peer).invoke("applyChange", change);

		// Add... ?
		if (invocations != null) {
			invocations.add(invocation);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.foxnet.rmi.pattern.change.ChangeableQueue#
	 * applyQueuedChangesLater(java.util.Collection)
	 */
	@Override
	public void applyQueuedChangesLater(Collection<Invocation> invocations) {
		Change<T> change;
		while ((change = changes.poll()) != null) {
			applyChangeLater(change, invocations);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.foxnet.rmi.pattern.change.ChangeableQueue#applyQueuedChanges
	 * ()
	 */
	@Override
	public synchronized void applyQueuedChanges() {
		Change<T> change;
		while ((change = changes.poll()) != null) {
			applyChange(change);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.foxnet.rmi.pattern.change.ChangeableQueue#mergeQueuedChanges
	 * ()
	 */
	@Override
	public synchronized void mergeQueuedChanges() {
		// Check the size
		if (changes.size() > 1) {
			// Create a new composite change
			CompositeChange<T> compositeChange = new CompositeChange<T>(changes);

			// Remove all changes
			changes.clear();

			// Offer the composite change
			changes.offer(compositeChange);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.foxnet.rmi.pattern.change.ChangeableQueue#separateQueuedChanges
	 * ()
	 */
	@Override
	public synchronized void separateQueuedChanges() {
		// Check the head
		if (changes.size() == 1 && changes.peek() instanceof CompositeChange) {
			// Remove composite change and add each change
			changes.addAll(((CompositeChange<T>) changes.poll()).changes());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.foxnet.rmi.pattern.change.ChangeableQueue#queueChange(com
	 * .indyforge.foxnet.rmi.pattern.change.Change, boolean)
	 */
	@Override
	public synchronized void queueChange(Change<T> change, boolean merge) {
		// Queue the change
		changes.offer(change);

		// Merge ?
		if (merge) {
			mergeQueuedChanges();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.foxnet.rmi.pattern.change.ChangeableQueue#removeQueuedChanges
	 * ()
	 */
	@Override
	public synchronized void removeQueuedChanges() {
		// Clear all changes
		changes.clear();
	}
}

package com.indyforge.foxnet.rmi.pattern.change.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.indyforge.foxnet.rmi.Invocation;
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
public final class CompositeChangeableQueue<T> implements ChangeableQueue<T> {

	/*
	 * Here we store all changeable queue of this composite changeable queue.
	 */
	private final List<ChangeableQueue<T>> changeableQueues = new LinkedList<ChangeableQueue<T>>();

	public CompositeChangeableQueue(ChangeableQueue<T>... changeableQueues) {
		for (ChangeableQueue<T> changeableQueue : changeableQueues) {
			if (changeableQueues != null) {
				this.changeableQueues.add(changeableQueue);
			}
		}
	}

	public CompositeChangeableQueue(
			Iterator<? extends ChangeableQueue<T>> changeableQueues) {
		if (changeableQueues != null) {
			while (changeableQueues.hasNext()) {
				ChangeableQueue<T> changeableQueue = changeableQueues.next();
				if (changeableQueue != null) {
					this.changeableQueues.add(changeableQueue);
				}
			}
		}
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
		for (ChangeableQueue<T> changeableQueue : changeableQueues) {
			changeableQueue.applyChange(change);
		}
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
		for (ChangeableQueue<T> changeableQueue : changeableQueues) {
			changeableQueue.applyChangeLater(change, invocations);
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
		for (ChangeableQueue<T> changeableQueue : changeableQueues) {
			changeableQueue.applyQueuedChangesLater(invocations);
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
	public void applyQueuedChanges() {
		for (ChangeableQueue<T> changeableQueue : changeableQueues) {
			changeableQueue.applyQueuedChanges();
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
	public void mergeQueuedChanges() {
		for (ChangeableQueue<T> changeableQueue : changeableQueues) {
			changeableQueue.mergeQueuedChanges();
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
	public void queueChange(Change<T> change, boolean merge) {
		for (ChangeableQueue<T> changeableQueue : changeableQueues) {
			changeableQueue.queueChange(change, merge);
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
	public void removeQueuedChanges() {
		for (ChangeableQueue<T> changeableQueue : changeableQueues) {
			changeableQueue.removeQueuedChanges();
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
	public void separateQueuedChanges() {
		for (ChangeableQueue<T> changeableQueue : changeableQueues) {
			changeableQueue.separateQueuedChanges();
		}
	}
}

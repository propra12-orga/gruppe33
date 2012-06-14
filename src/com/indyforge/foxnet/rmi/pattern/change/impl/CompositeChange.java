package com.indyforge.foxnet.rmi.pattern.change.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.indyforge.foxnet.rmi.pattern.change.Change;

/**
 * 
 * @author Christopher Probst
 * 
 * @param <T>
 */
public final class CompositeChange<T> implements Change<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * Used to store all changes.
	 */
	private final List<Change<T>> changes = new ArrayList<Change<T>>(),
			readOnlyChanges = Collections.unmodifiableList(changes);

	/**
	 * Collects all changes.
	 * 
	 * @param change
	 *            The change.
	 */
	private void collect(Change<T> change) {
		if (change != null) {
			if (change instanceof CompositeChange) {
				// Go through all changes
				for (Change<T> ptr : ((CompositeChange<T>) change).changes()) {
					collect(ptr);
				}
			} else {
				this.changes.add(change);
			}
		}
	}

	public CompositeChange(Collection<? extends Change<T>> changes) {
		if (changes != null) {
			for (Change<T> change : changes) {
				collect(change);
			}
		}
	}

	/**
	 * @return an unmodifiable list of all changes.
	 */
	public List<Change<T>> changes() {
		return readOnlyChanges;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.foxnet.rmi.pattern.change.Change#apply(java.lang.Object)
	 */
	@Override
	public void apply(T ctx) {
		// Simply apply all stored changes
		for (Change<T> change : changes) {
			if (change != null) {
				change.apply(ctx);
			}
		}
	}
}

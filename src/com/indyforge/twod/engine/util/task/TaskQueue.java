package com.indyforge.twod.engine.util.task;

import java.util.Deque;
import java.util.LinkedList;

/**
 * A simple task queue.
 * 
 * @author Christopher Probst
 */
public class TaskQueue implements CancellableTask {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
	 * Here we store all tasks.
	 */
	private final Deque<Task> tasks;

	public TaskQueue() {
		this(new LinkedList<Task>());
	}

	public TaskQueue(Deque<Task> tasks) {
		if (tasks == null) {
			throw new NullPointerException("tasks");
		}
		this.tasks = tasks;
	}

	/**
	 * @return all tasks.
	 */
	public Deque<Task> tasks() {
		return tasks;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.twod.engine.util.task.CancellableTask#cancel()
	 */
	@Override
	public void cancel() {
		Task task;
		while ((task = tasks.poll()) != null) {
			if (task instanceof CancellableTask) {
				((CancellableTask) task).cancel();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.twod.engine.util.task.Task#update(float)
	 */
	@Override
	public boolean update(float tpf) {
		// The task var
		Task task;

		// Poll...
		while ((task = tasks.poll()) != null) {

			// Update...
			if (!task.update(tpf)) {

				// Reoffer...
				tasks.offerFirst(task);

				// Wait for next invocation
				break;
			}
		}

		return tasks.isEmpty();
	}
}

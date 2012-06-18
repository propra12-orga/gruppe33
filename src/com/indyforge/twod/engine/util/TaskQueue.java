package com.indyforge.twod.engine.util;

import java.util.Deque;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class TaskQueue implements Task {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * Here we can store all tasks.
	 */
	private final Deque<Task> tasks;

	public TaskQueue(Deque<Task> tasks) {
		if (tasks == null) {
			throw new NullPointerException("tasks");
		}
		this.tasks = tasks;
	}

	/**
	 * @return the remaining tasks of this entity.
	 */
	public Deque<Task> tasks() {
		return tasks;
	}

	/**
	 * Executes all remaining tasks.
	 */
	@Override
	public void run() {
		// Tmp var
		Runnable task;

		// Process all tasks
		while ((task = tasks.poll()) != null) {
			task.run();
		}
	}
}

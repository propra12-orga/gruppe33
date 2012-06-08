package com.indyforge.twod.engine.graphics.rendering.scenegraph.transform;

import java.io.Serializable;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.util.FilteredIterator;
import com.indyforge.twod.engine.util.IterationRoutines;
import com.indyforge.twod.engine.util.TypeFilter;

/**
 * Represents an abstract target. A target is basically something which can be
 * reached by transforming the controlled entity. This class uses the
 * {@link TransformMotor} to apply transformations.
 * 
 * @author Christopher Probst
 * 
 * @param <T>
 *            The target type.
 */
public abstract class Target<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * The controlled entity.
	 */
	private final GraphicsEntity controlled;

	/*
	 * The transform motor.
	 */
	private final TransformMotor transformMotor;

	/*
	 * The target.
	 */
	private final T target;

	/*
	 * The veloctiy which is used to reach the target.
	 */
	private final float veloctiy;

	/**
	 * @return the state of the controlled graphics entity.
	 */
	protected abstract T state();

	/**
	 * Adds the given state to the controlled graphics entity state.
	 * 
	 * @param state
	 *            The state you want to add.
	 */
	protected abstract void addToState(T state);

	/**
	 * Uses the {@link TransformMotor} to transform the controlled graphics
	 * entity every frame using the given state.
	 * 
	 * @param state
	 *            The state.
	 */
	protected abstract void transform(T state);

	/**
	 * Create a new abstract target.
	 * 
	 * @param controlled
	 *            The controlled graphics entity.
	 * @param target
	 *            The target you want to reach.
	 * @param velocity
	 *            The velocity to reach the target.
	 */
	public Target(GraphicsEntity controlled, T target, float velocity) {
		if (controlled == null) {
			throw new NullPointerException("controlled");
		} else if (target == null) {
			throw new NullPointerException("target");
		}

		// Lookup motor
		TransformMotor ptr = (TransformMotor) IterationRoutines
				.next(new FilteredIterator<Entity>(new TypeFilter(
						TransformMotor.class, true), controlled.childIterator(
						false, false)));

		if (ptr == null) {
			throw new IllegalArgumentException("The controlled entity does "
					+ "not have a transform motor as child");
		} else {
			// Save the parameters
			this.controlled = controlled;
			transformMotor = ptr;
			this.target = target;
			this.veloctiy = Math.abs(velocity);
		}
	}

	/**
	 * @return the controlled entity.
	 */
	public GraphicsEntity controlled() {
		return controlled;
	}

	/**
	 * @return the transform motor.
	 */
	public TransformMotor transformMotor() {
		return transformMotor;
	}

	/**
	 * @return the target.
	 */
	public T target() {
		return target;
	}

	/**
	 * @return the veloctiy.
	 */
	public float veloctiy() {
		return veloctiy;
	}

	/**
	 * Tries to reaches the target.
	 * 
	 * @param tpf
	 *            The time-per-frame.
	 * @return true if the target is reached now, otherwise false.
	 */
	public abstract boolean reachTarget(float tpf);
}

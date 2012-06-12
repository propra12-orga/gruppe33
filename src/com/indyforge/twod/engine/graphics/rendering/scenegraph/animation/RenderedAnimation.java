package com.indyforge.twod.engine.graphics.rendering.scenegraph.animation;

import java.awt.Graphics2D;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.sprite.Animation;
import com.indyforge.twod.engine.graphics.sprite.AnimationBundle;
import com.indyforge.twod.engine.graphics.sprite.Sprite;

/**
 * This is an animation controller. It uses an {@link AnimationBundle} to render
 * the animated images. You can specify the active animation.
 * 
 * @author Christopher Probst
 * @author Matthias Hesse
 * @see Animation
 * @see AnimationBundle
 * @see Sprite
 */
public class RenderedAnimation extends GraphicsEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum AnimationEvent {
		AnimationFinished
	}

	// The map which stores all animations
	private final AnimationBundle animationBundle;

	// The active animation name
	private String animationName;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity
	 * #onEvent(com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity,
	 * java.lang.Object, java.lang.Object[])
	 */
	@Override
	protected void onEvent(Entity source, Object event, Object... params) {
		super.onEvent(source, event, params);

		if (event instanceof AnimationEvent) {
			switch ((AnimationEvent) event) {
			case AnimationFinished:
				onAnimationFinished((RenderedAnimation) source,
						(Animation) params[0]);
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity#onUpdate
	 * (float)
	 */
	@Override
	protected void onUpdate(float tpf) {
		super.onUpdate(tpf);

		// At first update
		Animation animation = animation();

		// Check for last
		if (animation != null && !animation.update().isValid()) {
			// Fire event
			fireEvent(AnimationEvent.AnimationFinished, animation);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity
	 * #onRender(java.awt.Graphics2D, java.awt.Graphics2D)
	 */
	@Override
	protected void onRender(Graphics2D original, Graphics2D transformed) {
		super.onRender(original, transformed);

		// Get animation
		Animation animation = animation();

		if (animation != null && animation.isValid()) {
			// Draw centered
			transformed.translate(-0.5, -0.5);
			transformed.drawImage(animation.image(), 0, 0, 1, 1, null);
		}
	}

	/**
	 * OVERRIDE FOR CUSTOM RENDER BEHAVIOUR.
	 * 
	 * This method gets called every time when the animation gets invalid
	 * (reached the end).
	 * 
	 * @param renderedAnimation
	 * @param animation
	 */
	protected void onAnimationFinished(RenderedAnimation renderedAnimation,
			Animation animation) {
	}

	/**
	 * Creates a new animation controller with an empty animation bundle.
	 * 
	 */
	public RenderedAnimation() {
		this(new AnimationBundle());
	}

	/**
	 * Create a new animation controller using the given animation bundle.
	 * 
	 * @param animationBundle
	 *            The animation bundle you want to use.
	 */
	public RenderedAnimation(AnimationBundle animationBundle) {
		if (animationBundle == null) {
			throw new NullPointerException("animationBundle");
		}
		this.animationBundle = animationBundle;
		animationName(animationName);

		events().put(AnimationEvent.AnimationFinished,
				iterableChildren(true, true));
	}

	/**
	 * @return the animation bundle.
	 */
	public AnimationBundle animationBundle() {
		return animationBundle;
	}

	/**
	 * @return the name of the active animation.
	 */
	public String animationName() {
		return animationName;
	}

	/**
	 * Sets the name of the active animation.
	 * 
	 * @param animationName
	 *            The name of the animation you want to switch to.
	 * @return the animation.
	 */
	public Animation animationName(String animationName) {
		// Set and get
		return animationBundle.get(this.animationName = animationName);
	}

	public Animation animation() {
		return animationBundle.get(animationName);
	}
}

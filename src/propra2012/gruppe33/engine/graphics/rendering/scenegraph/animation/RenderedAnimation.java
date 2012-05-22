package propra2012.gruppe33.engine.graphics.rendering.scenegraph.animation;

import java.awt.Graphics2D;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.GraphicsEntity;
import propra2012.gruppe33.engine.graphics.sprite.Animation;
import propra2012.gruppe33.engine.graphics.sprite.AnimationBundle;
import propra2012.gruppe33.engine.graphics.sprite.Sprite;

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

	public enum RenderedAnimationEvent {
		LastImage
	}

	// The map which stores all animations
	public final AnimationBundle animationBundle;

	// The active animation name
	private String animationName;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity#onUpdate
	 * (float)
	 */
	@Override
	protected void onUpdate(float tpf) {

		// At first update
		Animation animation = animation();

		// Check for last
		if (animation != null && animation.update().isLast()) {
			// Fire event
			fireEvent(siblingIterator(true), RenderedAnimationEvent.LastImage,
					this, animation);
			fireEvent(childIterator(false, false),
					RenderedAnimationEvent.LastImage, this, animation);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * propra2012.gruppe33.engine.graphics.rendering.scenegraph.GraphicsEntity
	 * #onRender(java.awt.Graphics2D, java.awt.Graphics2D)
	 */
	@Override
	protected void onRender(Graphics2D original, Graphics2D transformed) {

		// Get animation
		Animation animation = animation();

		if (animation != null) {
			// Draw centered
			transformed.translate(-0.5, -0.5);
			transformed.drawImage(animation.image(), 0, 0, 1, 1, null);
		}
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

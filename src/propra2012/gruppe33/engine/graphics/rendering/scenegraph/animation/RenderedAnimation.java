package propra2012.gruppe33.engine.graphics.rendering.scenegraph.animation;

import java.awt.Graphics2D;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.GraphicsEntity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.util.SiblingIterator;
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

	public enum AnimationEvent {
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
		Animation animation = animationBundle.update(animationName);

		// Check for last
		if (animation.isLast()) {
			// Fire event
			fireEvent(new SiblingIterator(this, true),
					AnimationEvent.LastImage, this, animation);
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

		// Draw centered
		transformed.translate(-0.5, -0.5);
		transformed.drawImage(animationBundle.getImage(animationName), 0, 0, 1,
				1, null);
	}

	/**
	 * Creates a new animation controller with an empty animation bundle.
	 * 
	 * @param animationName
	 *            The name of the animation you want to switch to.
	 */
	public RenderedAnimation(String animationName) {
		this(new AnimationBundle(), animationName);
	}

	/**
	 * Create a new animation controller using the given animation bundle.
	 * 
	 * @param animationBundle
	 *            The animation bundle you want to use.
	 * @param animationName
	 *            The name of the animation you want to switch to.
	 */
	public RenderedAnimation(AnimationBundle animationBundle,
			String animationName) {
		if (animationBundle == null) {
			throw new NullPointerException("animationBundle");
		}
		animationName(animationName);
		this.animationBundle = animationBundle;
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

		// Check the name of the animation
		Animation animation = animationBundle
				.get(this.animationName = animationName);
		if (animation == null) {
			throw new IllegalArgumentException("Name not in use");
		} else {
			return animation;
		}
	}

	public Animation animation() {
		return animationBundle.get(animationName);
	}
}

package propra2012.gruppe33.engine.graphics.rendering.scenegraph.animation;

import java.awt.Graphics2D;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.EntityControllerAdapter;
import propra2012.gruppe33.engine.graphics.sprite.Animation;
import propra2012.gruppe33.engine.graphics.sprite.AnimationMap;
import propra2012.gruppe33.engine.graphics.sprite.Sprite;

/**
 * This is an animation controller. It uses an {@link AnimationMap} to render
 * the animated images. You can specify the active animation.
 * 
 * @author Christopher Probst
 * @author Matthias Hesse
 * @see Animation
 * @see AnimationMap
 * @see Sprite
 */
public class AnimationController extends EntityControllerAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The map which stores all animations
	public final AnimationMap animationMap;

	// The active animation name
	private String animation;

	/**
	 * Creates a new animation controller with an empty animation map.
	 */
	public AnimationController() {
		this(new AnimationMap());
	}

	/**
	 * Create a new animation controller using the given animation map.
	 * 
	 * @param animationMap
	 *            The animation map you want to use.
	 */
	public AnimationController(AnimationMap animationMap) {
		if (animationMap == null) {
			throw new NullPointerException("animationMap");
		}

		this.animationMap = animationMap;
	}

	/**
	 * @return the animation map.
	 */
	public AnimationMap getAnimationMap() {
		return animationMap;
	}

	/**
	 * @return the name of the active animation.
	 */
	public String getAnimation() {
		return animation;
	}

	/**
	 * Sets the name of the active animation.
	 * 
	 * @param animation
	 *            The name of the animation you want to switch to.
	 * @param reset
	 *            If true the choosen animation will be resetted.
	 */
	public void setAnimation(String animation, boolean reset) {

		// Save
		this.animation = animation;

		// Reset ?
		if (reset) {
			// Lookup animation
			Animation tmp = animationMap.get(animation);

			// Does the animation exist ?
			if (tmp != null) {
				tmp.resetAnimation();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * propra2012.gruppe33.graphics.rendering.scenegraph.EntityController#doRender
	 * (propra2012.gruppe33.graphics.rendering.scenegraph.Entity,
	 * java.awt.Graphics2D, java.awt.Graphics2D)
	 */
	@Override
	public void doRender(Entity entity, Graphics2D original,
			Graphics2D transformed) {

		// Lookup up active animation
		Animation tmp = animationMap.get(animation);

		// Render active animation if valid
		if (tmp != null) {
			// Draw centered
			transformed.translate(-0.5, -0.5);
			transformed.drawImage(tmp.getAnimationImage(), 0, 0, 1, 1, null);
		}
	}
}

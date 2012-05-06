package propra2012.gruppe33.graphics.rendering.scenegraph;

import java.awt.Graphics2D;

import propra2012.gruppe33.graphics.sprite.Animation;
import propra2012.gruppe33.graphics.sprite.AnimationMap;

public class AnimationController implements EntityController {

	// The map which stores all animations
	public final AnimationMap animationMap;
	private String animation;

	public AnimationController(AnimationMap animationMap) {
		this.animationMap = animationMap;
	}

	public AnimationMap getAnimationMap() {
		return animationMap;
	}

	public String getAnimation() {
		return animation;
	}

	public void setAnimation(String animation) {
		this.animation = animation;

		Animation tmp = animationMap.get(animation);
		if (tmp != null) {
			tmp.resetAnimation();
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

		if (tmp != null) {
			transformed.translate(-0.5, -0.5);
			transformed.drawImage(tmp.getAnimationImage(), 0, 0, 1, 1, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * propra2012.gruppe33.graphics.rendering.scenegraph.EntityController#doUpdate
	 * (propra2012.gruppe33.graphics.rendering.scenegraph.Entity, float)
	 */
	@Override
	public void doUpdate(Entity entity, float tpf) {
	}
}

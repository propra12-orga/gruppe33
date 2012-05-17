package propra2012.gruppe33.engine.graphics.sprite;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * This class maps names to animations.
 * 
 * @author Matthias Hesse
 * @see Animation
 * @see Sprite
 */
public final class AnimationMap extends HashMap<String, Animation> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AnimationMap() {
	}

	public AnimationMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public AnimationMap(int initialCapacity) {
		super(initialCapacity);
	}

	public AnimationMap(Map<? extends String, ? extends Animation> m) {
		super(m);
	}

	public Animation removeAnimation(Animation animation) {
		return remove(animation.getName());
	}

	public Animation addAnimation(Animation animation) {
		return put(animation.getName(), animation);
	}

	public BufferedImage getImage(String name) {
		Animation animation = get(name);
		return animation != null ? animation.getAnimationImage() : null;
	}

	public Animation resetAnimation(String name) {
		Animation animation = get(name);
		if (animation != null) {
			animation.resetAnimation();
		}
		return animation;
	}
}

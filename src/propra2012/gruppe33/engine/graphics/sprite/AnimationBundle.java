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
public final class AnimationBundle {

	/*
	 * Here we store all animations.
	 */
	private final Map<String, Animation> animations = new HashMap<String, Animation>();

	public Animation add(Animation animation) {
		if (animation == null) {
			throw new NullPointerException("animation");
		} else if (animations.containsKey(animation.getName())) {
			throw new IllegalArgumentException("Animation name already in use");
		}
		animations.put(animation.getName(), animation);
		return animation;
	}

	public Animation remove(Animation animation) {
		if (animation == null) {
			throw new NullPointerException("animation");
		}
		return remove(animation.getName());
	}

	public Animation remove(String name) {
		return animations.remove(name);
	}

	public Animation get(String name) {
		return animations.get(name);
	}

	public boolean contains(String name) {
		return animations.containsKey(name);
	}

	public BufferedImage getImage(String name) {
		Animation animation = animations.get(name);
		if (animation == null) {
			throw new IllegalArgumentException("Name not in use");
		} else {
			return animation.getImage();
		}
	}

	public Animation update(String name) {
		Animation animation = animations.get(name);
		if (animation == null) {
			throw new IllegalArgumentException("Name not in use");
		} else {
			return animation.update();
		}
	}

	public Animation reset(String name) {
		Animation animation = animations.get(name);
		if (animation == null) {
			throw new IllegalArgumentException("Name not in use");
		} else {
			return animation.reset();
		}
	}
}

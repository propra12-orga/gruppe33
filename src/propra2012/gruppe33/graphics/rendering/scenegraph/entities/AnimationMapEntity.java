package propra2012.gruppe33.graphics.rendering.scenegraph.entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import propra2012.gruppe33.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.graphics.sprite.AnimationMap;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class AnimationMapEntity extends Entity {

	private final AnimationMap animations = new AnimationMap();
	private String activeAnimation;

	public AnimationMapEntity(String id) {
		super(id);
	}

	public AnimationMap getAnimations() {
		return animations;
	}

	public String getActiveAnimation() {
		return activeAnimation;
	}

	public void setActiveAnimation(String activeAnimation) {
		this.activeAnimation = activeAnimation;
	}

	public void resetActiveAnimation() {
		animations.resetAnimation(activeAnimation);
	}

	@Override
	protected void doRender(Graphics2D original, Graphics2D transformed) {
		super.doRender(original, transformed);
		// Try to get active image
		BufferedImage image = animations.getImage(activeAnimation);

		if (image != null) {
			transformed.drawImage(image, 0, 0, null);
		}
	}
}

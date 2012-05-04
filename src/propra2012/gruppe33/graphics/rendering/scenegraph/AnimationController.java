package propra2012.gruppe33.graphics.rendering.scenegraph;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import propra2012.gruppe33.graphics.sprite.Animation;

public class AnimationController extends Animation implements EntityController {

	public AnimationController(String name, long timePerImage, File dir,
			String prefix, String postfix, int count) throws IOException {
		super(name, timePerImage, dir, prefix, postfix, count);
	}

	public AnimationController(String name, List<BufferedImage> images,
			long timePerImage) {
		super(name, images, timePerImage);
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
		transformed.drawImage(getAnimationImage(), 0, 0, 1, 1, null);
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

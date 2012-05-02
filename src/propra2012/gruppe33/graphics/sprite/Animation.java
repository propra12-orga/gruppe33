package propra2012.gruppe33.graphics.sprite;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;

/**
 * This class represents a single Animation
 * 
 * @author Matthias Hesse
 * 
 */
public final class Animation {

	public static Animation merge(String name, long timePerImage,
			Animation... animations) {
		List<BufferedImage> subImages = new ArrayList<BufferedImage>();
		for (Animation a : animations) {
			subImages.addAll(a.getImages());
		}

		return new Animation(name, subImages, timePerImage);
	}

	// This list saves all images used by this animation
	private final List<BufferedImage> images;

	// The name of this animation
	private final String name;

	// The animation step counter
	private int animationStep = 0;

	// The duration of the animation
	private final long animationDuration;

	// Time vars
	private long timeStamp, timePerImage;

	Animation(String name, List<BufferedImage> images, long timePerImage) {

		if (name == null) {
			throw new NullPointerException("name");
		} else if (images == null) {
			throw new NullPointerException("images");
		} else if (images.size() == 0) {
			throw new IllegalArgumentException(
					"there should be at least one image");
		} else if (!(images instanceof RandomAccess)) {
			throw new IllegalArgumentException("For performance reasons we "
					+ "only accepts random-access list here!");
		}

		// Save the name
		this.name = name;

		// Calculate the duration for the whole animation
		animationDuration = images.size() * timePerImage;

		// Save the time per image
		this.timePerImage = timePerImage;

		// Read the list of SubImages
		this.images = images;

		// Start...
		resetAnimation();
	}

	public String getName() {
		return name;
	}

	public long getAnimationDuration() {
		return animationDuration;
	}

	public int getAnimationStep() {
		return animationStep;
	}

	public List<BufferedImage> getImages() {
		return images;
	}

	public long getTimePerImage() {
		return timePerImage;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	/**
	 * This Method returns the actual image of this animation using the actual
	 * timestamp.
	 * 
	 * @return
	 */
	public BufferedImage getAnimationImage() {

		// Read the actual time
		long time = System.currentTimeMillis();

		// Calculate if the time difference from the last step is higher then
		// the time-per-image
		// If yes the timeStamp will be incresed
		if (time - timeStamp > timePerImage) {
			animationStep++;
			timeStamp = time;
		}

		// If the animation is at the end the animation is resetted
		if (animationStep >= images.size()) {
			animationStep = 0;

		}

		// Return the image of the actual Animation Step
		return images.get(animationStep);
	}

	/**
	 * This Method resets the current Animation.
	 */
	public void resetAnimation() {
		// Reset the step
		animationStep = 0;

		// Set the TimeStep to the actual time
		timeStamp = System.currentTimeMillis();
	}
}

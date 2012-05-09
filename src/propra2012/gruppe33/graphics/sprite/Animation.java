package propra2012.gruppe33.graphics.sprite;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

/**
 * This class represents a single Animation
 * 
 * @author Matthias Hesse
 * 
 */
public final class Animation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The sprite which created this animation
	private final Sprite sprite;

	// This list saves all image coords used by this animation
	private final List<Point> imageCoords;

	// The name of this animation
	private final String name;

	// Should this animation loop ?
	private boolean loop = true;

	// Is this animation paused ?
	private boolean paused = false;

	// The duration of the animation
	private final long animationDuration, timePerImage;

	// The animation step counter
	private transient int animationStep = 0;

	// Time vars
	private transient long timeStamp;

	/*
	 * When deserializing reset the animation.
	 */
	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {

		// Read the default stuff
		in.defaultReadObject();

		// Always reset an animation when loading
		resetAnimation();
	}

	Animation(Sprite sprite, String name, List<Point> imageCoords,
			long timePerImage) {

		if (sprite == null) {
			throw new NullPointerException("sprite");
		} else if (name == null) {
			throw new NullPointerException("name");
		} else if (imageCoords == null) {
			throw new NullPointerException("images");
		} else if (imageCoords.size() == 0) {
			throw new IllegalArgumentException(
					"there should be at least one image coord");
		} else if (!(imageCoords instanceof RandomAccess)) {
			throw new IllegalArgumentException("For performance reasons we "
					+ "only accepts random-access list here!");
		}

		// Save the sprite
		this.sprite = sprite;

		// Save the name
		this.name = name;

		// Calculate the duration for the whole animation
		animationDuration = imageCoords.size() * timePerImage;

		// Save the time per image
		this.timePerImage = timePerImage;

		// Save the image coords
		this.imageCoords = Collections.unmodifiableList(imageCoords);

		// Start...
		resetAnimation();
	}

	public Sprite getSprite() {
		return sprite;
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public boolean isLoop() {
		return loop;
	}

	public void setLoop(boolean loop) {
		this.loop = loop;
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

	public List<Point> getImageCoords() {
		return imageCoords;
	}

	public long getTimePerImage() {
		return timePerImage;
	}

	/**
	 * This Method returns the actual image of this animation using the actual
	 * timestamp.
	 * 
	 * @return
	 */
	public BufferedImage getAnimationImage() {

		if (paused) {
			Point coord = imageCoords.get(animationStep);
			return sprite.getSubImages()[coord.x][coord.y];
		}

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
		if (animationStep >= imageCoords.size()) {
			if (loop) {
				animationStep = 0;
			} else {
				animationStep = imageCoords.size() - 1;
			}
		}

		// Return the image of the actual Animation Step
		Point coord = imageCoords.get(animationStep);
		return sprite.getSubImages()[coord.x][coord.y];
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

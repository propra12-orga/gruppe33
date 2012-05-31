package com.indyforge.twod.engine.graphics.sprite;

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
 * @see AnimationBundle
 * @see Sprite
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
	private boolean loop = false;

	// Is this animation paused ?
	private boolean paused = true;

	// The duration of the animation
	private final long duration, timePerImage;

	// The step counter
	private int step;

	// Time vars
	private transient long timeStamp;

	/*
	 * When deserializing reset the animation.
	 */
	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {

		// Read the default stuff
		in.defaultReadObject();

		// Set the TimeStep to the actual time
		timeStamp = System.currentTimeMillis();
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
		duration = imageCoords.size() * timePerImage;

		// Save the time per image
		this.timePerImage = timePerImage;

		// Save the image coords
		this.imageCoords = Collections.unmodifiableList(imageCoords);

		// Start...
		reset();
	}

	public Animation duplicate() {
		// Create clone
		return new Animation(sprite, name, imageCoords, timePerImage);
	}

	public Sprite sprite() {
		return sprite;
	}

	public boolean isPaused() {
		return paused;
	}

	public Animation paused(boolean paused) {
		this.paused = paused;
		return this;
	}

	public boolean isLoop() {
		return loop;
	}

	public Animation loop(boolean loop) {
		this.loop = loop;
		return this;
	}

	public String name() {
		return name;
	}

	public long duration() {
		return duration;
	}

	public int step() {
		return step;
	}

	public int nextStep() {
		if (paused) {
			return step;
		}

		return step >= imageCoords.size() - 1 ? (loop ? 0 : -1) : step + 1;
	}

	public List<Point> imageCoords() {
		return imageCoords;
	}

	public long timePerImage() {
		return timePerImage;
	}

	public boolean isFirst() {
		return step == 0;
	}

	public boolean isLast() {
		return step == imageCoords.size() - 1;
	}

	public boolean isValid() {
		return step != -1;
	}

	public BufferedImage image() {
		if (!isValid()) {
			throw new IllegalStateException("Animation is not valid anymore");
		}
		Point coord = imageCoords.get(step);
		return sprite.getSubImages()[coord.x][coord.y];
	}

	public Animation update() {

		if (!paused && isValid()) {

			// Read the actual time
			long time = System.currentTimeMillis();

			/*
			 * Calculate if the time difference from the last step is higher
			 * then the time-per-image If yes the timeStamp will be incresed
			 */
			if (time - timeStamp >= timePerImage) {

				// Swap
				step = nextStep();

				timeStamp = time;
			}
		}
		return this;
	}

	/**
	 * This Method resets the current Animation.
	 */
	public Animation reset() {
		// Reset the step
		step = 0;

		// Set the TimeStep to the actual time
		timeStamp = System.currentTimeMillis();

		return this;
	}
}

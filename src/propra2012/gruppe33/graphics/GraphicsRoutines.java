package propra2012.gruppe33.graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;

import propra2012.gruppe33.graphics.rendering.scenegraph.SceneProcessor;
import propra2012.gruppe33.graphics.rendering.scenegraph.math.Mathf;
import propra2012.gruppe33.graphics.sprite.Sprite;

/**
 * Utility class to bundle some default graphics routines like creating frames.
 * 
 * @author Christopher Probst
 * @author Matthias Hesse
 */
public final class GraphicsRoutines {

	/**
	 * Optimizes this image to be compatible with the screen.
	 * 
	 * @param image
	 *            The image you want to optimize.
	 * @return a optimized copy of the given image.
	 */
	public static BufferedImage optimizeImage(BufferedImage image) {

		// Create a new optimized image
		BufferedImage optimizedImage = GraphicsRoutines.createImage(
				image.getWidth(), image.getHeight(), image.getTransparency());

		// Get graphics context
		Graphics2D g2d = optimizedImage.createGraphics();

		try {
			// Draw the loaded image into the optimized image
			g2d.drawImage(image, 0, 0, null);
		} finally {
			g2d.dispose();
		}

		return optimizedImage;
	}

	/**
	 * Clear the given image with the given background color.
	 * 
	 * @param image
	 *            The image you want to clear.
	 * @param background
	 *            The background color. If null the image is not touched.
	 * @return the image.
	 */
	public static BufferedImage clear(BufferedImage image, Color background) {
		if (image == null) {
			throw new NullPointerException("image");
		}

		// Clear background if not null
		if (background != null) {
			Graphics2D g2d = image.createGraphics();
			try {
				g2d.setBackground(background);
				g2d.clearRect(0, 0, image.getWidth(), image.getHeight());
			} finally {
				g2d.dispose();
			}
		}
		return image;
	}

	/**
	 * Creates a compatible image.
	 * 
	 * @param width
	 *            The width of the new image.
	 * @param height
	 *            The height of the new image.
	 * @return the new compatible image.
	 */
	public static BufferedImage createImage(int width, int height) {
		return getGC().createCompatibleImage(width, height);
	}

	/**
	 * Creates a compatible image.
	 * 
	 * @param width
	 *            The width of the new image.
	 * @param height
	 *            The height of the new image.
	 * @param transparency
	 *            The transparency of the new image.
	 * @return the new compatible image.
	 */
	public static BufferedImage createImage(int width, int height,
			int transparency) {
		return getGC().createCompatibleImage(width, height, transparency);
	}

	/**
	 * Creates a compatible volatile image.
	 * 
	 * @param width
	 *            The width of the new volatile image.
	 * @param height
	 *            The height of the new volatile image.
	 * @return the new compatible volatile image.
	 */
	public static VolatileImage createVolatileImage(int width, int height) {
		return getGC().createCompatibleVolatileImage(width, height);
	}

	/**
	 * Creates a compatible volatile image.
	 * 
	 * @param width
	 *            The width of the new volatile image.
	 * @param height
	 *            The height of the new volatile image.
	 * @param transparency
	 *            The transparency of the new volatile image.
	 * @return the new compatible volatile image.
	 */
	public static VolatileImage createVolatileImage(int width, int height,
			int transparency) {
		return getGC().createCompatibleVolatileImage(width, height,
				transparency);
	}

	/**
	 * @return the active graphics configuration.
	 */
	public static GraphicsConfiguration getGC() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getDefaultConfiguration();
	}

	/**
	 * Reads a sprite sub image.
	 * 
	 * @param image
	 *            The image which contains the sub images.
	 * @param rasterX
	 *            The columns of the image.
	 * @param rasterY
	 *            The rows of the image.
	 * @param x
	 *            The column of the sub image.
	 * @param y
	 *            The row of the sub image.
	 * @return the sub image.
	 * @see Sprite
	 */
	public static BufferedImage getSpriteSubImage(BufferedImage image,
			int rasterX, int rasterY, int x, int y) {

		if (image == null) {
			throw new NullPointerException("image");
		} else if (rasterX <= 0) {
			throw new IllegalArgumentException("rasterX must be > 0");
		} else if (rasterY <= 0) {
			throw new IllegalArgumentException("rasterY must be > 0");
		}

		// Clamp the coords
		x = Mathf.clamp(x, 0, rasterX - 1);
		y = Mathf.clamp(y, 0, rasterY - 1);

		// Calculating the size of one sub image
		int sizeXPlate = image.getWidth(null) / rasterX;
		int sizeYPlate = image.getHeight(null) / rasterY;

		// Return the optimized copy of the sub-image
		return optimizeImage(image.getSubimage(x * sizeXPlate, y * sizeYPlate,
				sizeXPlate, sizeYPlate));
	}

	/**
	 * Creates a new visible frame which contains the processor.
	 * 
	 * @param processor
	 *            The processor of the frame.
	 * @param title
	 *            The title of the frame.
	 * @param width
	 *            The width of the frame.
	 * @param height
	 *            The height of the frame.
	 * @return a new visible frame.
	 * @throws Exception
	 *             If init fails.
	 */
	public static Frame createFrame(SceneProcessor<?> processor, String title,
			int width, int height) throws Exception {
		return createFrame(processor, title, width, height, true);
	}

	/**
	 * Creates a new frame which contains the processor.
	 * 
	 * @param processor
	 *            The processor of the frame.
	 * @param title
	 *            The title of the frame.
	 * @param width
	 *            The width of the frame.
	 * @param height
	 *            The height of the frame.
	 * @param visible
	 *            The visible flag of the frame.
	 * @return a new frame.
	 * @throws Exception
	 *             If init fails.
	 */
	public static Frame createFrame(final SceneProcessor<?> processor,
			final String title, final int width, final int height,
			final boolean visible) throws Exception {

		if (processor == null) {
			throw new NullPointerException("processor");
		}

		/*
		 * Create a new frame for viewing. Should be ok that it is not invoked
		 * on EDT since this is a new variable and the EDT does not know it yet
		 * so there cannot be any cached data yet.
		 */
		final Frame frame = new Frame(title);

		/*
		 * Invoke the setup on the EDT.
		 */
		EventQueue.invokeAndWait(new Runnable() {

			@Override
			public void run() {
				// Set border layout
				frame.setLayout(new BorderLayout());

				// Disable the repaint events
				frame.setIgnoreRepaint(true);

				// Is this a component ?
				if (processor instanceof Component) {
					// Add to frame
					frame.add((Component) processor, BorderLayout.CENTER);
				} else {
					throw new IllegalArgumentException(
							"The given processor is not a component.");
				}

				// Set size
				frame.setSize(width, height);

				// Shutdown hook
				frame.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {

						// Schedule for shutdown
						processor.getTasks().add(new Runnable() {

							@Override
							public void run() {
								processor.setShutdownRequest(true);
							}
						});
					}
				});

				// Make visible (Does not need the EDT)
				frame.setVisible(visible);
			}
		});

		return frame;
	}

	// Should not be instantiated
	private GraphicsRoutines() {
	}
}

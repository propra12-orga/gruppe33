package propra2012.gruppe33.routines;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import propra2012.gruppe33.graphics.rendering.scenegraph.SceneProcessor;

/**
 * Utility class to bundle some default graphics routines like creating frames.
 * 
 * @author Christopher Probst
 * 
 */
public final class GraphicsRoutines {

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

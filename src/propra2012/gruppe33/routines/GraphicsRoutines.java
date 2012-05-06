package propra2012.gruppe33.routines;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import propra2012.gruppe33.graphics.rendering.active.CanvasSceneProcessor;
import propra2012.gruppe33.graphics.rendering.scenegraph.Scene;
import propra2012.gruppe33.graphics.rendering.scenegraph.SceneProcessor;

/**
 * Utility class to bundle some default graphics routines like creating frames.
 * 
 * @author Christopher Probst
 * 
 */
public final class GraphicsRoutines {

	/**
	 * Creates a valid scene processor.
	 * 
	 * @return a valid implementation of scene processor.
	 */
	public static SceneProcessor<Scene> createProcessor() {
		return new CanvasSceneProcessor<Scene>();
	}

	/**
	 * Creates a valid scene processor.
	 * 
	 * @param sceneClass
	 *            The scene type of the processor.
	 * @return a valid implementation of scene processor.
	 */
	public static <S extends Scene> SceneProcessor<S> createProcessor(
			Class<S> sceneClass) {
		return new CanvasSceneProcessor<S>();
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
	 */
	public static Frame createFrame(SceneProcessor<?> processor, String title,
			int width, int height) {
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
	 */
	public static Frame createFrame(final SceneProcessor<?> processor,
			String title, int width, int height, boolean visible) {

		if (processor == null) {
			throw new NullPointerException("processor");
		}

		// Create a new frame for viewing
		final Frame frame = new Frame(title);

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

		// Make visible
		frame.setVisible(visible);

		return frame;
	}

	// Should not be instantiated
	private GraphicsRoutines() {
	}
}

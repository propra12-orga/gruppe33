/**
 * File: BufferStrategySnippet.java
 *
 * This file is free to use and modify as it is for educational use.
 *
 * Version:
 * 1.1 Initial Version
 *
 */

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This example shows how to use the BufferStrategy class. It will also be
 * hardware acclerated, on windows by default and use DirectX, however, on unix
 * you must enable opengl.
 * <p/>
 * See "http://java.sun.com/j2se/1.5.0/docs/guide/2d/flags.html" for details,
 * about runtime flags for controlling hardware accleration.
 *
 * @author Nick
 */
public class BufferStrategySnippet extends Canvas {

	// add some system property control flags. Setting these flags as "True"
	// with a captial will notify you via the console if the particular
	// pipeline D3D or Opengl has been successfully enabled. Using "true" all
	// lowercase will turn it on silently, you can still find out if it was
	// successful using the trace property as it will show calls to either D3D
	// or opengl, or neither.
	//
	// NOTE: trace does not seem to work for opengl with jdk1.6.0_20
	static {
		System.setProperty("sun.java2d.trace", "timestamp,log,count");
		System.setProperty("sun.java2d.transaccel", "True");
		System.setProperty("sun.java2d.opengl", "True");
		// System.setProperty("sun.java2d.d3d", "false"); //default on windows
		// System.setProperty("sun.java2d.ddforcevram", "true");
	}

	// app title.
	private static final String	TITLE	= "GPSnippets: BufferStrategy Snippet";

	// the strategy used for double buffering, or any number of buffered frames.
	private BufferStrategy		strategy;

	// our time keeper
	private final Timer			timer;

	// the main render and update task.
	private TimerTask			renderTask;

	// just a block to move around
	private Rectangle2D.Double	basicBlock;

	// just a gradient background for our little demo.
	private Paint				backgroundGradient;

	// our little basic blocks speed, which will be multiplied by the number
	// of seconds passed, each render frame.
	private final double		speed	= 150.0d /* px/sec */;

	// true if the right arrow key is being pressed, false otherwise
	private boolean				right	= false;

	// true if the left arrow key is being pressed, false otherwise
	private boolean				left	= false;

	/**
	 * This configures the canvas component for rendering. Creates any objects
	 * we need and sets up some default listeners for component events.
	 */
	public BufferStrategySnippet() {
		// we will be doing our own rendering, using the strategy.
		this.setIgnoreRepaint(true);

		timer = new Timer(); // used for the render thread

		// add quick and dirty 2-directional movement for our block
		addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					right = true;
				}

				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					left = true;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					right = false;
				}

				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					left = false;
				}
			}
		});
	}

	/**
	 * Our drawing function which utilizes the BufferStrategy that enables us to
	 * do offscreen rendering without having to wait for swing to repaint, the
	 * component. It also eliminates flickering and splicing issues.
	 */
	public void render() {
		// the back buffer graphics object
		Graphics2D bkG = (Graphics2D) strategy.getDrawGraphics();

		// clear the backbuffer, this could be substituted for a background
		// image or a tiled background.
		bkG.setPaint(backgroundGradient);
		bkG.fillRect(0, 0, getWidth(), getHeight());

		// TODO: Draw your game world, or scene or anything else here.

		// Rectangle2D is a shape subclass, and the graphics object can render
		// it, makes things a little easier.
		bkG.setColor(Color.green.darker());
		bkG.fill(basicBlock);

		// properly dispose of the backbuffer graphics object. Release resources
		// and cleanup.
		bkG.dispose();

		// flip/draw the backbuffer to the canvas component.
		strategy.show();

		// synchronize with the display refresh rate.
		Toolkit.getDefaultToolkit().sync();
	}

	/**
	 * It is necessary to wait until after the component has been displayed in
	 * order to create and retrieve the buffer strategy. This is the part that
	 * took me the longest to figure out. But it makes sense since the component
	 * requires native resources in order to perform hardware acceleration and
	 * those resources are handled by the component itself, and are only
	 * available once the component is created and displayed.
	 */
	public void setup() {
		// create the block, as a basic paddle
		basicBlock = new Rectangle2D.Double(0, 0, 128, 16);

		// center the block in the canvas.
		basicBlock.x = this.getWidth() / 2 - basicBlock.width / 2;

		// put the block at the bottom of the canvas
		basicBlock.y = this.getHeight() - basicBlock.height - 20;

		// create the background gradient paint object.
		backgroundGradient = new GradientPaint(0, 0, Color.gray, getWidth(),
				getHeight(), Color.lightGray.brighter());

		// create a strategy that uses two buffers, or is double buffered.
		this.createBufferStrategy(2);

		// get a reference to the strategy object, for use in our render method
		// this isn't necessary but it eliminates a call during rendering.
		strategy = this.getBufferStrategy();

		start();
	}

	/**
	 * Initialize the render and update tasks, to call the render method, do
	 * timing and FPS counting, handling input and canceling existing tasks.
	 */
	public void start() {
		// if the render task is already running stop it, this may cause an
		// exception to be thrown if the task is already canceled.
		if (renderTask != null) {
			renderTask.cancel();
		}

		// our main task for handling the rendering and for updating and
		// handling input and movement events. The timer class isn't the most
		// reliable for game updating and rendering but it will suffice for the
		// purpose of this snippet.
		renderTask = new TimerTask() {
			long	lasttime	= System.currentTimeMillis();

			@Override
			public void run() {

				// get the current system time
				long time = System.currentTimeMillis();

				// calculate the time passed in milliseconds
				double dt = (time - lasttime) * 0.001;

				// save the current time
				lasttime = time;

				// for now just move the basic block
				if (right) {
					basicBlock.x += dt * speed;
				} else if (left) {
					basicBlock.x -= dt * speed;
				}

				render();
			}
		};

		// These will cap our frame rate but give us unexpected results if our
		// rendering or updates take longer than the 'period' time. It
		// is likely that we could have overlapping calls.
		timer.schedule(renderTask, 0, 16);
	}

	/**
	 * Stops the rendering cycle so that the application can close gracefully.
	 */
	protected void stop() {
		renderTask.cancel();
	}

	/**
	 * Creates a Frame and adds a new canvas to the Frame, displays it and
	 * initializes the rendering method.
	 */
	protected static void createAndDisplay() {
		// Never mix swing and awt, since we use a canvas to utilize the
		// buffered strategy we will put the canvas in a Frame instead of a
		// JFrame.
		final Frame frame = new Frame(TITLE);
		frame.setLayout(new BorderLayout());
		final BufferStrategySnippet canvas = new BufferStrategySnippet();
		frame.add(canvas);

		// convenience exiting from the demo using the ESCAPE key.
		canvas.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					canvas.stop(); // first stop the drawing and updating
					frame.setVisible(false); // hide the window quickly
					frame.dispose(); // release all system resources
					System.exit(0); // finally exit.
				}
			}
		});

		// need this to trap when the user attempts to close the window using
		// the close icon for the window, or the close option from the window
		// menu or alt+f4 or by other means.
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				canvas.stop(); // first stop the drawing and updating
				frame.setVisible(false); // hide the window quickly
				frame.dispose(); // release all system resources
				System.exit(0); // finally exit.
			}
		});
		frame.setSize(800, 600); // should use configurable properties here
		frame.setLocationRelativeTo(null); // centers window on screen
		frame.setVisible(true); // creates and displays the actual window

		// this is our scene setup to initialize all necessary configurable
		// objects and properties. Using a setup method helps control the way
		// things look from a single location, it can be extended to include
		// how things act as well.
		canvas.setup();
	}

	/**
	 * Calls the swing event thread to create and display a new application
	 * frame. This is done so that the setVisible method is not part of the main
	 * application thread but is done within the swing event thread.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		// create and display the window.
		createAndDisplay();
	}

}

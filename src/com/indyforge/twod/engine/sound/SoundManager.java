package com.indyforge.twod.engine.sound;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.MathExt;
import com.indyforge.twod.engine.resources.assets.Asset;
import com.indyforge.twod.engine.resources.assets.AssetManager;

/**
 * This class manages/plays game sounds with the Java Sound API.
 * 
 * @author Matthias Hesse/MrBumble
 */
public final class SoundManager implements Serializable {

	/*
	 * This set must be synchroniced because the clips usually are played and
	 * stopped in different threads.
	 */
	private static final Set<Clip> currentSounds = new LinkedHashSet<Clip>();

	/*
	 * The mute function.
	 */
	private static boolean mute = false;

	/**
	 * Sets mute.
	 * 
	 * @param mute
	 *            If true no further sounds will be played.
	 */
	public static void mute(boolean mute) {
		if (SoundManager.mute = mute) {
			closeCurrentSounds();
		}
	}

	/**
	 * @return the mute flag.
	 */
	public static boolean isMute() {
		return mute;
	}

	/**
	 * Closes all active sounds.
	 */
	public static void closeCurrentSounds() {
		for (Clip clip : currentSounds()) {
			clip.close();
		}
	}

	/**
	 * @return a set which contains all active clips.
	 */
	public static Set<Clip> currentSounds() {
		synchronized (currentSounds) {
			return new HashSet<Clip>(currentSounds);
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * The asset manager of this sound manager.
	 */
	private final AssetManager assetManager;

	/*
	 * This map maps strings to byte arrays. The byte arrays contain the binary
	 * sound data. When starting a sound these byte arrays are used to create
	 * new clips.
	 */
	private final Map<String, Asset<byte[]>> soundMap = new HashMap<String, Asset<byte[]>>();

	/*
	 * Used to create / start the clips.
	 */
	private transient ExecutorService soundExecutor;

	/*
	 * The default volume.
	 */
	private float defaultVolume = 0.4f;

	/**
	 * Creates the current sounds other stuff.
	 */
	private void initSoundManager() {

		/*
		 * Use deamon threads.
		 */
		soundExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {

			private final ThreadFactory defaultThreadFactory = Executors
					.defaultThreadFactory();

			@Override
			public Thread newThread(Runnable r) {
				Thread thread = defaultThreadFactory.newThread(r);
				thread.setDaemon(true);
				return thread;
			}
		});
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {

		// Restore all vars
		in.defaultReadObject();

		if (!AssetManager.isHeadless()) {
			// Init
			initSoundManager();
		}
	}

	public SoundManager(AssetManager assetManager) {
		if (assetManager == null) {
			throw new NullPointerException("assetManager");
		}

		this.assetManager = assetManager;

		if (!AssetManager.isHeadless()) {
			// Init
			initSoundManager();
		}
	}

	/**
	 * @return the asset manager;
	 */
	public AssetManager assetManager() {
		return assetManager;
	}

	/**
	 * @return the default volume.
	 */
	public float defaultVolume() {
		return defaultVolume;
	}

	/**
	 * 
	 * @param defaultVolume
	 *            The default volume.
	 * @return this for chaining.
	 */
	public SoundManager defaultVolume(float defaultVolume) {
		this.defaultVolume = defaultVolume;
		return this;
	}

	/**
	 * Uses the given input stream to read the complete sound data and puts it
	 * into the map using the given name.
	 * 
	 * @param name
	 *            The name of the sound.
	 * @param assetPath
	 *            The asset path of the sound.
	 * @return the previous value or null.
	 * @throws Exception
	 *             If an exception occurs.
	 */
	public Asset<byte[]> putSound(String name, String assetPath)
			throws Exception {

		if (name == null) {
			throw new NullPointerException("name");
		} else if (assetPath == null) {
			throw new NullPointerException("assetPath");
		}

		// Reads the complete stream and puts it into the map
		return soundMap.put(name, assetManager.loadBytes(assetPath, false));
	}

	/**
	 * Creates a new clip using the given sound data and the
	 * {@link SoundManager#defaultVolume(float) default volume}.
	 * 
	 * @param name
	 *            The name of the sound.
	 * @param oneshot
	 *            If true the sound will be played a single time, otherwise it
	 *            will be looped forever.
	 * @return the future which will retrieve the new clip or null (If sound
	 *         does not exist, or headless mode, or...)
	 */
	public Future<Clip> playSound(final String name, final boolean oneshot) {
		return playSound(name, defaultVolume, oneshot);
	}

	/**
	 * Creates a new clip using the given sound data.
	 * 
	 * @param name
	 *            The name of the sound.
	 * @param oneshot
	 *            If true the sound will be played a single time, otherwise it
	 *            will be looped forever.
	 * @param volume
	 *            The volume between 0.0f and 1.0f.
	 * @return the future which will retrieve the new clip or null (If sound
	 *         does not exist, or headless mode, or...)
	 */
	public Future<Clip> playSound(final String name, final float volume,
			final boolean oneshot) {

		// No executor ?
		if (soundExecutor == null || mute) {
			return null;
		}

		/*
		 * Start the new sound in a thread pool.
		 */
		return soundExecutor.submit(new Callable<Clip>() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.concurrent.Callable#call()
			 */
			@Override
			public Clip call() throws Exception {
				// Get the sound data
				Asset<byte[]> soundAsset = soundMap.get(name);

				// Check for null
				if (soundAsset == null) {
					return null;
				}

				try {
					// Create new audio input stream
					AudioInputStream ais = AudioSystem
							.getAudioInputStream(new ByteArrayInputStream(
									soundAsset.get()));

					// Get line (using clip interface)
					DataLine.Info info = new DataLine.Info(Clip.class, ais
							.getFormat());

					// Create a new clip
					final Clip clip = (Clip) AudioSystem.getLine(info);

					// Open with binary data
					clip.open(ais);

					// Get the gain control
					FloatControl gainControl = (FloatControl) clip
							.getControl(FloatControl.Type.MASTER_GAIN);

					// Set the volume
					gainControl.setValue((float) (Math.log(MathExt.clamp(
							volume, 0, 1)) / Math.log(10.0) * 20.0));

					// Add into set
					synchronized (currentSounds) {
						currentSounds.add(clip);
					}

					/*
					 * Very important:
					 * 
					 * This listener stops/removes the created clip.
					 */
					clip.addLineListener(new LineListener() {

						@Override
						public void update(LineEvent event) {
							if (event.getType() == Type.STOP) {
								// If the line is stopped -> close it!
								event.getLine().close();
							} else if (event.getType() == Type.CLOSE) {
								// If the line is closed -> remove it!
								synchronized (currentSounds) {
									currentSounds.remove(clip);
								}
							}
						}
					});

					// Start !
					if (oneshot) {
						clip.start();
					} else {
						clip.loop(Clip.LOOP_CONTINUOUSLY);
					}

					return clip;

				} catch (Exception e) {
					System.err
							.println("Failed to create & start sound. Reason: "
									+ e.getMessage());
					return null;
				}
			}
		});
	}

	/**
	 * Clears all sound mappings.
	 */
	public void clearSounds() {
		soundMap.clear();
	}

	/**
	 * Removes the mapping with the given name.
	 * 
	 * @param name
	 *            The name of the mapping you want to remove.
	 * @return the old value or null.
	 */
	public Asset<byte[]> removeSound(String name) {
		return soundMap.remove(name);
	}

	/**
	 * @param name
	 *            The name of the sound.
	 * @return the binary byte array which contains the sound data of the given
	 *         name or null.
	 */
	public Asset<byte[]> soundData(String name) {
		return soundMap.get(name);
	}
}

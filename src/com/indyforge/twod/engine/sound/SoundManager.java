package com.indyforge.twod.engine.sound;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
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
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;

import com.indyforge.twod.engine.resources.assets.Asset;
import com.indyforge.twod.engine.resources.assets.AssetManager;

/**
 * This class manages/plays game sounds with the Java Sound API.
 * 
 * @author Matthias Hesse/MrBumble
 */
public final class SoundManager implements Serializable {

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
	 * This set must be synchroniced because the clips usually are played and
	 * stopped in different threads.
	 */
	private transient Set<Clip> currentSounds, readOnlyCurrentSounds;

	/*
	 * Used to create / start the clips.
	 */
	private transient ExecutorService soundExecutor;

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
		currentSounds = Collections.synchronizedSet(new LinkedHashSet<Clip>());
		readOnlyCurrentSounds = Collections.unmodifiableSet(currentSounds);
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
	 * @return an unmodifiable set which contains all active clips.
	 */
	public Set<Clip> currentSounds() {
		return readOnlyCurrentSounds;
	}

	/**
	 * Creates a new clip using the given sound data.
	 * 
	 * @param name
	 *            The name of the sound.
	 * @param start
	 *            If true the clip will be started directly.
	 * @return the future which will retrieve the new clip or null (If sound
	 *         does not exist, or headless mode, or...)
	 */
	public Future<Clip> playSound(final String name, final boolean start) {

		// No executor ?
		if (soundExecutor == null) {
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

					// Create a new clip
					final Clip clip = AudioSystem.getClip();

					// Open with binary data
					clip.open(ais);

					// Add into set
					currentSounds.add(clip);

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
								currentSounds.remove(clip);
							}
						}
					});

					// Start the clip ?
					if (start) {
						clip.start();
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

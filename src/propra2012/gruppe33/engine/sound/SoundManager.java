package propra2012.gruppe33.engine.sound;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import propra2012.gruppe33.engine.io.IoRoutines;
import propra2012.gruppe33.engine.resources.assets.AssetManager;

/**
 * 
 * @author Matthias Hesse/MrBumble
 * 
 */

public class SoundManager {

	private Map<String, byte[]> soundMap;
	private static final Object CLIP_OBJECT = new Object();
	private final ConcurrentMap<Clip, Object> currentSounds = new ConcurrentHashMap<Clip, Object>();

	public SoundManager() {
		// HashMap for all Sounds
		soundMap = new HashMap<String, byte[]>();
	}

	// Add a new Sound to the SoundMap
	public void addSound(String name, InputStream file) throws IOException,
			UnsupportedAudioFileException {

		byte[] AUDIO_DATA = IoRoutines.readFully(file);
		soundMap.put(name, AUDIO_DATA);

	}

	// Return all current Running Sounds
	public Set<Clip> currentSounds() {
		return currentSounds.keySet();
	}

	// Play a specific of the SoundMap by a Key-Name
	public Clip playSound(String name, boolean startDirect)
			throws UnsupportedAudioFileException, IOException,
			LineUnavailableException {
		AudioInputStream ais = AudioSystem
				.getAudioInputStream(new ByteArrayInputStream(soundMap
						.get(name)));
		final Clip clip = AudioSystem.getClip();
		clip.open(ais);

		currentSounds.put(clip, CLIP_OBJECT);

		clip.addLineListener(new LineListener() {

			@Override
			public void update(LineEvent event) {
				if (event.getType() == Type.STOP) {
					event.getLine().close();
				} else if (event.getType() == Type.CLOSE) {
					currentSounds.remove(clip);

				}
			}
		});
		if (startDirect) {
			clip.start();
		}

		return clip;
	}

	// Delete the complete SoundMap
	public void clearSoundMap() {
		soundMap.clear();

	}

	// Remove a specific Sound of the SoundMap
	public void removeSound(String name) {
		soundMap.remove(name);
	}

	// Returns the Byte Array of a specific Sound out of the SoundMap
	public byte[] getSoundByteArray(String name) {
		byte[] AUDIO_DATA = soundMap.get(name);
		return AUDIO_DATA;
	}

	// Returns a Clip of a specific Sound out of the SoundMap
	public Clip getSoundClip(String name) throws UnsupportedAudioFileException,
			IOException, LineUnavailableException {
		AudioInputStream ais = AudioSystem
				.getAudioInputStream(new ByteArrayInputStream(soundMap
						.get(name)));
		Clip clip = AudioSystem.getClip();
		clip.open(ais);
		return clip;
	}

	// Returns the length of a SoundFile out of the SoundMap in Milliseconds
	public long getSoundLenght(String name)
			throws UnsupportedAudioFileException, IOException,
			LineUnavailableException {
		long soundLength;
		AudioInputStream ais = AudioSystem
				.getAudioInputStream(new ByteArrayInputStream(soundMap
						.get(name)));
		Clip clip = AudioSystem.getClip();
		clip.open(ais);
		soundLength = clip.getMicrosecondLength() / 1000;

		return soundLength;

	}

}

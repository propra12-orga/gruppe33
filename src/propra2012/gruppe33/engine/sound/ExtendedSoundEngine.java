package propra2012.gruppe33.engine.sound;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * 
 * @author Matthias Hesse/MrBumble
 * 
 */

public class ExtendedSoundEngine {

	private static SourceDataLine audioLine;

	private AudioFormat format;

	private Map<String, AudioInputStream> soundMap;

	public ExtendedSoundEngine() {

	}

	public void addSound(String name, FileInputStream file)
			throws UnsupportedAudioFileException, IOException {
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(file);
			soundMap.put(name, ais);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void playSound() {

	}

	public static void main(String[] agrs) throws Exception {

		InputStream input = new FileInputStream(new File("c:/back.wav"));

		// File einlesen
		ByteArrayOutputStream audioFile = new ByteArrayOutputStream();
		byte[] copyBuffer = new byte[8192];
		int read;
		while ((read = input.read(copyBuffer)) != -1) {
			audioFile.write(copyBuffer, 0, read);
		}
		input.close();
		byte[] AUDIO_DATA = audioFile.toByteArray();

		AudioInputStream ais = AudioSystem
				.getAudioInputStream(new ByteArrayInputStream(AUDIO_DATA));

		Clip clip = AudioSystem.getClip();

		clip.open(ais);
		clip.start();

		Thread.sleep(1000);

		Clip clip2 = AudioSystem.getClip();

		clip2.open(AudioSystem.getAudioInputStream(new ByteArrayInputStream(
				AUDIO_DATA)));
		clip2.start();

		Thread.sleep(222222);

	}
}

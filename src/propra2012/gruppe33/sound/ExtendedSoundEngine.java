package propra2012.gruppe33.sound;

import java.applet.AudioClip;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
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



	public ExtendedSoundEngine(){

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

		byte[] data = null;
		AudioInputStream ais = AudioSystem
				.getAudioInputStream(new BufferedInputStream(
						new FileInputStream(new File("c:/back.wav"))));


		
		Clip clip = AudioSystem.getClip();
		
		clip.open(ais);
		clip.start();
		
		Thread.sleep(1000);
		
		Clip clip2 = AudioSystem.getClip();

		clip2.open(AudioSystem
				.getAudioInputStream(new BufferedInputStream(
						new FileInputStream(new File("c:/back.wav")))));
		clip2.start();
		
		Thread.sleep(222222);
		
		

	}
}

package propra2012.gruppe33.sound;

import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
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

	public static final int SAMPLE_RATE = 44100;
	private static final int BYTES_PER_SAMPLE = 2; // 16-bit audio
	private static final int BITS_PER_SAMPLE = 16; // 16-bit audio
	private static final double MAX_16_BIT = Short.MAX_VALUE; // 32,767
	private static final int SAMPLE_BUFFER_SIZE = 4096;

	private static SourceDataLine audioLine;

	private AudioFormat format;

	private Map<String, AudioInputStream> soundMap;

	private static byte[] buffer; // our internal buffer
	private static int bufferSize = 0; // number of samples currently in
										// internal buffer

	public ExtendedSoundEngine() throws LineUnavailableException {
		format = new AudioFormat((float) SAMPLE_RATE, BITS_PER_SAMPLE, 1, true,
				false);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		audioLine = (SourceDataLine) AudioSystem.getLine(info);
		audioLine.open(format, SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE);
		soundMap = new HashMap<String, AudioInputStream>();
	}

	public void closeLine() {
		audioLine.drain();
		audioLine.stop();
	}

	public void addSound(String name, FileInputStream file) throws UnsupportedAudioFileException, IOException {
		try{
		AudioInputStream ais =  AudioSystem.getAudioInputStream(file);
		soundMap.put(name, ais);
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}



	public double[] read(File file) {
		byte[] data = readByte(file);
		int N = data.length;
		double[] d = new double[N / 2];
		for (int i = 0; i < N / 2; i++) {
			d[i] = ((short) (((data[2 * i + 1] & 0xFF) << 8) + (data[2 * i] & 0xFF)))
					/ ((double) MAX_16_BIT);
		}
		return d;
	}

	public byte[] readByte(File file) {
		byte[] data = null;
		AudioInputStream ais = null;
		try {
			
            ais = AudioSystem.getAudioInputStream(file);
            System.out.println(ais);
			data = new byte[ais.available()];
			ais.read(data);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new RuntimeException("Could not read " + file);
		}
		
		return data;
	}

	public static void play(double[] input) {
		
		for (int i = 0; i < input.length; i++) {
			
			play(input[i]);
		}
	}

	public static void play(double in) {
		
		// clip if outside [-1, +1]
		if (in < -1.0)
			in = -1.0;
		if (in > +1.0)
			in = +1.0;

		// convert to bytes
		short s = (short) (MAX_16_BIT * in);
		buffer[bufferSize++] = (byte) s;
		buffer[bufferSize++] = (byte) (s >> 8); // little Endian

		// send to sound card if buffer is full
		if (bufferSize >= buffer.length) {
			audioLine.write(buffer, 0, buffer.length);
			bufferSize = 0;
		}
	}
	
	public static void main(String[] agrs) throws LineUnavailableException, UnsupportedAudioFileException, IOException{
		ExtendedSoundEngine bla = new ExtendedSoundEngine();
		File testFile = new File("C:\\back.wav");
		
		FileInputStream testStream = new FileInputStream(testFile);
		
		//bla.addSound("test", testStream);
		bla.play(bla.read(testFile));
	}
}

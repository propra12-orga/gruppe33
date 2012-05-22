package propra2012.gruppe33.sound;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @author Matthias Hesse
 *
 */
 
public class SimpleSoundEngine { 
	
	private Map<String, URL> soundMap;
	
	//Creates soundMap for all sound URLs
	public SimpleSoundEngine(){
		soundMap =new HashMap<String, URL>();
		
	}
	
	//This Method allows you to add new Sounds to the soundMap
	public void addSound(String name, URL url){
		
		soundMap.put(name, url);
		
	}
	
	//This Method reads the URL to an specific AudioClip out of the Soundmap creates a new Sound and plays it
	public void playSound(String name) throws MalformedURLException{
		if(soundMap.get(name) != null){
			AudioClip ac =  Applet.newAudioClip(soundMap.get(name));
			ac.play();	
		}else{
			throw new IllegalArgumentException("The name of the sound is not valid");
		}
	} 
} 
 
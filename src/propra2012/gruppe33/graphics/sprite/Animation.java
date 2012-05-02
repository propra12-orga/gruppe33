package propra2012.gruppe33.graphics.sprite;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


//This class represents a single Animation
public class Animation {


	private List<BufferedImage> images = new ArrayList<BufferedImage>();
	private String name;
	private int animationLength, animationStep;
	private long timeStamp, timeDiff, animationTime, timePerImage;

	public Animation(String name, List<BufferedImage> images, long timePerImage) {
		//Read the number of Images of this animation
		animationLength = images.size();
		//Calculate the time for the whole animation
		animationTime = animationLength * timePerImage;
		this.timePerImage = timePerImage;
		
		//Set the animationStepCounter to zero
		animationStep = 0;

		//Read the list of SubImages
		this.images = images;

		//Set the TimeStep to the actual time
		timeStamp = System.currentTimeMillis();

	}
	
	
	//This Method returns the actual Image of this animation by the actual timestamp
	public BufferedImage getAnimationImage() {

		//Read the actual time
		long time = System.currentTimeMillis();

		//Calcutelate if the time difference from the last Step is higher then the timePer Image
		//If yes the timeStamp will be incresed
		if (time - timeStamp > timePerImage) {
			animationStep++;
			timeStamp = time;
		}
		
		
		//If the animation is at the end the animation is resetted
		if (animationStep >= images.size()) {
			animationStep = 0;
			
		}

		
		//Return the image of the actual Animation Step
		return images.get(animationStep);

	}
	
	//This Method resets the current Animation
	public void resetAnimation(){
		this.animationStep = 0;
	}

}

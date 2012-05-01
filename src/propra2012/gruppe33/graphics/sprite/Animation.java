package propra2012.gruppe33.graphics.sprite;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Animation {
	
	private List<BufferedImage> images = new ArrayList<BufferedImage>();
	private String name;
	private Point[] points;
	private int animationLength,animationStep;
	private long timeStamp,timeDiff,animationTime,timePerImage;
	
	public Animation(String name, List<BufferedImage> images,long timePerImage,Point... points){
		animationLength = points.length;
		animationTime = animationLength * timePerImage;
		this.timePerImage = timePerImage;
		animationStep = 0;
		
		
		
		
	}
	
	public BufferedImage getAnimationImage(){
		timeDiff = System.currentTimeMillis() - timeStamp;
		if(animationStep + timeDiff/timePerImage > animationLength){
			animationStep = 0;
		}
		animationStep+= timeDiff/timePerImage;
		System.out.println(animationStep);
		
		
		
		
		timeStamp = System.currentTimeMillis();
		return null;
		
	}
	


}

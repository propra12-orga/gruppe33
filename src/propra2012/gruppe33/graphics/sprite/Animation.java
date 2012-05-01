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
	private long timeStamp,time,animationTimeLength;
	
	public Animation(String name,long time, Point... points){
		this.name = name;
		this.points = points;
		animationLength = points.length;
		this.time = time;
		animationTimeLength = time * this.points.length;
		animationStep = 0;
		
		
		
		
		
	}
	
	public BufferedImage getAnimationImage(){
		long timeDiff = System.currentTimeMillis() - timeStamp;
		if(animationStep != animationLength){
			animationStep+= timeDiff/time;
		}else{
			animationStep = 0;
		}
		
		
		
		
		
		
		timeStamp = System.currentTimeMillis();
		return images.get(animationStep);
		
	}
	


}

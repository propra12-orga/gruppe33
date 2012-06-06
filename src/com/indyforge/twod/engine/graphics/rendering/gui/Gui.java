package com.indyforge.twod.engine.graphics.rendering.gui;

import java.awt.image.BufferedImage;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.RenderedImage;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;



public class Gui {
	
	private RenderedImage backGround, cursor;
	
	private Vector2f[] pointList;
	
	private int menuPos, menuEntries;

	public Gui (RenderedImage back, RenderedImage cursor, Vector2f[] points) throws Exception {
		this.backGround = back;
		this.pointList = points;
		this.cursor = cursor;
		menuPos = 0;
		menuEntries = pointList.length;

	}
	
	
	//Navigate up and down in a Menu
	//if value is false you navigate up
	//if value is true you navigate down
	//Only for vertical menu
	public void updatePos(boolean value){
		if(value){
			if (menuPos == menuEntries-1) {
				menuPos = 0;
			}else{
				menuPos++;
			}
			
		}else{
			if (menuPos == 0) {
				menuPos = menuEntries-1;
			}else{
				menuPos--;
			}
		}
		
	}
	
	public RenderedImage updateCursor(){
		cursor.position().set(this.getMenuVector());
		
		return cursor;
	}
	
	//Retun a 2D vector of the actual menu Position
	public Vector2f getMenuVector(){
		Vector2f menuVector = pointList[menuPos];
		
		return menuVector;
		
		
	}
}

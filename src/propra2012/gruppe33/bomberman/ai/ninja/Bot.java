package propra2012.gruppe33.bomberman.ai.ninja;

import java.awt.Point;
import java.util.ArrayList;

import propra2012.gruppe33.bomberman.ai.AIControl;
import propra2012.gruppe33.bomberman.ai.AIProcessor;

public class Bot implements AIProcessor {

	//private Pathfinder[][] map;
	
	@Override
	public void process(float timeSinceLastFrame, AIControl aiControl) {

	}

	private void pathfind(Point[][] field, Point start, Point goal) {
		ArrayList<Pathfinder> crowd = new ArrayList<Pathfinder>();
		Pathfinder[][] map = new Pathfinder[field.length][field[0].length];

		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				map[i][j].setDistance(1000);
				crowd.add(map[i][j]);
			}
		}
		map[start.y][start.x].setDistance(0);

	}
	
	private void dijkstra(Point[][] field, Point start, ArrayList<Pathfinder> crowd) {
		
	}
}

package propra2012.gruppe33.bomberman.ai.ninja;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;

import propra2012.gruppe33.bomberman.ai.AIControl;
import propra2012.gruppe33.bomberman.ai.AIProcessor;

public class Bot implements AIProcessor {

	private Pathfinder[][] map;
	private boolean initialized;
	private LinkedList<Pathfinder> path;

	@Override
	public void process(float timeSinceLastFrame, AIControl aiControl) {

		if (!initialized) {
			map = new Pathfinder[aiControl.fields().length - 1][aiControl
					.fields()[0].length - 1];
			initialize(aiControl.fields());
			initialized = true;
			for (int y = 0; y < map.length; y++) {
				for (int x = 0; x < map.length; x++) {
					if (aiControl.fields()[y][x] != null) {
						map[y][x] = new Pathfinder();
					}
				}
			}
			for (int y = 0; y < map.length; y++) {
				for (int x = 0; x < map[0].length; x++) {
					if ((y - 1 >= 0) && (map[y - 1][x] != null)) {
						map[y][x].setNorth(map[y - 1][x]);
						map[y][x].setSpeedNorth(aiControl.edgeWeight(map[y][x],
								map[y - 1][x]));
					} else {
						map[y][x].setNorth(map[y][x]);
					}
					if ((x + 1 < map[y].length) && (map[y][x + 1] != null)) {
						map[y][x].setEast(map[y][x + 1]);
						map[y][x].setSpeedEast(aiControl.edgeWeight(map[y][x],
								map[y][x + 1]));
					} else {
						map[y][x].setEast(map[y][x]);
					}
					if ((y + 1 < map.length) && (map[y + 1][x] != null)) {
						map[y][x].setSouth(map[y + 1][x]);
						map[y][x].setSpeedSouth(aiControl.edgeWeight(map[y][x],
								map[y + 1][x]));
					} else {
						map[y][x].setSouth(map[y][x]);
					}
					if ((x - 1 >= 0) && (map[y][x - 1] != null)) {
						map[y][x].setWest(map[y][x - 1]);
						map[y][x].setSpeedWest(aiControl.edgeWeight(map[y][x],
								map[y][x - 1]));
					} else {
						map[y][x].setWest(map[y][x]);
					}

				}
			}
		}

	}

	public void initialize(int[][][] fields) {
		for (int y = 0; y < fields.length; y++) {
			for (int x = 0; x < fields[0].length; x++) {
				if (fields[y][x] != null) {
					// map[y][x] = new Pathfinder;
				}
			}
		}
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
	
	private LinkedList getPath() {
		path = new LinkedList<Pathfinder>();
		
		return path;
	}
}

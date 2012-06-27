package propra2012.gruppe33.bomberman.ai.ninja;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;

import propra2012.gruppe33.bomberman.ai.AIControl;
import propra2012.gruppe33.bomberman.ai.AIProcessor;

public class Bot implements AIProcessor {

	/**
	 * the abstract data type Pathfinder is used to create a grid structure. As
	 * a result the dijkstra algorithm can be easily implemented. map is the
	 * base frame for the grid.
	 */
	private Pathfinder[][] map;

	/**
	 * Is only used to determine whether the bot was already initialized or not.
	 * Only used in process
	 */
	private boolean initialized;

	/**
	 * The destined path on which the bot is walking. Is used by
	 * AIControl.setPath and AIControl.getPath.
	 */
	private Point[] path;

	/**
	 * The method that actually guides the bot.
	 */
	@Override
	public void process(float timeSinceLastFrame, AIControl aiControl) {

		/*
		 * Since the class only needs to initialized once. This submethod should
		 * only be called once for every bot.
		 * 
		 * It initializes map, chains the connections between the pathfinders
		 * and sets the speed.
		 */
		if (!initialized) {
			map = new Pathfinder[aiControl.fields().length - 1][aiControl
					.fields()[0].length - 1];
			initialized = true;
			for (int y = 0; y < map.length; y++) {
				for (int x = 0; x < map.length; x++) {
					if (aiControl.fields()[y][x] != null) {
						map[y][x] = new Pathfinder(x, y);
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

		/*
		 * from here on: further implementation
		 * 
		 * checkfields implementieren (checkt wo items, bomben, spieler in
		 * näherere umgebung um den bot liegen)
		 * 
		 * checkbombpath implementieren (checkt ob auf dem aktuellen path bomben
		 * liegen, die möglicherweise den bot töten könnten
		 */

	}

	// public void initialize(int[][][] fields) {
	// for (int y = 0; y < fields.length; y++) {
	// for (int x = 0; x < fields[0].length; x++) {
	// if (fields[y][x] != null) {
	// // map[y][x] = new Pathfinder;
	// }
	// }
	// }
	// }
	//
	// private void pathfind(Point[][] field, Point start, Point goal) {
	// ArrayList<Pathfinder> crowd = new ArrayList<Pathfinder>();
	// Pathfinder[][] map = new Pathfinder[field.length][field[0].length];
	//
	// for (int i = 0; i < map.length; i++) {
	// for (int j = 0; j < map[0].length; j++) {
	// map[i][j].setDistance(1000);
	// crowd.add(map[i][j]);
	// }
	// }
	// map[start.y][start.x].setDistance(0);
	//
	// }
	//
	// private LinkedList getPath(Pathfinder goal) {
	// path = new LinkedList<Pathfinder>();
	//
	// return path;
	// }
}

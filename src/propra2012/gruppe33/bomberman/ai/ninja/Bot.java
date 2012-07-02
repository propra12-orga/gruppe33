package propra2012.gruppe33.bomberman.ai.ninja;

import java.awt.Point;

import propra2012.gruppe33.bomberman.ai.AIControl;
import propra2012.gruppe33.bomberman.ai.AIProcessor;

public class Bot implements AIProcessor {

	/**
	 * Is now serializable.
	 */
	private static final long serialVersionUID = 1L;

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
	 * The position the bot is standing on. May only be accommodated by the
	 * method positionChanged().
	 */
	private Point position;

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
			position = new Point(0, 0);
			map = new Pathfinder[aiControl.fields().length][aiControl.fields()[0].length];
			initialized = true;
			for (int y = 0; y < map.length; y++) {
				for (int x = 0; x < map[0].length; x++) {
					if (aiControl.fields()[y][x] != null) {
						map[y][x] = new Pathfinder(x, y);
					}
				}
			}
			for (int y = 0; y < map.length; y++) {
				for (int x = 0; x < map[0].length; x++) {

					if (map[y][x] == null) {
						continue;
					}

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

		if ((positionChanged(aiControl.activePosition()))
				&& (aiControl.hasFieldChanged()) && aiControl.isResting()) {
			reinitialize();
			map[position.y][position.x].dijkstra(0);

			// checks where is aen enemy player and moves the bot to this
			// position.

			for (int y = 0; y < aiControl.fields().length; y++) {
				for (int x = 0; x < aiControl.fields()[y].length; x++) {

					if (aiControl.fields()[x][y] != null) {
						for (int z = 0; z < aiControl.fields()[x][y].length; z++) {
							if (aiControl.fields()[x][y][z] == AIControl.PLAYER) {
								aiControl.setPath(getPath(map[y][x]));
								return;
							}
						}
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

	/**
	 * After a successful execution of dijkstra algorithm distance and count may
	 * be wrong for the next one. This method resets them on default values.
	 */
	private void reinitialize() {
		for (int y = 0; y < map.length; y++) {
			for (int x = 0; x < map[0].length; x++) {
				if (map[y][x] == null) {
					continue;
				}

				map[y][x].setVisited(false);
				map[y][x].setPredecessor(null);
				map[y][x].setDistance(1000);
				map[y][x].setCount(0);
			}
		}
	}

	/**
	 * Generates the path from the "goal pathfinder" to the "start pathfinder"
	 * by using the number of steps needed to get there.
	 * 
	 * @param goal
	 *            This Pathfinder is thelocation that the bot should walk to
	 *            from his current location.
	 * @return the path from current location to the goal location.
	 */
	private Point[] getPath(Pathfinder goal) {
		int i = 0;
		path = new Point[goal.getCount()];

		while (!goal.getPredecessor().equals(position)) {
			path[path.length - i - 1] = goal;
			goal = goal.getPredecessor();
			i++;
		}
		path[0] = goal;
		return path;
	}

	/**
	 * Controls whether the position on the map of the bot has changed since the
	 * last call.
	 * 
	 * @param activePosition
	 *            the actual position of the bot. Can only be determined by
	 *            aiControl. May be differ from this.position.
	 * @return true if the position has changed or false if not.
	 */
	private Boolean positionChanged(Point activePosition) {
		if (!position.equals(activePosition)) {
			position = activePosition;
			return true;
		} else {
			return false;
		}
	}

	// Alte Funktionen die evtl. noch brauchbar sind, werden vor abschluss
	// gelöscht. Ebenso wird die gesammte Dokumentation vor abschluss auf
	// Englisch umgestellt!

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
}

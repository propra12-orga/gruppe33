package propra2012.gruppe33.bomberman.ai.ninja;

import java.awt.Point;

/**
 * This class is a utility abstract data type and uses the Dijkstra algorithm to
 * get the closest path to all other pathfinders in the grid from a certain
 * start parthfinder.
 * 
 * @author Malte
 * 
 */
public class Pathfinder extends Point {

	/**
	 * Is now serializable.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This variable points at another pathfinder. If the dijkstra algorithm has
	 * checked all pathfinders one simply can follow the path that is pointed by
	 * the "goal pathfinder" to the "start pathfinder".
	 */
	private Pathfinder predecessor;

	/**
	 * The distance from the "start pathfinder" to this. May be not correct if
	 * dijkstra has not completely run jet. Mainly used in dijkstra.
	 */
	private float distance;

	/**
	 * The number of pathfinders that are needed to reach this from the
	 * "start pathfinder". Like distance it may be not correct if dijkstra is
	 * not complete.
	 */
	private int count;

	/**
	 * Used in dijkstra to check whether the pathfinder already were checked by
	 * dijkstra.
	 */
	private boolean visited;

	/**
	 * Simply indicates what speed is used. It get's accommodated by
	 * lowestUncheckedConnection and is used by dijkstra. Thus it does not need
	 * Getter and Setter.
	 */
	private char indicator;

	/*
	 * The following variables are used to connect the grid and point on another
	 * pathfinder or determine the speed in the given direction. If their is no
	 * pathfinder in the given direction, this points on itself and the speed is
	 * 0. in the further documentary i will refer to them as connections.
	 */
	private Pathfinder north;
	private float speedNorth = java.lang.Float.MAX_VALUE;
	private Pathfinder east;
	private float speedEast = java.lang.Float.MAX_VALUE;
	private Pathfinder south;
	private float speedSouth = java.lang.Float.MAX_VALUE;
	private Pathfinder west;
	private float speedWest = java.lang.Float.MAX_VALUE;

	/**
	 * Default constructor. Sets the Distance to 1000, because this value should
	 * never be reached in the game and is therefore used as a default value.
	 * Before running dijkstra distance should always be set to 1000.
	 */
	public Pathfinder(int x, int y) {
		this.distance = 1000;
		this.x = x;
		this.y = y;
		this.count = 0;
	}

	/**
	 * Just a lot of if-checks whether their is an unchecked Connection to
	 * another pathfinder. Used in dijkstra.
	 * 
	 * @return this if their is no unchecked connection. Else return the
	 *         connection with the lowest speed.
	 */
	public Pathfinder lowestUncheckedConnection() {

//		System.out.println(speedNorth);
//		System.out.println(speedEast);
//		System.out.println(speedSouth);
//		System.out.println(speedWest);

		if ((north != this) && (!north.isVisited())
				&& (speedNorth <= speedEast) && (speedNorth <= speedSouth)
				&& (speedNorth <= speedWest)) {
			indicator = 'n';
			return north;
		} else if ((east != this) && (!east.isVisited())
				&& (speedEast <= speedNorth) && (speedEast <= speedSouth)
				&& (speedEast <= speedWest)) {
			indicator = 'e';
			return east;
		} else if ((south != this) && (!south.isVisited())
				&& (speedSouth <= speedNorth) && (speedSouth <= speedEast)
				&& (speedSouth <= speedWest)) {
			indicator = 's';
			return south;
		} else if ((west != this) && (!west.isVisited())
				&& (speedWest <= speedNorth) && (speedWest <= speedEast)
				&& (speedWest <= speedSouth)) {
			indicator = 'w';
			return west;
		} else {
			return this;
		}
	}

	/**
	 * This method is a recursive implementation of the dijkstra algorithm. It
	 * checks all connections of this, whether their distance to this plus the
	 * distance from the "start pathfinder" is lower than their current
	 * distance. If thats the case it, changes their predecessor to this and
	 * accommodates the speed.
	 * 
	 * @param wayTillHere
	 *            describes the distance of the "start pathfinder" till the
	 *            pathfinder from where dijkstra is started. Dijkstra may be
	 *            started with the value of wayTillHere=0.
	 */
	public void dijkstra(float wayTillHere) {
		// First: Set visited to true. Otherwise lowestUncheckedConnection could
		// find this by mistake.
		visited = true;

		// The Pathfinder that will be the next for dijkstra.
		Pathfinder next = this.lowestUncheckedConnection();

		float speed = 0;

		// Checks all connections. Up to four times.
		while (next != this) {
			switch (indicator) {
			case 'n':
				speed = wayTillHere + speedNorth;
				break;
			case 'e':
				speed = wayTillHere + speedEast;
				break;
			case 's':
				speed = wayTillHere + speedSouth;
				break;
			case 'w':
				speed = wayTillHere + speedWest;
				break;
			}

			if (next.getDistance() > speed) {
				next.setDistance(speed);
				next.setPredecessor(this);
				next.setCount(count + 1);
			}
			next.dijkstra(speed);
			next = this.lowestUncheckedConnection();
		}
	}

	public int count(Pathfinder x) {
		return 0;
	}

	/*
	 * __________________________________________________________________________________________
	 * From here on only Getters and Setters for variables.
	 */

	public Pathfinder getPredecessor() {
		return predecessor;
	}

	public void setPredecessor(Pathfinder predecessor) {
		this.predecessor = predecessor;
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public Pathfinder getNorth() {
		return north;
	}

	public void setNorth(Pathfinder north) {
		this.north = north;
	}

	public float getSpeedNorth() {
		return speedNorth;
	}

	public void setSpeedNorth(float speedNorth) {
		this.speedNorth = speedNorth;
	}

	public Pathfinder getEast() {
		return east;
	}

	public void setEast(Pathfinder east) {
		this.east = east;
	}

	public float getSpeedEast() {
		return speedEast;
	}

	public void setSpeedEast(float speedEast) {
		this.speedEast = speedEast;
	}

	public Pathfinder getSouth() {
		return south;
	}

	public void setSouth(Pathfinder south) {
		this.south = south;
	}

	public float getSpeedSouth() {
		return speedSouth;
	}

	public void setSpeedSouth(float speedSouth) {
		this.speedSouth = speedSouth;
	}

	public Pathfinder getWest() {
		return west;
	}

	public void setWest(Pathfinder west) {
		this.west = west;
	}

	public float getSpeedWest() {
		return speedWest;
	}

	public void setSpeedWest(float speedWest) {
		this.speedWest = speedWest;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}

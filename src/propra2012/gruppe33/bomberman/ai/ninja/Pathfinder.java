package propra2012.gruppe33.bomberman.ai.ninja;

import java.awt.Point;

public class Pathfinder {
	private Pathfinder predecessor;
	private int distance;
	private boolean visited;
	private Pathfinder north;
	private int speedNorth;
	private Pathfinder east;
	private int speedEast;
	private Pathfinder south;
	private int speedSouth;
	private Pathfinder west;
	private int speedWest;
	private char indicator;

	public Pathfinder() {
		this.distance = 1000;
	}

	public Pathfinder lowestUncheckedConnection() {
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
		} else
			return this;
	}

	public void dijkstra(int wayTillHere) {
		Pathfinder next = this.lowestUncheckedConnection();
		int speed = 0;
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
			}
			next.dijkstra(speed);
			next = this.lowestUncheckedConnection();
		}
		visited = true;
	}

	public Pathfinder getPredecessor() {
		return predecessor;
	}

	public void setPredecessor(Pathfinder predecessor) {
		this.predecessor = predecessor;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
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

	public int getSpeedNorth() {
		return speedNorth;
	}

	public void setSpeedNorth(int speedNorth) {
		this.speedNorth = speedNorth;
	}

	public Pathfinder getEast() {
		return east;
	}

	public void setEast(Pathfinder east) {
		this.east = east;
	}

	public int getSpeedEast() {
		return speedEast;
	}

	public void setSpeedEast(int speedEast) {
		this.speedEast = speedEast;
	}

	public Pathfinder getSouth() {
		return south;
	}

	public void setSouth(Pathfinder south) {
		this.south = south;
	}

	public int getSpeedSouth() {
		return speedSouth;
	}

	public void setSpeedSouth(int speedSouth) {
		this.speedSouth = speedSouth;
	}

	public Pathfinder getWest() {
		return west;
	}

	public void setWest(Pathfinder west) {
		this.west = west;
	}

	public int getSpeedWest() {
		return speedWest;
	}

	public void setSpeedWest(int speedWest) {
		this.speedWest = speedWest;
	}

}
